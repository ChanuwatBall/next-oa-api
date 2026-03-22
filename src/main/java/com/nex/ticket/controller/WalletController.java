package com.nex.ticket.controller;

import com.nex.ticket.bao.WalletTopupBao;
import com.nex.ticket.service.ChargeStore;
import org.springframework.beans.factory.annotation.Autowired;
import co.omise.Client;
import co.omise.models.Charge;
import co.omise.models.Source;
import co.omise.models.SourceType;
import co.omise.requests.Request;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Domain 9 – Wallet (🔒 Auth placeholder)
 * GET /api/wallet
 * GET /api/wallet/transactions
 * POST /api/wallet/topup
 */
@RestController
@RequestMapping("/api/wallet")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WalletController {

    @Autowired
    private Client omiseClient;

    // ─── Mock state ───────────────────────────────────────────────────────────
    private static int balance = 350;

    private static final List<Map<String, Object>> TRANSACTIONS = new ArrayList<>(Arrays.asList(
            buildTx("tx_001", "จ่ายค่าตั๋ว กรุงเทพ → เชียงใหม่", "28 ก.พ. 2566", -850, "payment"),
            buildTx("tx_002", "เติมเงินผ่าน QR Code", "8 ก.พ. 2566", 500, "topup"),
            buildTx("tx_003", "แลกแต้มเป็นเงิน", "10 ก.พ. 2566", 50, "redeem")));

    // ─── GET /api/wallet ──────────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<Map<String, Object>> getWallet() {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("balance", balance);
        resp.put("availablePoints", 156);
        resp.put("transactions", TRANSACTIONS);
        return ResponseEntity.ok(resp);
    }

    // ─── GET /api/wallet/transactions ─────────────────────────────────────────

    @GetMapping("/transactions")
    public ResponseEntity<List<Map<String, Object>>> getTransactions() {
        return ResponseEntity.ok(TRANSACTIONS);
    }

    // ─── POST /api/wallet/topup ───────────────────────────────────────────────

    @PostMapping("/topup")
    public ResponseEntity<Map<String, Object>> topup(@RequestBody WalletTopupBao body) {
        if (body.getAmount() <= 0) {
            return ResponseEntity.badRequest().build();
        }

        String qrCodeUrl = null;
        String chargeId;

        try {
            // Map sourceType string to Omise SourceType
            SourceType sourceType = resolveSourceType(body.getSourceType());

            Request<Source> sourceReq = new Source.CreateRequestBuilder()
                    .type(sourceType)
                    .amount((long) body.getAmount() * 100)
                    .currency("THB")
                    .build();
            Source source = omiseClient.sendRequest(sourceReq);

            Request<Charge> chargeReq = new Charge.CreateRequestBuilder()
                    .amount((long) body.getAmount() * 100)
                    .currency("THB")
                    .source(source.getId())
                    .build();
            Charge charge = omiseClient.sendRequest(chargeReq);

            chargeId = charge.getId();
            if (charge.getSource() != null
                    && charge.getSource().getScannableCode() != null
                    && charge.getSource().getScannableCode().getImage() != null) {
                qrCodeUrl = charge.getSource().getScannableCode().getImage().getDownloadUri();
            }

            // Record in ChargeStore for status polling
            ChargeStore.addCharge(charge.getId(), source.getId(), (long) body.getAmount() * 100, "THB", null, null);

        } catch (Exception e) {
            // Fallback: create test-mode charge
            chargeId = ChargeStore.addCharge(null, "wallet-test-" + body.getSourceType(),
                    (long) body.getAmount() * 100, "THB", null, null);
            qrCodeUrl = null;
            System.err.println("Wallet topup Omise error (using test mode): " + e.getMessage());
        }

        // Optimistically add balance in mock state
        balance += body.getAmount();
        TRANSACTIONS.add(0, buildTx(chargeId, "เติมเงินผ่าน " + body.getSourceType(),
                java.time.LocalDate.now().toString(), body.getAmount(), "topup"));

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("chargeId", chargeId);
        resp.put("qrCodeUrl", qrCodeUrl);
        return ResponseEntity.ok(resp);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private SourceType resolveSourceType(String type) {
        if (type == null)
            return SourceType.PromptPay;
        return switch (type.toLowerCase()) {
            case "alipay" -> SourceType.Alipay;
            case "wechat_pay_mpm" -> SourceType.WeChatPay;
            default -> SourceType.PromptPay;
        };
    }

    private static Map<String, Object> buildTx(
            String id, String description, String date, int amount, String type) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("description", description);
        m.put("date", date);
        m.put("amount", amount);
        m.put("type", type); // topup | payment | redeem
        return m;
    }
}
