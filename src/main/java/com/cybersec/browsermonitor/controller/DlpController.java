package com.cybersec.browsermonitor.controller;

import com.cybersec.browsermonitor.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class DlpController {

    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/dlp-event")
    public void logDlpEvent(@RequestBody Map<String, Object> event) {
        databaseService.saveDlpEvent(event);
    }
}
