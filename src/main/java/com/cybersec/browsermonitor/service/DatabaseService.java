



package com.cybersec.browsermonitor.service;

import com.cybersec.browsermonitor.model.Alert;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {

    private static final String DB_URL = "jdbc:sqlite:alerts.db";

    @PostConstruct
    public void init() {
        // Existing alerts table
        String alertsTable = "CREATE TABLE IF NOT EXISTS alerts (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "url TEXT NOT NULL," +
                "timestamp TEXT NOT NULL," +
                "reason TEXT NOT NULL," +
                "risk_score INTEGER NOT NULL)";
        // New tables
        String dailyStats = "CREATE TABLE IF NOT EXISTS daily_stats (date TEXT PRIMARY KEY, total_checks INTEGER DEFAULT 0, dangerous_count INTEGER DEFAULT 0, avg_risk REAL DEFAULT 0)";
        String auditLog = "CREATE TABLE IF NOT EXISTS audit_log (id INTEGER PRIMARY KEY AUTOINCREMENT, user_id TEXT, action TEXT, url TEXT, timestamp TEXT, ip_address TEXT)";
        String dlpEvents = "CREATE TABLE IF NOT EXISTS dlp_events (id INTEGER PRIMARY KEY AUTOINCREMENT, url TEXT, event_type TEXT, content_preview TEXT, timestamp TEXT)";
        String extInventory = "CREATE TABLE IF NOT EXISTS extension_inventory (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, ext_id TEXT, version TEXT, enabled INTEGER, permissions TEXT, timestamp TEXT)";
        String trainingFeedback = "CREATE TABLE IF NOT EXISTS training_feedback (id INTEGER PRIMARY KEY AUTOINCREMENT, helpful INTEGER, timestamp TEXT)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(alertsTable);
            stmt.execute(dailyStats);
            stmt.execute(auditLog);
            stmt.execute(dlpEvents);
            stmt.execute(extInventory);
            stmt.execute(trainingFeedback);
            System.out.println("✅ Database ready with all tables");
        } catch (SQLException e) {
            System.err.println("❌ DB init error: " + e.getMessage());
        }
    }

    // ========== Existing methods (unchanged) ==========
    public void saveAlert(Alert alert) {
        String sql = "INSERT INTO alerts (url, timestamp, reason, risk_score) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, alert.getUrl());
            pstmt.setString(2, alert.getTimestamp().toString());
            pstmt.setString(3, alert.getReason());
            pstmt.setInt(4, alert.getRiskScore());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Save failed: " + e.getMessage());
        }
    }

    public List<Alert> getRecentAlerts(int limit) {
        List<Alert> alerts = new ArrayList<>();
        String sql = "SELECT url, timestamp, reason, risk_score FROM alerts ORDER BY timestamp DESC LIMIT ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Alert alert = new Alert(
                        rs.getString("url"),
                        LocalDateTime.parse(rs.getString("timestamp")),
                        rs.getString("reason"),
                        rs.getInt("risk_score")
                );
                alerts.add(alert);
            }
        } catch (SQLException e) {
            System.err.println("❌ Query error: " + e.getMessage());
        }
        return alerts;
    }

    public int getTotalAlerts() {
        String sql = "SELECT COUNT(*) FROM alerts";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getInt(1);
        } catch (SQLException e) {
            return 0;
        }
    }

    public double getAverageRiskScore() {
        String sql = "SELECT AVG(risk_score) FROM alerts";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.getDouble(1);
        } catch (SQLException e) {
            return 0.0;
        }
    }

    // ========== NEW methods for Dashboard & DLP & Inventory & Training ==========

    public List<Map<String, Object>> getWeeklyStats() {
        List<Map<String, Object>> stats = new ArrayList<>();
        String sql = "SELECT date, total_checks, dangerous_count, avg_risk FROM daily_stats WHERE date >= date('now', '-7 days') ORDER BY date";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> day = new HashMap<>();
                day.put("date", rs.getString("date"));
                day.put("total_checks", rs.getInt("total_checks"));
                day.put("dangerous_count", rs.getInt("dangerous_count"));
                day.put("avg_risk", rs.getDouble("avg_risk"));
                stats.add(day);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    public List<Map<String, Object>> getTopDomains(int limit) {
        List<Map<String, Object>> domains = new ArrayList<>();
        String sql = "SELECT SUBSTR(url, INSTR(url, '//') + 2, INSTR(SUBSTR(url, INSTR(url, '//') + 2), '/') - 1) AS domain, COUNT(*) as cnt FROM alerts GROUP BY domain ORDER BY cnt DESC LIMIT ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("domain", rs.getString("domain"));
                entry.put("count", rs.getInt("cnt"));
                domains.add(entry);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return domains;
    }

    public String exportAlertsToCsv() {
        StringBuilder csv = new StringBuilder("URL,Timestamp,Reason,RiskScore\n");
        String sql = "SELECT url, timestamp, reason, risk_score FROM alerts ORDER BY timestamp DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                csv.append("\"").append(rs.getString("url").replace("\"", "\"\"")).append("\",")
                        .append(rs.getString("timestamp")).append(",")
                        .append("\"").append(rs.getString("reason").replace("\"", "\"\"")).append("\",")
                        .append(rs.getInt("risk_score")).append("\n");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return csv.toString();
    }

    public void saveDlpEvent(Map<String, Object> event) {
        String sql = "INSERT INTO dlp_events (url, event_type, content_preview, timestamp) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, (String) event.get("url"));
            pstmt.setString(2, (String) event.get("event_type"));
            pstmt.setString(3, (String) event.get("content_preview"));
            pstmt.setString(4, LocalDateTime.now().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void saveExtensionInventory(Map<String, Object> ext) {
        String sql = "INSERT INTO extension_inventory (name, ext_id, version, enabled, permissions, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, (String) ext.get("name"));
            pstmt.setString(2, (String) ext.get("id"));
            pstmt.setString(3, (String) ext.get("version"));
            pstmt.setInt(4, (Boolean) ext.get("enabled") ? 1 : 0);
            pstmt.setString(5, ext.get("permissions").toString());
            pstmt.setString(6, LocalDateTime.now().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void saveTrainingFeedback(boolean helpful) {
        String sql = "INSERT INTO training_feedback (helpful, timestamp) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, helpful ? 1 : 0);
            pstmt.setString(2, LocalDateTime.now().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}