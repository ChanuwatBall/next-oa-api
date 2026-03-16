package com.nex.ticket.model;

public class Trip {
    private String id;
    private String routeId;
    private String originProvinceId;
    private String destinationProvinceId;
    private String departureTime;
    private String arrivalTime;
    private int price;
    private int availableSeats;
    private int totalSeats;
    private String tripType;
    private String busType;
    private String date;

    public Trip() {}

    public Trip(String id, String routeId, String originProvinceId, String destinationProvinceId,
                String departureTime, String arrivalTime, int price,
                int availableSeats, int totalSeats, String tripType, String busType, String date) {
        this.id = id;
        this.routeId = routeId;
        this.originProvinceId = originProvinceId;
        this.destinationProvinceId = destinationProvinceId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.price = price;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
        this.tripType = tripType;
        this.busType = busType;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }

    public String getOriginProvinceId() { return originProvinceId; }
    public void setOriginProvinceId(String originProvinceId) { this.originProvinceId = originProvinceId; }

    public String getDestinationProvinceId() { return destinationProvinceId; }
    public void setDestinationProvinceId(String destinationProvinceId) { this.destinationProvinceId = destinationProvinceId; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public String getTripType() { return tripType; }
    public void setTripType(String tripType) { this.tripType = tripType; }

    public String getBusType() { return busType; }
    public void setBusType(String busType) { this.busType = busType; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
