package com.nex.ticket.mock;

import com.nex.ticket.model.BoardingPoint;
import com.nex.ticket.model.Promotion;
import com.nex.ticket.model.Province;
import com.nex.ticket.model.Route;
import com.nex.ticket.model.Trip;

import java.util.Arrays;
import java.util.List;

public class MockData {

        // ─────────────────────────────────────────────
        // Routes
        // ─────────────────────────────────────────────
        public static final List<Route> ROUTES = Arrays.asList(
                        new Route("southern", "สายใต้", "Southern Line"),
                        new Route("northern", "สายเหนือ", "Northern Line"),
                        new Route("northeast", "สายอีสาน", "Northeastern Line"),
                        new Route("eastern", "สายตะวันออก", "Eastern Line"));

        // ─────────────────────────────────────────────
        // Provinces
        // ─────────────────────────────────────────────
        public static final List<Province> PROVINCES = Arrays.asList(
                        new Province("bkk", "กรุงเทพฯ", "Bangkok",
                                        Arrays.asList("southern", "northern", "northeast", "eastern")),
                        new Province("cnx", "เชียงใหม่", "Chiang Mai", Arrays.asList("northern")),
                        new Province("cri", "เชียงราย", "Chiang Rai", Arrays.asList("northern")),
                        new Province("nkr", "นครราชสีมา", "Nakhon Ratchasima", Arrays.asList("northeast")),
                        new Province("udn", "อุดรธานี", "Udon Thani", Arrays.asList("northeast")),
                        new Province("skn", "สุราษฎร์ธานี", "Surat Thani", Arrays.asList("southern")),
                        new Province("hdy", "หาดใหญ่", "Hat Yai", Arrays.asList("southern")),
                        new Province("pty", "พัทยา", "Pattaya", Arrays.asList("eastern")),
                        new Province("ryn", "ระยอง", "Rayong", Arrays.asList("eastern")));

        // ─────────────────────────────────────────────
        // Boarding Points
        // ─────────────────────────────────────────────
        public static final List<BoardingPoint> BOARDING_POINTS = Arrays.asList(
                        new BoardingPoint("bkk-mo-chit", "หมอชิต 2", "Mo Chit 2", "bkk"),
                        new BoardingPoint("bkk-sai-tai", "สายใต้ใหม่", "Southern Terminal", "bkk"),
                        new BoardingPoint("bkk-ekkamai", "เอกมัย", "Ekkamai", "bkk"),
                        new BoardingPoint("cnx-arcade", "อาเขต", "Arcade Bus Terminal", "cnx"),
                        new BoardingPoint("cri-terminal", "สถานีขนส่งเชียงราย", "Chiang Rai Terminal", "cri"),
                        new BoardingPoint("nkr-terminal", "สถานีขนส่งโคราช", "Korat Terminal", "nkr"),
                        new BoardingPoint("udn-terminal", "สถานีขนส่งอุดร", "Udon Terminal", "udn"),
                        new BoardingPoint("skn-terminal", "สถานีขนส่งสุราษฎร์", "Surat Terminal", "skn"),
                        new BoardingPoint("hdy-terminal", "สถานีขนส่งหาดใหญ่", "Hat Yai Terminal", "hdy"),
                        new BoardingPoint("pty-terminal", "สถานีขนส่งพัทยา", "Pattaya Terminal", "pty"),
                        new BoardingPoint("ryn-terminal", "สถานีขนส่งระยอง", "Rayong Terminal", "ryn"));

