package com.nex.ticket.model;

public class BoardingPoint {
    private String id;
    private String name;
    private String nameEn;
    private String provinceId;

    public BoardingPoint() {}

    public BoardingPoint(String id, String name, String nameEn, String provinceId) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
        this.provinceId = provinceId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public String getProvinceId() { return provinceId; }
    public void setProvinceId(String provinceId) { this.provinceId = provinceId; }
}
