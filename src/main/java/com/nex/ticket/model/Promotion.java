package com.nex.ticket.model;

public class Promotion {
    private String id;
    private String title;
    private String description;
    private String imageUrl;
    private String promoCode;
    private int discountPercent;
    private int discountAmount;
    private int remainingQuota;
    private String expiryDate;
    private int validityDays;
    private boolean memberOnly;

    public Promotion() {}

    public Promotion(String id, String title, String description, String imageUrl, String promoCode,
                     int discountPercent, int discountAmount, int remainingQuota,
                     String expiryDate, int validityDays, boolean memberOnly) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.promoCode = promoCode;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.remainingQuota = remainingQuota;
        this.expiryDate = expiryDate;
        this.validityDays = validityDays;
        this.memberOnly = memberOnly;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public int getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(int discountAmount) { this.discountAmount = discountAmount; }

    public int getRemainingQuota() { return remainingQuota; }
    public void setRemainingQuota(int remainingQuota) { this.remainingQuota = remainingQuota; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public int getValidityDays() { return validityDays; }
    public void setValidityDays(int validityDays) { this.validityDays = validityDays; }

    public boolean isMemberOnly() { return memberOnly; }
    public void setMemberOnly(boolean memberOnly) { this.memberOnly = memberOnly; }
}
