package com.nex.ticket.controller;

import com.nex.ticket.bao.TripSearchRequest;
import com.nex.ticket.mock.MockData;
import com.nex.ticket.model.Trip;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Domain 2 – Trips / Search
 * GET
 * /api/trips?originProvinceId=&destinationProvinceId=&date=&routeId=&passengerCount=
 * GET /api/trips/:id
 * GET /api/trips/:id/seats
 */
@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TripController {

    // ─── List / Search Trips ──────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<List<Trip>> getTrips(
            @RequestBody TripSearchRequest tripSearchRequest) {
        System.out.println("originProvinceId: " + tripSearchRequest.getOriginProvinceId());
        System.out.println("destinationProvinceId: " + tripSearchRequest.getDestinationProvinceId());
        System.out.println("date: " + tripSearchRequest.getDate());
        System.out.println("routeId: " + tripSearchRequest.getRouteId());
        System.out.println("passengerCount: " + tripSearchRequest.getPassengerCount());
        List<Trip> result = MockData.TRIPS;

        if (tripSearchRequest.getOriginProvinceId() != null && !tripSearchRequest.getOriginProvinceId().isBlank()) {
            result = result.stream()
                    .filter(t -> t.getOriginProvinceId().equals(tripSearchRequest.getOriginProvinceId()))
                    .collect(Collectors.toList());
        }
        if (tripSearchRequest.getDestinationProvinceId() != null
                && !tripSearchRequest.getDestinationProvinceId().isBlank()) {
            result = result.stream()
                    .filter(t -> t.getDestinationProvinceId().equals(tripSearchRequest.getDestinationProvinceId()))
                    .collect(Collectors.toList());
        }
        if (tripSearchRequest.getDate() != null && !tripSearchRequest.getDate().isBlank()) {
            result = result.stream()
                    .filter(t -> t.getDate().equals(tripSearchRequest.getDate()))
                    .collect(Collectors.toList());
        }
        if (tripSearchRequest.getRouteId() != null && !tripSearchRequest.getRouteId().isBlank()) {
            result = result.stream()
                    .filter(t -> t.getRouteId().equals(tripSearchRequest.getRouteId()))
                    .collect(Collectors.toList());
        }
        // Filter trips that have enough available seats for the passenger count
        final int count = tripSearchRequest.getPassengerCount();
        result = result.stream()
                .filter(t -> t.getAvailableSeats() >= count)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ─── Single Trip ──────────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable String id) {
        Trip trip = MockData.findTripById(id);
        return trip != null ? ResponseEntity.ok(trip) : ResponseEntity.notFound().build();
    }

    // ─── Seat Layout ──────────────────────────────────────────────────────────

    @GetMapping("/{id}/seats")
    public ResponseEntity<Map<String, Object>> getTripSeats(@PathVariable String id) {
        Trip trip = MockData.findTripById(id);
        if (trip == null) {
            return ResponseEntity.notFound().build();
        }

        // Build a simple mock seat layout based on totalSeats
        int total = trip.getTotalSeats();
        int available = trip.getAvailableSeats();
        int booked = total - available;

        // Layout: 4-across rows (A B _ C D pattern for VIP, A B C D for normal)
        boolean isVip = trip.getBusType().toUpperCase().contains("VIP");
        int cols = isVip ? 3 : 4; // VIP: 1A 1B | 1C / Normal: 1A 1B 1C 1D
        int rows = (int) Math.ceil((double) total / cols);

        // Generate seat list
        List<Map<String, Object>> seats = new ArrayList<>();
        String[] colLabels = isVip ? new String[] { "A", "B", "C" } : new String[] { "A", "B", "C", "D" };
        int seatIdx = 0;
        int bookedCount = 0;

        for (int r = 1; r <= rows && seatIdx < total; r++) {
            for (int c = 0; c < cols && seatIdx < total; c++, seatIdx++) {
                String seatNumber = r + colLabels[c];
                String status;
                if (bookedCount < booked) {
                    status = "booked";
                    bookedCount++;
                } else {
                    status = "available";
                }
                Map<String, Object> seat = new LinkedHashMap<>();
                seat.put("id", "s-" + seatNumber);
                seat.put("number", seatNumber);
                seat.put("row", r);
                seat.put("col", c);
                seat.put("status", status);
                seat.put("floor", 1);
                seats.add(seat);
            }
        }

        // Layout header rows
        List<List<String>> layoutRows = new ArrayList<>();
        List<String> headerRow = isVip
                ? Arrays.asList("DOOR1", null, "DRIVER")
                : Arrays.asList("DOOR1", null, null, "DRIVER");
        layoutRows.add(headerRow);

        Map<String, Object> layout = new LinkedHashMap<>();
        layout.put("id", isVip ? "vip" : "standard");
        layout.put("name", isVip ? "รถบัส VIP" : "รถบัส มาตรฐาน");
        layout.put("rows", layoutRows);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tripId", id);
        response.put("layout", layout);
        response.put("seats", seats);

        return ResponseEntity.ok(response);
    }
}
