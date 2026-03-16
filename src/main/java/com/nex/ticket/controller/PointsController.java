package com.nex.ticket.controller;

import com.nex.ticket.bao.RedeemPointsBao;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Domain 8 – Points  (🔒 Auth placeholder)
 * GET  /api/points
 * GET  /api/points/history
 * POST /api/points/redeem
 */
@RestController
@RequestMapping("/api/points")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PointsController {

    // ─── Mock state ──────────────────────────────────────────────────────────
    private static int totalPoints    = 156;
    private static int nextRewardAt   = 200;
    private static final int REWARD_PER_10 = 25; // baht per 10 points

    private static final List<Map<String, Object>> HISTORY = new ArrayList<>(Arrays.asList(
        buildHistoryItem("ph_001", "จอง กรุงเทพ → เชียงใหม่", "28 ก.พ. 2566", 850, 8, "earn"),
        buildHistoryItem("ph_002", "จอง กรุงเทพ → หาดใหญ่",   "20 ม.ค. 2566", 850, 8, "earn"),
        buildHistoryItem("ph_003", "แลกแต้มเป็นเงิน Wallet",   "10 ก.พ. 2566", 0, -20, "redeem")
    ));

    // ─── GET /api/points ──────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPoints() {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("totalPoints",  totalPoints);
        resp.put("nextRewardAt", nextRewardAt);
        resp.put("rewardValue",  REWARD_PER_10);
        return ResponseEntity.ok(resp);
    }

    // ─── GET /api/points/history ──────────────────────────────────────────────

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistory() {
        return ResponseEntity.ok(HISTORY);
    }

    // ─── POST /api/points/redeem ──────────────────────────────────────────────

    @PostMapping("/redeem")
    public ResponseEntity<Map<String, Object>> redeemPoints(@RequestBody RedeemPointsBao body) {
        if (body.getPoints() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        if (body.getPoints() > totalPoints) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("statusCode", 422);
            err.put("message",    "แต้มไม่เพียงพอ");
            err.put("error",      "Unprocessable Entity");
            return ResponseEntity.unprocessableEntity().body(err);
        }

        int bahtAdded = (body.getPoints() / 10) * REWARD_PER_10;
        totalPoints  -= body.getPoints();
        nextRewardAt  = totalPoints + (10 - (totalPoints % 10));

        // Add redeem event to history
        Map<String, Object> newItem = buildHistoryItem(
                "ph_" + System.currentTimeMillis(),
                "แลกแต้มเป็นเงิน Wallet",
                java.time.LocalDate.now().toString(),
                0, -body.getPoints(), "redeem"
        );
        HISTORY.add(0, newItem);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("redeemedPoints",    body.getPoints());
        resp.put("bahtAdded",         bahtAdded);
        resp.put("newWalletBalance",  350 + bahtAdded); // simplified mock
        resp.put("remainingPoints",   totalPoints);
        return ResponseEntity.ok(resp);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private static Map<String, Object> buildHistoryItem(
            String id, String description, String date, int amount, int points, String type) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",          id);
        m.put("description", description);
        m.put("date",        date);
        m.put("amount",      amount);
        m.put("points",      points);
        m.put("type",        type);  // earn | redeem
        return m;
    }
}
