package com.nex.ticket.controller;

import co.omise.Client;
import co.omise.models.Charge;
import co.omise.models.Source;
import co.omise.models.SourceType;
import co.omise.requests.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nex.ticket.bao.PaymentBodyBao;
import com.nex.ticket.bao.PaymentResponseBao;
import com.nex.ticket.bao.TransactionResponseBao;
import com.nex.ticket.bao.ErrorResponseBao;
import com.nex.ticket.bao.ChargeDto;
import com.nex.ticket.bao.ErrorResponse;
import com.nex.ticket.service.ChargeStore;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private Client omiseClient;

    @PostMapping("/qr")
    public ResponseEntity<PaymentResponseBao> createQrPayment(@RequestParam Integer amount) {
        try {
            Request<Source> sourceRequest = new Source.CreateRequestBuilder()
                    .type(SourceType.PromptPay)
                    .amount(amount * 100L) // Convert to satang
                    .currency("THB")
                    .build();

            Source source = omiseClient.sendRequest(sourceRequest);

            // Create charge with the source
            Request<Charge> chargeRequest = new Charge.CreateRequestBuilder()
                    .amount(amount * 100L) // Convert to satang
                    .currency("THB")
                    .source(source.getId())
                    .build();

            Charge charge = omiseClient.sendRequest(chargeRequest);

            // Store charge in memory
            String storedChargeId = ChargeStore.addCharge(source.getId(), source.getAmount(), source.getCurrency());

            PaymentResponseBao response = new PaymentResponseBao();
            response.setSourceId(source.getId());
            response.setChargeId(storedChargeId);
            
            // Check if scannable code exists
            System.out.println("Source scannable code: " + source.getScannableCode());
            
            if (charge != null && charge.getSource() != null && charge.getSource().getScannableCode() != null) {
                response.setQrCodeUrl(charge.getSource().getScannableCode().getImage().getDownloadUri());
            } else {
                response.setQrCodeUrl(null);
            }
            
            response.setAmount(source.getAmount());
            response.setCurrency(source.getCurrency());
            response.setChargeStatus(charge.getStatus().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error creating QR payment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/alipay-qr")
    public ResponseEntity<PaymentResponseBao> createAliPayQrPayment(@RequestParam Integer amount) {
        try {
            // Try to create in Omise first
            String chargeId = null;
            String sourceId = "alipay-test-mode";
            
            try {
                Request<Source> sourceRequest = new Source.CreateRequestBuilder()
                        .type(SourceType.Alipay) // Try AliPay QR
                        .amount(amount * 100L)
                        .currency("THB")
                        .build();

                Source source = omiseClient.sendRequest(sourceRequest);
                sourceId = source.getId();

                // Create charge with the source
                Request<Charge> chargeRequest = new Charge.CreateRequestBuilder()
                        .amount(amount * 100L)
                        .currency("THB")
                        .source(sourceId)
                        .build();

                Charge charge = omiseClient.sendRequest(chargeRequest);
                chargeId = charge.getId();
                
                System.out.println("AliPay charge created in Omise: " + chargeId);
            } catch (Exception omiseEx) {
                // If Omise fails, create test mode charge
                System.out.println("AliPay not available in Omise, creating test mode charge: " + omiseEx.getMessage());
                chargeId = ChargeStore.addCharge(sourceId, (long) amount * 100, "THB");
            }

            // Save charge to memory
            if (!chargeId.startsWith("test_chrg_")) {
                ChargeStore.addCharge(sourceId, (long) amount * 100, "THB");
            }

            PaymentResponseBao response = new PaymentResponseBao();
            response.setSourceId(sourceId);
            response.setChargeId(chargeId);
            response.setQrCodeUrl(null);
            response.setAmount((long) amount * 100);
            response.setCurrency("THB");
            response.setChargeStatus("Pending");
            
            System.out.println("AliPay payment processed: Charge ID = " + chargeId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error creating AliPay payment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/wechat-pay")
    public ResponseEntity<PaymentResponseBao> createWeChatPayPayment(@RequestParam Integer amount) {
        try {
            // Store charge in memory
            String chargeId = ChargeStore.addCharge("wechat-test-mode", (long) amount * 100, "THB");

            PaymentResponseBao response = new PaymentResponseBao();
            response.setSourceId("wechat-test-mode");
            response.setChargeId(chargeId);
            response.setQrCodeUrl(null);
            response.setAmount((long) amount * 100);
            response.setCurrency("THB");
            response.setChargeStatus("Test Mode - Not Available");
            
            System.out.println("WeChat Pay payment requested: Charge ID = " + chargeId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error creating WeChat Pay payment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/transaction/{chargeId}")
    public ResponseEntity<?> checkTransaction(@PathVariable String chargeId) {
        try {
            // First, check if charge exists in memory (test mode)
            // ChargeStore.ChargeRecord record = ChargeStore.getCharge(chargeId);
              ChargeStore.ChargeRecord record = ChargeStore.getCharge(chargeId);
            if (record != null) {
                System.out.println("Found charge in memory: " + record.getId());
                 
                ChargeDto dto = new ChargeDto(
                        record.getId(),
                        record.getAmount(),
                        record.getCurrency(),
                        record.getStatus(),
                        record.getSourceId(),
                        record.getCreatedAt(),
                        null  // Pass the fetched charge for test mode
                );
                return ResponseEntity.ok(dto);
            }
                System.out.println( chargeId+ "  : Not Found charge in memory " );

            // If not found in memory, try to fetch from Omise API
            System.out.println("Charge not in memory, trying Omise API for: " + chargeId);

            try { 
                Request<Charge> request = new Charge.GetRequestBuilder(chargeId).build();
                Charge charge = omiseClient.sendRequest(request);
                     System.out.println("Charge found in Omise: " + charge.getId() + ", Status: " + charge.getStatus());
                TransactionResponseBao response = new TransactionResponseBao();
                response.setId(charge.getId());
                response.setStatus(charge.getStatus().toString());
                response.setAmount(charge.getAmount());
                response.setCurrency(charge.getCurrency());
                response.setDescription(charge.getDescription());
                response.setCharge(charge);
            return ResponseEntity.ok(response);
            } catch (Exception e) {
                // TODO: handle exception
                ChargeDto dto = new ChargeDto(
                         chargeId ,
                        record != null ? record.getAmount() : 0L    ,
                         record != null ? record.getCurrency() : "THB",
                        record != null ? record.getStatus() : "failed",
                        record != null ? record.getSourceId() : "wechat-test-mode",
                        record != null ? record.getCreatedAt() : new Date().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(),
                        null  // Pass the fetched charge for test mode
                );
                return ResponseEntity.ok(dto);
            }

       

        } catch (Exception e) {
            System.err.println("Error checking transaction for " + chargeId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new ErrorResponse("Charge not found", e.getMessage()));
        }
    }

    // ======================== Charge Management Endpoints ========================

    @GetMapping("/charges")
    public ResponseEntity<List<ChargeDto>> getAllCharges() {
        try {
            List<ChargeStore.ChargeRecord> records = ChargeStore.getAllCharges();
            List<ChargeDto> dtos = records.stream()
                    .map(r -> new ChargeDto(
                            r.getId(),
                            r.getAmount(),
                            r.getCurrency(),
                            r.getStatus(),
                            r.getSourceId(),
                            r.getCreatedAt(),
                            null // Initialize charge to null
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/charges/{chargeId}")
    public ResponseEntity<TransactionResponseBao> getCharge(@PathVariable String chargeId) {
        try {
            ChargeStore.ChargeRecord record = ChargeStore.getCharge(chargeId);
            if (record == null) {
                return ResponseEntity.notFound().build();
            }

            Request<Charge> request = new Charge.GetRequestBuilder(chargeId).build();
            Charge charge = omiseClient.sendRequest(request);
            TransactionResponseBao response = new TransactionResponseBao();
            response.setId(charge.getId());
            response.setStatus(charge.getStatus().toString());
            response.setAmount(charge.getAmount());
            response.setCurrency(charge.getCurrency());
            response.setDescription(charge.getDescription());
            response.setCharge(charge);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/charges/{chargeId}/status")
    public ResponseEntity<ChargeDto> updateChargeStatus(@PathVariable String chargeId, @RequestParam String status) {
        try {
            ChargeStore.updateChargeStatus(chargeId, status);
            ChargeStore.ChargeRecord record = ChargeStore.getCharge(chargeId);
            if (record == null) {
                return ResponseEntity.notFound().build();
            }
            ChargeDto dto = new ChargeDto(
                    record.getId(),
                    record.getAmount(),
                    record.getCurrency(),
                    record.getStatus(),
                    record.getSourceId(),
                    record.getCreatedAt(),
                    null
            );
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/charges/clear")
    public ResponseEntity<String> clearAllCharges() {
        try {
            ChargeStore.clearAll();
            return ResponseEntity.ok("{\"message\": \"All charges cleared\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("{\"error\": \"Failed to clear charges\"}");
        }
    }
}