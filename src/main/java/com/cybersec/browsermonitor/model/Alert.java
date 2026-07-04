package com.cybersec.browsermonitor.model;

import java.time.LocalDateTime;

public class Alert {
    private Long id;
    private String url;
    private LocalDateTime timestamp;
    private String  reason;
    private int riskScore;

    public Alert(String url,LocalDateTime timestamp,String reason,int riskScore){
        this.url=url;
        this.timestamp=timestamp;
        this.reason=reason;
        this.riskScore=riskScore;
    }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public int getRiskScore() { return riskScore; }
    public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
}
