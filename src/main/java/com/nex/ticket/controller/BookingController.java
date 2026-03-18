package com.nex.ticket.controller;

import com.nex.ticket.bao.CreateBookingBao;
import com.nex.ticket.mock.MockData;
import com.nex.ticket.model.Province;
import com.nex.ticket.model.Trip;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Domain 3 – Bookings  (🔒 Auth placeholder — no real JWT yet)
 * POST   /api/bookings
 * GET    /api/bookings?status=
 * GET    /api/bookings/:id
 * PATCH  /api/bookings/:id/cancel
 */
@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BookingController {

    // ─── In-memory booking store ───────────────────────────────────────────────
    private static final Map<String, Map<String, Object>> bookingStore = new ConcurrentHashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(1000);

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ─── POST /api/bookings ────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody CreateBookingBao body) {
        Trip trip = MockData.findTripById(body.getTripId());
        if (trip == null) {
            return ResponseEntity.badRequest().body(errorBody("Trip not found"));
        }

        // Calculate discount
        int basePrice = trip.getPrice();
        int seats = body.getPassengers() != null ? body.getPassengers().size() : 1;
        int discount = 0;
        if (body.getPromoCode() != null && !body.getPromoCode().isBlank()) {
            var promo = MockData.findPromotionByCode(body.getPromoCode());
            if (promo != null) {
                if (promo.getDiscountPercent() > 0) {
                    discount = (basePrice * seats * promo.getDiscountPercent()) / 100;
                } else {
                    discount = promo.getDiscountAmount();
                }
            }
        }
        int total = Math.max(0, (basePrice * seats) - discount);

        String bookingId = "NEX" + Integer.toHexString(counter.incrementAndGet()).toUpperCase();
        String now = LocalDateTime.now().format(DT_FMT);

        Province origin = MockData.findProvinceById(body.getOriginProvinceId());
        Province dest   = MockData.findProvinceById(body.getDestinationProvinceId());

        List<String> seatNumbers = new ArrayList<>();
        if (body.getPassengers() != null) {
            body.getPassengers().forEach(p -> seatNumbers.add(p.getSeatNumber()));
        }

        Map<String, Object> record = new LinkedHashMap<>();
        record.put("id",              bookingId);
        record.put("tripId",          body.getTripId());
        record.put("origin",          origin != null ? origin.getName() : body.getOriginProvinceId());
        record.put("destination",     dest   != null ? dest.getName()   : body.getDestinationProvinceId());
        record.put("date",            body.getTravelDate());
        record.put("departureTime",   trip.getDepartureTime());
        record.put("arrivalTime",     trip.getArrivalTime());
        record.put("seats",           seatNumbers);
        record.put("status",          "pending_payment");
        record.put("total",           total);
        record.put("boardingPoint",   body.getBoardingPointId());
        record.put("dropOffPoint",    body.getDropOffPointId());
        record.put("busType",         trip.getBusType());
        record.put("tripType",        trip.getTripType());
        record.put("busPlate",        "10-1234 กรุงเทพฯ");
        record.put("routeName",       trip.getRouteId());
        record.put("paymentMethod",   "QR PromptPay");
        record.put("promoCode",       body.getPromoCode() != null ? body.getPromoCode() : "");
        record.put("discount",        discount);
        record.put("pricePerSeat",    basePrice);
        record.put("bookingDate",     now);
        record.put("passengers",      body.getPassengers());

        bookingStore.put(bookingId, record);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("bookingId", bookingId);
        response.put("status",    "pending_payment");
        response.put("total",     total);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ─── GET /api/bookings ─────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getBookings(
            @RequestParam(name = "status", required = false) String status) {

        List<Map<String, Object>> list = new ArrayList<>(bookingStore.values());
        if (status != null && !status.isBlank()) {
            list = list.stream()
                    .filter(b -> status.equalsIgnoreCase((String) b.get("status")))
                    .collect(Collectors.toList());
        }

        // Return compact list fields only
        List<Map<String, Object>> result = list.stream().map(b -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",            b.get("id"));
            m.put("origin",        b.get("origin"));
            m.put("destination",   b.get("destination"));
            m.put("date",          b.get("date"));
            m.put("departureTime", b.get("departureTime"));
            m.put("arrivalTime",   b.get("arrivalTime"));
            m.put("seats",         b.get("seats"));
            m.put("status",        b.get("status"));
            m.put("total",         b.get("total"));
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // ─── GET /api/bookings/:id ─────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBookingById(@PathVariable(name = "id") String id) {
        Map<String, Object> booking = bookingStore.get(id);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(booking);
    }

    // ─── PATCH /api/bookings/:id/cancel ───────────────────────────────────────

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable(name = "id") String id) {
        Map<String, Object> booking = bookingStore.get(id);
        if (booking == null) {
            return ResponseEntity.notFound().build();
        }
        booking.put("status", "cancelled");

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "ยกเลิกการจองสำเร็จ");
        return ResponseEntity.ok(response);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, Object> errorBody(String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("statusCode", 400);
        m.put("message",    message);
        m.put("error",      "Bad Request");
        return m;
    }
}
