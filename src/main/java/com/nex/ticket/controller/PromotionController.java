package com.nex.ticket.controller;

import com.nex.ticket.bao.ValidatePromoBao;
import com.nex.ticket.mock.MockData;
import com.nex.ticket.model.Promotion;
import com.nex.ticket.model.Trip;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Domain 4 – Promotions
 * GET /api/promotions?memberOnly=
 * GET /api/promotions/:id
 * POST /api/promotions/validate
 */
@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PromotionController {

    // ─── GET /api/promotions ───────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Promotion>> getPromotions(
            @RequestParam(name = "memberOnly", required = false) Boolean memberOnly) {
        List<Promotion> list = MockData.PROMOTIONS;
        if (memberOnly != null) {
            list = list.stream()
                    .filter(p -> p.isMemberOnly() == memberOnly)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(list);
    }

    // ─── GET /api/promotions/:id ───────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<Promotion> getPromotionById(@PathVariable(name = "id") String id) {
        Promotion promo = MockData.PROMOTIONS.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst().orElse(null);
        return promo != null ? ResponseEntity.ok(promo) : ResponseEntity.notFound().build();
    }

    // ─── POST /api/promotions/validate ────────────────────────────────────────

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validatePromo(@RequestBody ValidatePromoBao body) {
        Map<String, Object> result = new LinkedHashMap<>();

        Promotion promo = MockData.findPromotionByCode(body.getPromoCode());
        if (promo == null) {
            result.put("valid", false);
            result.put("discountPercent", 0);
            result.put("discountAmount", 0);
            result.put("message", "ไม่พบรหัสโปรโมชั่น");
            return ResponseEntity.ok(result);
        }

        if (promo.getRemainingQuota() <= 0) {
            result.put("valid", false);
            result.put("discountPercent", 0);
            result.put("discountAmount", 0);
            result.put("message", "โปรโมชั่นนี้ถูกใช้ครบแล้ว");
            return ResponseEntity.ok(result);
        }

        // If tripId provided, calculate effective discount amount
        int effectiveDiscountAmount = promo.getDiscountAmount();
        if (body.getTripId() != null && promo.getDiscountPercent() > 0) {
            Trip trip = MockData.findTripById(body.getTripId());
            if (trip != null) {
                effectiveDiscountAmount = (trip.getPrice() * promo.getDiscountPercent()) / 100;
            }
        }

        result.put("valid", true);
        result.put("discountPercent", promo.getDiscountPercent());
        result.put("discountAmount", effectiveDiscountAmount);
        result.put("message", null);
        return ResponseEntity.ok(result);
    }
}
