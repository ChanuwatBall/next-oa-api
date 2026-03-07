package com.nex.ticket.bao;

import java.time.LocalDateTime;

import co.omise.models.Charge;

public class ChargeDto {
    private String id;
    private long amount;
    private String currency;
    private String status;
    private String sourceId;
    private LocalDateTime createdAt;
    private Charge charge;

    public ChargeDto() {}

    public ChargeDto(String id, long amount, String currency, String status, String sourceId, LocalDateTime createdAt , Charge charge) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.sourceId = sourceId;
        this.createdAt = createdAt;
        this.charge =  charge; // Initialize charge to null
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public long getAmount() { return amount; }
    public void setAmount(long amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Charge getCharge() { return charge; }
    public void setCharge(Charge charge) { this.charge = charge; }
}
