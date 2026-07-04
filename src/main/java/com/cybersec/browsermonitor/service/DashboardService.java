package com.cybersec.browsermonitor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private DatabaseService databaseService;

    public Map<String, Object> getWeeklyStats() {
        Map<String, Object> result = new HashMap<>();
        result.put("weeklyData", databaseService.getWeeklyStats());
        result.put("topDomains", databaseService.getTopDomains(10));
        return result;
    }

    public String exportCsv() {
        return databaseService.exportAlertsToCsv();
    }
}