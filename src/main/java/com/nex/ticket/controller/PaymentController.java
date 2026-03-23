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
        ChargeStore.ChargeRecord record = ChargeStore.getCharge(id);

        try {
            Request<Charge> markAsPaiddRequest = new Charge.MarkAsPaidRequestBuilder(id)
                    .build();
            omiseClient.sendRequest(markAsPaiddRequest);
            ChargeStore.updateChargeStatus(id, status);
        } catch (Exception e) {
            System.err.println("Fail to mark Omise charge as paid " + id + ": "
                    + e.getMessage());
        }
        if (record == null)
            return ResponseEntity.notFound().build();

        if (record.getStatus() != "success") {
            // Cancellation logic: same routeId and seat
            PaymentBodyBao successPbao = record.getPaymentBodyBao();
            if (successPbao != null && successPbao.getRoute() != null && successPbao.getRoute().getRouteId() != null
                    && successPbao.getSeat() != null) {

                String successRouteId = successPbao.getRoute().getRouteId();
                Set<String> successSeatIds = successPbao.getSeat().stream()
                        .map(s -> s.getId())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                List<ChargeStore.ChargeRecord> otherCharges = ChargeStore.getAllCharges().stream()
                        .filter(c -> !c.getId().equals(record.getId()))
                        .filter(c -> c.getStatus().equalsIgnoreCase("Pending"))
                        .toList();
                for (ChargeStore.ChargeRecord chrg : otherCharges) {
                    PaymentBodyBao otherPbao = chrg.getPaymentBodyBao();
                    if (otherPbao != null && otherPbao.getRoute() != null
                            && successRouteId.equals(otherPbao.getRoute().getRouteId())
                            && otherPbao.getSeat() != null) {

                        boolean hasOverlap = otherPbao.getSeat().stream()
                                .map(s -> s.getId())
                                .anyMatch(successSeatIds::contains);
                        System.out.println("has overlap : " + hasOverlap);
                        if (hasOverlap) {
                            ChargeStore.updateChargeStatus(chrg.getId(), "failed");

                            // Send cancel/reverse to Omise if not a mock ID
                            String otherId = chrg.getId();
                            if (otherId != null && !otherId.startsWith("test_chrg_")) {
                                try {
                                    Request<Charge> markAsFailedRequest = new Charge.MarkAsFailedRequestBuilder(
                                            otherId)
                                            .build();
                                    omiseClient.sendRequest(markAsFailedRequest);
                                    System.err.println("Success to mark Omise charge as failed " + otherId);
                                } catch (Exception e) {
                                    System.err.println("Failed to mark Omise charge as failed " + otherId + ": "
                                            + e.getMessage());
                                }
                            }
                        }
                    } else {
                        System.out.println("No overlap for charge " + chrg.getId());
                    }
                }
                // .forEach(other -> {

                // });
            }

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
            ChargeStore.addCharge(charge.getId(), source.getId(), (long) amountBaht * 100, "THB", null,
                    paymentDetailBao);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("chargeId", charge.getId());
            response.put("qrCodeUrl", qrUrl);
            response.put("status", charge.getStatus().toString().toLowerCase());
            response.put("expiresAt", "2026-03-16T21:00:00Z");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String mockId = ChargeStore.addCharge(null, "mock-source-" + type.toString(), (long) amountBaht * 100,
                    "THB",
                    null, paymentDetailBao);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("chargeId", mockId);
            response.put("qrCodeUrl", null);
            response.put("status", "pending");
            response.put("message", "Using mock payment (test mode fallback)");
            return ResponseEntity.ok(response);
        }
    }

    // ─── POST /api/payment/cancel/:id ──────────────────────────────────────────

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Map<String, Object>> cancelPayment(@PathVariable(name = "id") String id) {
        ChargeStore.ChargeRecord record = ChargeStore.getCharge(id);
        if (record == null) {
            return ResponseEntity.notFound().build();
        }

        // Update local status to failed
        ChargeStore.updateChargeStatus(id, "failed");

        Map<String, Object> response = new LinkedHashMap<>();
        // Send mark_as_failed to Omise if not a mock ID
        if (id != null && !id.startsWith("test_chrg_")) {
            try {
                Request<Charge> markAsFailedRequest = new Charge.MarkAsFailedRequestBuilder(id).build();
                omiseClient.sendRequest(markAsFailedRequest);

                response.put("success", true);
                response.put("message", "Payment cancelled successfully");
                response.put("chargeId", id);
            } catch (Exception e) {
                System.err.println(
                        "User requested cancellation: failed to mark Omise charge as failed " + id + ": "
                                + e.getMessage());
                response.put("success", false);
                response.put("message", e.getMessage());
                response.put("chargeId", id);
            }
        }

        return ResponseEntity.ok(response);
    }
}