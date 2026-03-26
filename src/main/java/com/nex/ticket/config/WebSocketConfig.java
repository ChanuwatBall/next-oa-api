package com.nex.ticket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // เปิดใช้งาน WebSocket Message Broker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // คลื่นความถี่ที่ Server จะใช้ยิงออกไปหา Client (เช่น /topic/order/123)
        config.enableSimpleBroker("/topic");

        // Prefix สำหรับข้อความที่ Client ส่งเข้ามาหา Server
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // --- นี่คือจุดที่ /ws-payment ถูกสร้างขึ้น ---
        registry.addEndpoint("/ws-payment")
                .setAllowedOriginPatterns("*") // อนุญาตให้ React (คนละ Port) เชื่อมต่อได้
                .withSockJS(); // รองรับ SockJS สำหรับ Browser รุ่นเก่า
    }
}