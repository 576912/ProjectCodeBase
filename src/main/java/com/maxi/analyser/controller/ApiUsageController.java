package com.maxi.analyser.controller;

import com.maxi.analyser.util.ApiUsage;
import com.maxi.analyser.util.ApiUsageStore;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api-usage")
public class ApiUsageController {

    @PostMapping
    public void trackUsage(@RequestBody ApiUsage usage) {
        ApiUsageStore.addUsage(
                usage.getApiPath(),
                usage.getComponentName()
        );
    }

    @GetMapping
    public Object getAllUsage() {
        return ApiUsageStore.getAll();
    }
}

