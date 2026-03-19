package com.nex.ticket.bao;

import java.util.List;

public class PaymentBodyBao {
    private RouteDetail route;
    private List<SeatDetail> seat;
    private Double subtotal;
    private Double discount;
    private Double total;

    public RouteDetail getRoute() {
        return route;
    }

    public void setRoute(RouteDetail route) {
        this.route = route;
    }

    public List<SeatDetail> getSeat() {
        return seat;
    }

    public void setSeat(List<SeatDetail> seat) {
        this.seat = seat;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

}
