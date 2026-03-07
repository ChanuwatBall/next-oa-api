package com.nex.ticket.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChargeStore {
    private static final Map<String, ChargeRecord> charges = new ConcurrentHashMap<>();
    private static int chargeCounter = 0;

    public static class ChargeRecord {
        private String id;
        private long amount;
        private String currency;
        private String status; // Pending, Success, Failed
        private String sourceId;
        private LocalDateTime createdAt;

        public ChargeRecord(String sourceId, long amount, String currency) {
            this.id = "test_chrg_" + System.currentTimeMillis() + "_" + (++chargeCounter);
            this.sourceId = sourceId;
            this.amount = amount;
            this.currency = currency;
            this.status = "Pending";
            this.createdAt = LocalDateTime.now();
        }

        // Getters and setters
        public String getId() { return id; }
        public long getAmount() { return amount; }
        public String getCurrency() { return currency; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getSourceId() { return sourceId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
    }

    public static String addCharge(String sourceId, long amount, String currency) {
        ChargeRecord record = new ChargeRecord(sourceId, amount, currency);
        charges.put(record.getId(), record);
        return record.getId();
    }

    public static List<ChargeRecord> getAllCharges() {
        return new ArrayList<>(charges.values());
    }

    public static ChargeRecord getCharge(String chargeId) {
        return charges.get(chargeId);
    }

    public static void updateChargeStatus(String chargeId, String status) {
        ChargeRecord record = charges.get(chargeId);
        if (record != null) {
            record.setStatus(status);
        }
    }

    public static void clearAll() {
        charges.clear();
    }
}
