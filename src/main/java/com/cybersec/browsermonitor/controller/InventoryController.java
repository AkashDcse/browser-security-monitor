package com.cybersec.browsermonitor.controller;

import com.cybersec.browsermonitor.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class InventoryController {

    @Autowired
    private DatabaseService databaseService;

    @PostMapping("/extension-inventory")
    public void recordExtension(@RequestBody Map<String, Object> extData) {
        databaseService.saveExtensionInventory(extData);
    }
}
