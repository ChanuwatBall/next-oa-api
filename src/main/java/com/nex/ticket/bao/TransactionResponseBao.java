package com.nex.ticket.bao;

import co.omise.models.Charge;

public class TransactionResponseBao {
    private String id;
    private String status;
    private Long amount;
    private String currency;
    private String description;
    private Charge charge;
 

    public TransactionResponseBao() {
    }

    public TransactionResponseBao(String id, String status, Long amount, 
                                 String currency, String description) {
        this.id = id;
        this.status = status;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }
}
