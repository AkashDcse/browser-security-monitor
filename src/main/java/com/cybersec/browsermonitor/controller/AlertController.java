package com.cybersec.browsermonitor.controller;

import  com.cybersec.browsermonitor.model.Alert;
import  com.cybersec.browsermonitor.service.DatabaseService;
import com.cybersec.browsermonitor.service.RuleEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins="*")
public class AlertController {

@Autowired
    private RuleEngineService ruleEngine;

@Autowired
    private DatabaseService databaseService;

@PostMapping("/check")
    public Map<String,Object> checkUrl(@RequestBody Map<String,String> request)
{
    String url=request.get("url");
    Map<String,Object> response=new HashMap<>();
    if(url==null || url.isEmpty()){
        response.put("error","No URL provided");
        return response;
    }
    System.out.println("Checking URL: " + url);
    var result=ruleEngine.checkUrl(url);
    if(result.isDangerous()){
        Alert alert=new Alert(url,LocalDateTime.now(),result.getSummary(),result.getRiskScore());
        databaseService.saveAlert(alert);
        System.out.println("Alert :"+url+" - "+result.getSummary());
    }
    response.put("dangerous",result.isDangerous());
    response.put("risk_score",result.getRiskScore());
    response.put("reasons",result.getReasons());
    response.put("timestamp",LocalDateTime.now().toString());
    return response;

}
@GetMapping("/alerts")
    public Map<String,Object> getAlerts(){
    List<Alert> alerts=databaseService.getRecentAlerts(100);
    Map<String,Object> response=new HashMap<>();
    response.put("alerts",alerts);
    response.put("count",alerts.size());
    return  response;
}
    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_alerts", databaseService.getTotalAlerts());
        stats.put("average_risk_score", databaseService.getAverageRiskScore());
        stats.put("status", "monitoring_active");
        stats.put("server_time", LocalDateTime.now().toString());
        return stats;
    }
    @PostMapping("/training-feedback")
    public void trainingFeedback(@RequestBody Map<String, Boolean> feedback) {
        boolean helpful = feedback.getOrDefault("helpful", false);
        databaseService.saveTrainingFeedback(helpful);
    }
@GetMapping("/")
    public Map<String,Object> home(){
    Map<String,Object> info=new HashMap<>();
    info.put("project","Browser Security Monitor");
    info.put("version", "1.0.0");
    info.put("status","running");
    info.put("endpoints",List.of("POST /check","GET /alerts","GET /stats"));
    return info;


}
}
