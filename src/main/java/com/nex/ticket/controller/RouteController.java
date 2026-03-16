package com.nex.ticket.controller;

import com.nex.ticket.mock.MockData;
import com.nex.ticket.model.BoardingPoint;
import com.nex.ticket.model.Province;
import com.nex.ticket.model.Route;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Domain 1 – Routes & Geography
 * GET /api/routes
 * GET /api/provinces?routeId=
 * GET /api/boarding-points?provinceId=
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RouteController {

    // ─── Routes ───────────────────────────────────────────────────────────────

    @GetMapping("/routes")
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(MockData.ROUTES);
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable String id) {
        Route route = MockData.findRouteById(id);
        return route != null ? ResponseEntity.ok(route) : ResponseEntity.notFound().build();
    }

    // ─── Provinces ────────────────────────────────────────────────────────────

    @GetMapping("/provinces")
    public ResponseEntity<List<Province>> getProvinces(
            @RequestParam(required = false) String routeId) {
        List<Province> result = MockData.PROVINCES;
        if (routeId != null && !routeId.isBlank()) {
            result = result.stream()
                    .filter(p -> p.getRouteIds().contains(routeId))
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/provinces/{id}")
    public ResponseEntity<Province> getProvinceById(@PathVariable String id) {
        Province province = MockData.findProvinceById(id);
        return province != null ? ResponseEntity.ok(province) : ResponseEntity.notFound().build();
    }

    // ─── Boarding Points ──────────────────────────────────────────────────────

    @GetMapping("/boarding-points")
    public ResponseEntity<List<BoardingPoint>> getBoardingPoints(
            @RequestParam(required = false) String provinceId) {
        if (provinceId != null && !provinceId.isBlank()) {
            return ResponseEntity.ok(MockData.findBoardingPointsByProvinceId(provinceId));
        }
        return ResponseEntity.ok(MockData.BOARDING_POINTS);
    }

    @GetMapping("/boarding-points/{id}")
    public ResponseEntity<BoardingPoint> getBoardingPointById(@PathVariable String id) {
        BoardingPoint bp = MockData.findBoardingPointById(id);
        return bp != null ? ResponseEntity.ok(bp) : ResponseEntity.notFound().build();
    }
}
