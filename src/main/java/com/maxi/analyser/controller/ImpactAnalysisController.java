package com.maxi.analyser.controller;

import com.maxi.analyser.util.ApiUsageStore;
import com.maxi.analyser.util.SwaggerStore;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/impact")
public class ImpactAnalysisController {

    @PostMapping("/snapshot")
    public String takeSnapshot(@RequestBody String swaggerJson) {
        SwaggerStore.OLD_SWAGGER = swaggerJson;
        return "Swagger snapshot stored";
    }

    @PostMapping("/analyze")
    public Object analyzeImpact(@RequestBody String newSwagger) {

        // SIMPLE demo logic
        // Assume /api/products changed
        String changedApi = "/api/products";

        Set<String> impactedComponents =
                ApiUsageStore.getComponents(changedApi);

        Map<String, Object> result = new HashMap<>();
        result.put("changedApi", changedApi);
        result.put("impactedComponents", impactedComponents);

        return result;
    }
}

