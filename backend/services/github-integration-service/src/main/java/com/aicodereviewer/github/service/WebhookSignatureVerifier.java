package com.aicodereviewer.github.service;

import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WebhookSignatureVerifier {

    private final String webhookSecret;

    public WebhookSignatureVerifier(@Value("${github.webhook-secret:}") String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    public boolean verify(String body, String signatureHeader) {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            return true;
        }
        if (signatureHeader == null || !signatureHeader.startsWith("sha256=")) {
            return false;
        }

        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = hmac.doFinal(body.getBytes(StandardCharsets.UTF_8));
            String expected = "sha256=" + java.util.HexFormat.of().formatHex(hash);
            return constantTimeEquals(expected, signatureHeader);
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }
}
