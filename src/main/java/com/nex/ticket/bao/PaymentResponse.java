package com.nex.ticket.bao;

public class PaymentResponse {
    private String orderId;
    private String status; // เช่น "SUCCESS", "FAILED", "PENDING"
    private String message;
    private long timestamp;

    // Constructor พื้นฐานสำหรับเรียกใช้ง่ายๆ
    public PaymentResponse(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
        this.message = "Payment status updated to: " + status;
        this.timestamp = System.currentTimeMillis();
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}