        // ─────────────────────────────────────────────
        // Trips
        // ─────────────────────────────────────────────
        public static final List<Trip> TRIPS = Arrays.asList(
                        new Trip("t1", "northern", "bkk", "cnx", "08:00", "18:00", 550, 24, 40, "ด่วนพิเศษ",
                                        "VIP 24 ที่นั่ง",
                                        "2026-03-23"),
                        new Trip("t2", "northern", "bkk", "cnx", "20:00", "06:00", 650, 12, 32, "ด่วนพิเศษ",
                                        "VIP 32 ที่นั่ง",
                                        "2026-03-23"),
                        new Trip("t3", "northern", "bkk", "cnx", "21:30", "07:30", 750, 6, 24, "ด่วนพิเศษ",
                                        "VIP First Class",
                                        "2026-03-23"),
                        new Trip("t4", "southern", "bkk", "hdy", "18:00", "07:00", 850, 18, 40, "ด่วนพิเศษ",
                                        "VIP 24 ที่นั่ง",
                                        "2026-03-23"),
                        new Trip("t5", "northeast", "bkk", "udn", "19:00", "05:00", 480, 30, 40, "ปรับอากาศ",
                                        "ป.1 (ป.อ.)",
                                        "2026-03-23"),
                        new Trip("t6", "southern", "bkk", "skn", "19:00", "05:30", 600, 20, 40, "ด่วนพิเศษ",
                                        "VIP 24 ที่นั่ง",
                                        "2026-03-23"),
                        new Trip("t7", "southern", "bkk", "skn", "21:00", "07:00", 700, 14, 32, "ด่วนพิเศษ",
                                        "VIP 32 ที่นั่ง",
                                        "2026-03-23"),
                        new Trip("t8", "eastern", "bkk", "pty", "09:00", "12:00", 200, 35, 40, "ปรับอากาศ",
                                        "ป.1 (ป.อ.)",
                                        "2026-03-23"));

        // ─────────────────────────────────────────────
        // Promotions
        // ─────────────────────────────────────────────
        public static final List<Promotion> PROMOTIONS = Arrays.asList(
                        new Promotion(
                                        "p1",
                                        "ส่วนลด 10% สายเหนือ",
                                        "รับส่วนลด 10% สำหรับเส้นทางสายเหนือทุกเที่ยว",
                                        "../assets/promotion/discount10.jpg",
                                        "NORTH10",
                                        10, 0, 50,
                                        "2026-04-30", 30, false),
                        new Promotion(
                                        "p2",
                                        "สมาชิกลด 100 บาท",
                                        "สมาชิกรับส่วนลด 100 บาท เมื่อจองผ่านแอป",
                                        "../assets/promotion/member100.jpg",
                                        "MEMBER100",
                                        0, 100, 20,
                                        "2026-03-31", 15, true),
                        new Promotion(
                                        "p3",
                                        "เดินทางคู่ ลดพิเศษ",
                                        "จอง 2 ที่นั่งขึ้นไป รับส่วนลด 15%",
                                        "../assets/promotion/duo15.jpg",
                                        "DUO15",
                                        15, 0, 100,
                                        "2026-05-15", 60, false));

        // ─────────────────────────────────────────────
        // Helper: find by ID
        // ─────────────────────────────────────────────
        public static Route findRouteById(String id) {
                return ROUTES.stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
        }

        public static Province findProvinceById(String id) {
                return PROVINCES.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
        }

        public static BoardingPoint findBoardingPointById(String id) {
                return BOARDING_POINTS.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
        }

        public static List<BoardingPoint> findBoardingPointsByProvinceId(String provinceId) {
                return BOARDING_POINTS.stream()
                                .filter(b -> b.getProvinceId().equals(provinceId))
                                .collect(java.util.stream.Collectors.toList());
        }

        public static Trip findTripById(String id) {
                return TRIPS.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
        }

        public static List<Trip> findTripsByRoute(String originProvinceId, String destinationProvinceId, String date) {
                return TRIPS.stream()
                                .filter(t -> t.getOriginProvinceId().equals(originProvinceId)
                                                && t.getDestinationProvinceId().equals(destinationProvinceId)
                                                && (date == null || t.getDate().equals(date)))
                                .collect(java.util.stream.Collectors.toList());
        }

        public static Promotion findPromotionByCode(String promoCode) {
                return PROMOTIONS.stream().filter(p -> p.getPromoCode().equalsIgnoreCase(promoCode)).findFirst()
                                .orElse(null);
        }
}
