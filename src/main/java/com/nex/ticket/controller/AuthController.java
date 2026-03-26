package com.nex.ticket.controller;

import com.nex.ticket.bao.AuthLineBao;
import com.nex.ticket.bao.AuthLoginBao;
import com.nex.ticket.bao.AuthRegisterBao;
import com.nex.ticket.bao.UpdateProfileBao;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Domain 6 – Authentication
 * POST /api/auth/login
 * POST /api/auth/register
 * POST /api/auth/line
 * POST /api/auth/logout
 *
 * Domain 7 – User Profile (🔒 Auth placeholder)
 * GET /api/users/me
 * PATCH /api/users/me
 */
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    // ─── Shared mock user (single-user mock mode) ──────────────────────────────
    private static final Map<String, Object> MOCK_USER = new LinkedHashMap<>();

    static {
        MOCK_USER.put("id", "usr_001");
        MOCK_USER.put("fullName", "สมชาย ใจดี");
        MOCK_USER.put("phone", "0812345678");
        MOCK_USER.put("email", "user@example.com");
        MOCK_USER.put("lineUserId", "Uxxxxxxxxxxxxxxxx");
        MOCK_USER.put("avatarUrl", "https://cdn.example.com/avatar/usr_001.jpg");
        MOCK_USER.put("points", 156);
        MOCK_USER.put("walletBalance", 350);
        MOCK_USER.put("memberSince", "2025-01-15");
    }

    private Map<String, Object> buildAuthResponse() {
        String token = "mock-jwt-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("token", token);
        response.put("user", new LinkedHashMap<>(MOCK_USER));
        return response;
    }

    // ─── POST /api/auth/login ──────────────────────────────────────────────────

    @PostMapping("/api/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AuthLoginBao body) {
        if (body.getEmail() == null || body.getPassword() == null) {
            return ResponseEntity.badRequest().body(errorBody(400, "email และ password ห้ามว่าง", "Bad Request"));
        }
        return ResponseEntity.ok(buildAuthResponse());
    }

    // ─── POST /api/auth/register ───────────────────────────────────────────────

    @PostMapping("/api/auth/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody AuthRegisterBao body) {
        if (body.getEmail() == null || body.getPassword() == null
                || body.getFullName() == null || body.getPhone() == null) {
            return ResponseEntity.badRequest().body(errorBody(400, "กรุณากรอกข้อมูลให้ครบ", "Bad Request"));
        }
        // Update mock user to reflect registration data
        MOCK_USER.put("fullName", body.getFullName());
        MOCK_USER.put("phone", body.getPhone());
        MOCK_USER.put("email", body.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(buildAuthResponse());
    }

    // ─── POST /api/auth/line ───────────────────────────────────────────────────

    @PostMapping("/api/auth/line")
    public ResponseEntity<Map<String, Object>> lineLogin(@RequestBody AuthLineBao body) {
        if (body.getLineAccessToken() == null || body.getLineAccessToken().isBlank()) {
            return ResponseEntity.badRequest().body(errorBody(400, "lineAccessToken ห้ามว่าง", "Bad Request"));
        }
        return ResponseEntity.ok(buildAuthResponse());
    }

    // ─── POST /api/auth/logout ─────────────────────────────────────────────────

    @PostMapping("/api/auth/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("success", true);
        return ResponseEntity.ok(resp);
    }

    // ─── GET /api/users/me ─────────────────────────────────────────────────────

    @GetMapping("/api/users/me")
    public ResponseEntity<Map<String, Object>> getProfile() {
        return ResponseEntity.ok(new LinkedHashMap<>(MOCK_USER));
    }

    // ─── PATCH /api/users/me ───────────────────────────────────────────────────

    @PatchMapping("/api/users/me")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody UpdateProfileBao body) {
        if (body.getFullName() != null)
            MOCK_USER.put("fullName", body.getFullName());
        if (body.getPhone() != null)
            MOCK_USER.put("phone", body.getPhone());
        if (body.getEmail() != null)
            MOCK_USER.put("email", body.getEmail());
        if (body.getAvatarUrl() != null)
            MOCK_USER.put("avatarUrl", body.getAvatarUrl());
        return ResponseEntity.ok(new LinkedHashMap<>(MOCK_USER));
    }

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> errorBody(int code, String message, String error) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("statusCode", code);
        m.put("message", message);
        m.put("error", error);
        return m;
    }
}
