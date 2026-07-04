 package com.cybersec.browsermonitor.service;

import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class RuleEngineService {

    private static final String[] SUSPICIOUS_WORDS = {"login", "verify", "secure", "account", "update", "confirm", "signin", "banking", "paypal", "credential"};
    private static final Pattern IP_PATTERN = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");

    public RuleResult checkUrl(String url) {
        List<String> reasons = new ArrayList<>();
        int riskScore = 0;

        System.out.println("=== CHECKING URL: " + url);

        // Accumulate multiple suspicious words (no break)
        for (String word : SUSPICIOUS_WORDS) {
            if (url.toLowerCase().contains(word)) {
                reasons.add("Suspicious word: " + word);
                riskScore += 20;
                System.out.println("  +20 for: " + word);
            }
        }

        // No HTTPS
        if (!url.startsWith("https")) {
            reasons.add("No HTTPS encryption");
            riskScore += 30;
            System.out.println("  +30 for no HTTPS");
        }

        // IP address
        if (IP_PATTERN.matcher(url).find()) {
            reasons.add("IP address instead of domain");
            riskScore += 40;
            System.out.println("  +40 for IP address");
        }

        // Long URL
        if (url.length() > 100) {
            reasons.add("Unusually long URL");
            riskScore += 10;
        }

        // Many dots
        int dotCount = url.length() - url.replace(".", "").length();
        if (dotCount > 4) {
            reasons.add("Many subdomains");
            riskScore += 15;
        }

        // URL shortener
        String[] shorteners = {"bit.ly", "tinyurl", "goo.gl", "ow.ly", "is.gd"};
        for (String s : shorteners) {
            if (url.toLowerCase().contains(s)) {
                reasons.add("URL shortener: " + s);
                riskScore += 25;
                break;
            }
        }

        riskScore = Math.min(riskScore, 100);
        boolean isDangerous = riskScore >= 40;

        System.out.println("FINAL RISK: " + riskScore + " -> DANGEROUS: " + isDangerous);
        return new RuleResult(isDangerous, riskScore, reasons);
    }

    public static class RuleResult {
        private final boolean dangerous;
        private final int riskScore;
        private final List<String> reasons;

        public RuleResult(boolean dangerous, int riskScore, List<String> reasons) {
            this.dangerous = dangerous;
            this.riskScore = riskScore;
            this.reasons = reasons;
        }

        public boolean isDangerous() { return dangerous; }
        public int getRiskScore() { return riskScore; }
        public List<String> getReasons() { return reasons; }
        public String getSummary() { return String.join(", ", reasons); }
    }
}