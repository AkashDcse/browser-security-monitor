package com.cybersec.browsermonitor.controller;

import com.cybersec.browsermonitor.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats/weekly")
    public Object weeklyStats() {
        return dashboardService.getWeeklyStats();
    }

    @GetMapping("/export/csv")
    public ResponseEntity<String> exportCsv() {
        String csv = dashboardService.exportCsv();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=security_alerts.csv")
                .body(csv);
    }
}
