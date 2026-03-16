package com.nex.ticket.bao;

public class WalletTopupBao {
    /** Amount in baht */
    private int amount;
    /** promptpay | alipay | wechat_pay_mpm */
    private String sourceType;

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
}
