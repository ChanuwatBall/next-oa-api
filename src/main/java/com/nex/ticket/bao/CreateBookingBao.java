package com.nex.ticket.bao;

import java.util.List;

public class CreateBookingBao {
    private String tripId;
    private String travelDate;
    private String originProvinceId;
    private String destinationProvinceId;
    private String boardingPointId;
    private String dropOffPointId;
    private String promoCode;
    private List<BookingPassengerBao> passengers;

    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }

    public String getTravelDate() { return travelDate; }
    public void setTravelDate(String travelDate) { this.travelDate = travelDate; }

    public String getOriginProvinceId() { return originProvinceId; }
    public void setOriginProvinceId(String originProvinceId) { this.originProvinceId = originProvinceId; }

    public String getDestinationProvinceId() { return destinationProvinceId; }
    public void setDestinationProvinceId(String destinationProvinceId) { this.destinationProvinceId = destinationProvinceId; }

    public String getBoardingPointId() { return boardingPointId; }
    public void setBoardingPointId(String boardingPointId) { this.boardingPointId = boardingPointId; }

    public String getDropOffPointId() { return dropOffPointId; }
    public void setDropOffPointId(String dropOffPointId) { this.dropOffPointId = dropOffPointId; }

    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public List<BookingPassengerBao> getPassengers() { return passengers; }
    public void setPassengers(List<BookingPassengerBao> passengers) { this.passengers = passengers; }
}
