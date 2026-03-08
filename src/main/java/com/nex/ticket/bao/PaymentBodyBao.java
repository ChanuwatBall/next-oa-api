package com.nex.ticket.bao;

public class PaymentBodyBao {

    private Integer amount;
    private String lineUserId;

    public String getLineUserId() {
        return lineUserId;
    }
    public void setLineUserId(String lineUserId) {
        this.lineUserId = lineUserId;
    }

    public Integer getAmount() {
        return amount;
    }
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
