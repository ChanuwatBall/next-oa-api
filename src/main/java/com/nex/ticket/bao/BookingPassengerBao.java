package com.nex.ticket.bao;

public class BookingPassengerBao {
    /** seatId, seatNumber, fullName, thaiId, phone, passengerType */
    private String seatId;
    private String seatNumber;
    private String fullName;
    private String thaiId;
    private String phone;
    /** male | female | child | monk */
    private String passengerType;

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getThaiId() { return thaiId; }
    public void setThaiId(String thaiId) { this.thaiId = thaiId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassengerType() { return passengerType; }
    public void setPassengerType(String passengerType) { this.passengerType = passengerType; }
}
