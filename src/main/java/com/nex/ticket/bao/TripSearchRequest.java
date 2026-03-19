package com.nex.ticket.bao;

public class TripSearchRequest {
    String originProvinceId;
    String destinationProvinceId;
    String date;
    String routeId;
    int passengerCount;

    public String getOriginProvinceId() {
        return originProvinceId;
    }

    public void setOriginProvinceId(String originProvinceId) {
        this.originProvinceId = originProvinceId;
    }

    public String getDestinationProvinceId() {
        return destinationProvinceId;
    }

    public void setDestinationProvinceId(String destinationProvinceId) {
        this.destinationProvinceId = destinationProvinceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int passengerCount) {
        this.passengerCount = passengerCount;
    }
}
