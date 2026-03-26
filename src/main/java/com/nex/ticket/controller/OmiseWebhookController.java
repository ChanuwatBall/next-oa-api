package com.nex.ticket.controller;

import com.nex.ticket.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks")
public class OmiseWebhookController {

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
            String orderId = (String) metadata.get("order_id");

            if (orderId != null) {
                // 2. ส่งข้อมูลเข้า Service เพื่อยิง WebSocket
                paymentService.processPaymentUpdate(orderId, status);
            }

            return ResponseEntity.ok().build(); // ตอบกลับ Omise ว่าได้รับแล้ว (200 OK)
        } catch (Exception e) {
            // หากเกิดข้อผิดพลาดในการแกะ JSON
            return ResponseEntity.badRequest().build();
        }
    }
}