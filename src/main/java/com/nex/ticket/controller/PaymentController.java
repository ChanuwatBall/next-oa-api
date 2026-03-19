package com.nex.ticket.controller;

import co.omise.Client;
import co.omise.models.Charge;
import co.omise.models.Source;
import co.omise.models.SourceType;
import co.omise.requests.Request;
import com.nex.ticket.bao.ChargeDto;
import com.nex.ticket.bao.PaymentBodyBao;
import com.nex.ticket.service.ChargeStore;
import com.nex.ticket.service.LineMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Domain 5 – Payment
 * POST /api/payment/qr
 * POST /api/payment/alipay-qr
 * POST /api/payment/wechat-pay
 * GET /api/payment/transaction/:id
 */
@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PaymentController {

    @Autowired
    private Client omiseClient;

    @Autowired
    private LineMessageService lineMessageService;

    // ─── POST /api/payment/qr ──────────────────────────────────────────────────

    @PostMapping("/qr")
    public ResponseEntity<Map<String, Object>> createQrPayment(@RequestParam(name = "amount") int amount,
            @RequestBody PaymentBodyBao paymentBodyBao) {
        return createOmiseCharge(amount, SourceType.PromptPay, paymentBodyBao);
    }

    // ─── POST /api/payment/alipay-qr ───────────────────────────────────────────

    @PostMapping("/alipay-qr")
    public ResponseEntity<Map<String, Object>> createAlipayPayment(@RequestParam(name = "amount") int amount,
            @RequestBody PaymentBodyBao paymentBodyBao) {
        return createOmiseCharge(amount, SourceType.Alipay, paymentBodyBao);
    }

    // ─── POST /api/payment/wechat-pay ──────────────────────────────────────────

    @PostMapping("/wechat-pay")
    public ResponseEntity<Map<String, Object>> createWechatPayment(@RequestParam(name = "amount") int amount,
            @RequestBody PaymentBodyBao paymentBodyBao) {
        return createOmiseCharge(amount, SourceType.WeChatPay, paymentBodyBao);
    }

    // ─── GET /api/payment/transaction/:id ──────────────────────────────────────

    @GetMapping("/transaction/{id}")
    public ResponseEntity<Map<String, Object>> getTransaction(@PathVariable(name = "id") String id) {
        try {
            Request<Charge> request = new Charge.GetRequestBuilder(id).build();
            Charge charge = omiseClient.sendRequest(request);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("chargeId", charge.getId());
            response.put("status", charge.getStatus().toString().toLowerCase());
            response.put("amount", charge.getAmount());
            response.put("currency", charge.getCurrency());

            if (charge.getPaidAt() != null) {
                response.put("paidAt", charge.getPaidAt().toString());
            }

            // Nest for compatibility
            Map<String, Object> chargeNest = new LinkedHashMap<>();
            chargeNest.put("id", charge.getId());
            chargeNest.put("status", charge.getStatus().toString().toLowerCase());
            chargeNest.put("amount", charge.getAmount());
            chargeNest.put("currency", charge.getCurrency());
            if (charge.getSource() != null && charge.getSource().getScannableCode() != null) {
                Map<String, Object> source = new LinkedHashMap<>();
                Map<String, Object> scannable = new LinkedHashMap<>();
                Map<String, Object> image = new LinkedHashMap<>();
                image.put("download_uri", charge.getSource().getScannableCode().getImage().getDownloadUri());
                scannable.put("image", image);
                source.put("scannable_code", scannable);
                chargeNest.put("source", source);
            }
            response.put("charge", chargeNest);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ChargeStore.ChargeRecord record = ChargeStore.getCharge(id);
            if (record != null) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("chargeId", record.getId());
                response.put("status", record.getStatus().toLowerCase());
                response.put("amount", record.getAmount());
                return ResponseEntity.ok(response);
            }
            return ResponseEntity.notFound().build();
        }
    }

    // ======================== Charge Management Endpoints ========================

    @GetMapping("/charges")
    public ResponseEntity<List<ChargeDto>> getAllCharges() {
        List<ChargeStore.ChargeRecord> records = ChargeStore.getAllCharges();
        List<ChargeDto> dtos = records.stream()
                .map(r -> new ChargeDto(r.getId(), r.getAmount(), r.getCurrency(), r.getStatus(), r.getSourceId(),
                        r.getCreatedAt(), null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/charges/{id}/status")
    public ResponseEntity<ChargeDto> updateChargeStatus(
            @PathVariable(name = "id") String id,
            @RequestParam(name = "status") String status) {
        ChargeStore.updateChargeStatus(id, status);
        ChargeStore.ChargeRecord record = ChargeStore.getCharge(id);
        if (record == null)
            return ResponseEntity.notFound().build();

        if (record.getStatus().equalsIgnoreCase("success")) {
            lineMessageService.sendCarouselMessage(record.getLineUserId());
        } else if (record.getStatus().equalsIgnoreCase("failed")) {
            lineMessageService.sendLineMessage(record.getLineUserId(), "Your charge has failed.");
        }

        ChargeDto dto = new ChargeDto(record.getId(), record.getAmount(), record.getCurrency(), record.getStatus(),
                record.getSourceId(), record.getCreatedAt(), null);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/charges/clear")
    public ResponseEntity<Map<String, String>> clearAllCharges() {
        ChargeStore.clearAll();
        return ResponseEntity.ok(Collections.singletonMap("message", "All charges cleared"));
    }

    // ─── Internal Helper ──────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> createOmiseCharge(int amountBaht, SourceType type,
            PaymentBodyBao paymentDetailBao) {
        try {
            Request<Source> sourceReq = new Source.CreateRequestBuilder()
                    .type(type)
                    .amount((long) amountBaht * 100)
                    .currency("THB")
                    .build();
            Source source = omiseClient.sendRequest(sourceReq);

            Request<Charge> chargeReq = new Charge.CreateRequestBuilder()
                    .amount((long) amountBaht * 100)
                    .currency("THB")
                    .source(source.getId())
                    .build();
            Charge charge = omiseClient.sendRequest(chargeReq);

            String qrUrl = null;
            if (charge.getSource() != null && charge.getSource().getScannableCode() != null) {
                qrUrl = charge.getSource().getScannableCode().getImage().getDownloadUri();
            }

            // Sync with memory store
            ChargeStore.addCharge(source.getId(), (long) amountBaht * 100, "THB", null);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("chargeId", charge.getId());
            response.put("qrCodeUrl", qrUrl);
            response.put("status", charge.getStatus().toString().toLowerCase());
            response.put("expiresAt", "2026-03-16T21:00:00Z");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String mockId = ChargeStore.addCharge("mock-source-" + type.toString(), (long) amountBaht * 100, "THB",
                    null);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("chargeId", mockId);
            response.put("qrCodeUrl", null);
            response.put("status", "pending");
            response.put("message", "Using mock payment (test mode fallback)");
            return ResponseEntity.ok(response);
        }
    }
}