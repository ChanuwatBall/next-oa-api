package com.nex.ticket.bao;

public class PaymentResponseBao {
    private String sourceId;
    private String chargeId;
    private String qrCodeUrl;
    private Long amount;
    private String currency;
    private String chargeStatus;

    public PaymentResponseBao() {
    }

    public PaymentResponseBao(String sourceId, String chargeId, String qrCodeUrl, 
                             Long amount, String currency, String chargeStatus) {
        this.sourceId = sourceId;
        this.chargeId = chargeId;
        this.qrCodeUrl = qrCodeUrl;
        this.amount = amount;
        this.currency = currency;
        this.chargeStatus = chargeStatus;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getChargeId() {
        return chargeId;
    }

    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
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

    public String getChargeStatus() {
        return chargeStatus;
    }

    public void setChargeStatus(String chargeStatus) {
        this.chargeStatus = chargeStatus;
    }
}
