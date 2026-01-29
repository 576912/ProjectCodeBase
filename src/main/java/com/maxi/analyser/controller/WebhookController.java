package com.maxi.analyser.controller;

import com.maxi.analyser.entity.GitHubPushEvent;
import com.maxi.analyser.service.RiskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebhookController {
    Logger log= LoggerFactory.getLogger(WebhookController.class);

    private final RiskService riskService;

    public WebhookController(RiskService riskService) {
        this.riskService = riskService;
    }


    @PostMapping("/github/webhook")
    public String handlePush(@RequestBody GitHubPushEvent event) {
        riskService.process(event.getBefore(), event.getAfter());
        log.info("Event before: "+event.getBefore());
        log.info("Event after: "+event.getAfter());
        return "OK";
    }

}
