package com.nex.ticket.model;

import java.util.List;

public class Province {
    private String id;
    private String name;
    private String nameEn;
    private List<String> routeIds;

    public Province() {}

    public Province(String id, String name, String nameEn, List<String> routeIds) {
        this.id = id;
        this.name = name;
        this.nameEn = nameEn;
        this.routeIds = routeIds;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameEn() { return nameEn; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn; }

    public List<String> getRouteIds() { return routeIds; }
    public void setRouteIds(List<String> routeIds) { this.routeIds = routeIds; }
}
