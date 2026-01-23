package com.maxi.analyser.controller;

import com.maxi.analyser.entity.GitHubPushEvent;
import com.maxi.analyser.service.RiskService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebhookController {

    private final RiskService riskService;

    public WebhookController(RiskService riskService) {
        this.riskService = riskService;
    }

    @PostMapping("/github/webhook")
    public String handlePush(@RequestBody GitHubPushEvent event) {
        riskService.process(event.getBefore(), event.getAfter());

        return "OK";
    }

}
