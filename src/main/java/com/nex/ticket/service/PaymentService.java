package com.nex.ticket.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.nex.ticket.bao.PaymentResponse;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    // ใช้ SimpMessagingTemplate สำหรับยิง WebSocket เท่านั้น
    private final SimpMessagingTemplate messagingTemplate;

    public PaymentService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * เมธอดสำหรับรับสถานะจาก Omise และส่งต่อให้ UI
     */
    public void processPaymentUpdate(String orderId, String omiseStatus) {
        log.info("Receiving Omise status for Order: {} -> Status: {}", orderId, omiseStatus);

        String finalStatus;

        // 1. แปลงสถานะจาก Omise เป็นสถานะที่ UI เข้าใจ
        // Omise status: pending, succeeded, failed, reversed, etc.
        if ("succeeded".equals(omiseStatus)) {
            finalStatus = "SUCCESS";
        } else if ("failed".equals(omiseStatus)) {
            finalStatus = "FAILED";
        } else {
            finalStatus = "PENDING"; // หรือสถานะอื่นๆ เช่น expired
        }

        // 2. ยิง Message ไปหา UI ทันที
        sendWebSocketNotification(orderId, finalStatus);
    }

    private void sendWebSocketNotification(String orderId, String status) {
        // สร้าง Object ข้อมูลที่จะส่ง
        PaymentResponse response = new PaymentResponse(orderId, status);

        // กำหนดปลายทางท่อ (Topic)
        String destination = "/topic/order/" + orderId;

        // ยิงข้อมูลออกไป!
        messagingTemplate.convertAndSend(destination, response);

        log.info("WebSocket Sent: [{}] to destination: {}", status, destination);
    }
}