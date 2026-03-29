package com.nex.ticket.controller;

import com.nex.ticket.bao.PaymentBodyBao;
import com.nex.ticket.bao.RouteDetail;
import com.nex.ticket.service.ChargeStore;
import com.nex.ticket.service.PaymentService;

import co.omise.Client;
import co.omise.models.Charge;
import co.omise.requests.Request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api/webhooks")
public class OmiseWebhookController {

    @Autowired
    private Client omiseClient;

    private final PaymentService paymentService;

    public OmiseWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/omise")
    public ResponseEntity<Void> handleOmiseEvent(@RequestBody Map<String, Object> payload) {
        try {
            // 1. แกะโครงสร้าง JSON ของ Omise
            // โครงสร้างปกติคือ: { "data": { "status": "...", "metadata": { "order_id":
            // "..." } } }
            Map<String, Object> data = (Map<String, Object>) payload.get("data");
            String status = (String) data.get("status");

            Map<String, Object> metadata = (Map<String, Object>) data.get("metadata");
            String omiseSouceId = (String) metadata.get("order_id");
            System.out.println("Omise Source ID: " + omiseSouceId);
            if (omiseSouceId != null && status.equals("successful")) {
                // 2. ส่งข้อมูลเข้า Service เพื่อยิง WebSocket
                ChargeStore.ChargeRecord matchCharge = ChargeStore.getAllCharges().stream()
                        .filter(c -> c.getSourceId().equals(omiseSouceId))
                        .findFirst().orElse(null);
                if (matchCharge == null) {
                    return ResponseEntity.badRequest().build();
                }
                System.out.println("Match charge id: " + matchCharge.getId());
                // String successRouteId =
                // matchCharge.getPaymentBodyBao().getRoute().getRouteId();
                paymentService.processPaymentUpdate(omiseSouceId, status);

                try {
                    RouteDetail routeDetail = matchCharge.getPaymentBodyBao().getRoute();
                    List<ChargeStore.ChargeRecord> otherCharges = ChargeStore.getAllCharges().stream()
                            .filter(c -> !c.getId().equals(matchCharge.getId()))
                            .filter(c -> c.getStatus().toLowerCase().equals("pending"))
                            .filter(c -> c.getPaymentBodyBao() != null && c.getPaymentBodyBao().getRoute().getDate()
                                    .equals(routeDetail.getDate()))
                            .filter(c -> c.getPaymentBodyBao() != null
                                    && c.getPaymentBodyBao().getRoute().getDestination()
                                            .equals(routeDetail.getDestination()))
                            .filter(c -> c.getPaymentBodyBao() != null && c.getPaymentBodyBao().getRoute().getOrigin()
                                    .equals(routeDetail.getOrigin()))
                            .toList();
                    System.out.println("otherCharges: " + otherCharges.size());
                    Set<String> matchSeatIds = matchCharge.getPaymentBodyBao() != null
                            && matchCharge.getPaymentBodyBao().getSeat() != null
                                    ? matchCharge.getPaymentBodyBao().getSeat().stream()
                                            .map(s -> s.getId())
                                            .filter(Objects::nonNull)
                                            .collect(java.util.stream.Collectors.toSet())
                                    : Set.of();

                    for (ChargeStore.ChargeRecord chrg : otherCharges) {
                        PaymentBodyBao otherPbao = chrg.getPaymentBodyBao();
                        if (otherPbao != null && otherPbao.getRoute() != null
                                && otherPbao.getSeat() != null) {

                            boolean hasOverlap = otherPbao.getSeat().stream()
                                    .map(s -> s.getId())
                                    .anyMatch(matchSeatIds::contains);

                            System.out.println("has overlap : " + hasOverlap);
                            if (hasOverlap) {
                                ChargeStore.updateChargeStatus(chrg.getId(), "failed");
                                String otherId = chrg.getId();
                                if (otherId != null && !otherId.startsWith("test_chrg_")) {
                                    try {
                                        Request<Charge> markAsFailedRequest = new Charge.MarkAsFailedRequestBuilder(
                                                otherId)
                                                .build();
                                        omiseClient.sendRequest(markAsFailedRequest);
                                        System.err.println("Success to mark Omise charge as failed " + otherId);
                                        if (chrg.getSourceId() != null) {
                                            paymentService.processPaymentUpdate(chrg.getSourceId(), "failed");
                                            System.out.println(
                                                    "Manual WebSocket sent to cancelled user: " + chrg.getSourceId());
                                        }
                                    } catch (Exception e) {
                                        System.err.println("Failed to mark Omise charge as failed " + otherId + ": "
                                                + e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            return ResponseEntity.ok().build(); // ตอบกลับ Omise ว่าได้รับแล้ว (200 OK)
        } catch (Exception e) {
            // หากเกิดข้อผิดพลาดในการแกะ JSON
            return ResponseEntity.badRequest().build();
        }
    }
}