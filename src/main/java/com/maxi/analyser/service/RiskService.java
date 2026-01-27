package com.maxi.analyser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service

public class RiskService {

    Logger log= LoggerFactory.getLogger(RiskService.class);

    private final GitService git;

    @Value("${risk.repo.root}")
    private String repoRoot;

    @Value("${risk.backend.java.path}")
    private String backendJavaPath;

    @Value("${risk.frontend.src.path}")
    private String frontendSrcPath;

    @Value("${risk.report.path}")
    private String reportPath;

    @Value("${risk.backend.src.rel}")
    private String backendSrcRel;


    // class-level base path: @RequestMapping("/api/products") OR @RequestMapping(value="/api/products")
    private static final Pattern CLASS_BASE_MAPPING =
            Pattern.compile("@RequestMapping\\s*\\(\\s*(?:(?:value|path)\\s*=\\s*)?\"([^\"]+)\"");

    // method-level mapping with explicit path: @GetMapping("/x") OR @GetMapping(value="/x") OR @GetMapping(path="/x")
    private static final Pattern METHOD_MAPPING_WITH_PATH =
            Pattern.compile("@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)\\s*\\(\\s*(?:(?:value|path)\\s*=\\s*)?\"([^\"]*)\"");

    // method-level mapping WITHOUT any args: @GetMapping OR @GetMapping()
    private static final Pattern METHOD_MAPPING_NO_ARGS =
            Pattern.compile("@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)\\s*(?:\\(\\s*\\))?\\s*");

    public RiskService(GitService git) {
        this.git = git;
    }

    public void process(String beforeSha, String afterSha) {
        try {
            log.info("Processing risk for commits {} -> {}", beforeSha, afterSha);

            // 1) Ensure we have the SHAs locally
            git.fetchAll();
            String base = beforeSha;
            String head = afterSha;

            if (!git.hasCommit(head)) {
                log.warn("Head SHA {} not found locally. Using HEAD.", head);
                head = "HEAD";
            }
            if (!git.hasCommit(base)) {
                log.warn("Base SHA {} not found locally. Falling back to HEAD~1.", base);
                // Best-effort fallback
                base = "HEAD~1";
            }

            // 2) Get changed files
            List<String> changed = git.diffNameOnly(base, head);
            log.info("Changed files ({}): {}", changed.size(), changed);

            String backendPrefix = backendSrcRel.replace("\\", "/") + "/";

            List<String> backendApiFiles = changed.stream()
                    .map(s -> s.replace("\\", "/"))
                    .filter(f -> f.startsWith(backendPrefix))
                    .filter(f -> f.contains("Controller"))
                    .filter(f -> f.endsWith(".java"))
                    .toList();

            log.info("Backend API Files Changed: {}", backendApiFiles);

            // 4) Detect changed API endpoints
            Set<String> changedApis = detectChangedApiEndpoints(backendApiFiles);
            log.info("Detected changed APIs: {}", changedApis);

            // 5) Map APIs → Angular components
            Map<String, List<String>> map = mapApisToUiComponents(changedApis);
            log.info("API → UI Map: {}", map);

            // 6) Compute risk (simple heuristic)
            List<RiskItem> items = computeRisk(map);

            // 7) Save report
            saveRiskReport(items);
            log.info("Risk report saved at {}", reportPath);


            log.info("repoRoot={}", repoRoot);
            log.info("backendJavaPath exists? {}", Files.exists(Paths.get(backendJavaPath)));
            log.info("frontendSrcPath exists? {}", Files.exists(Paths.get(frontendSrcPath)));
            log.info("backendPrefix (relative)={}", backendPrefix);

        } catch (Exception e) {
            log.error("Error processing risk: ", e);
        }
    }

    private String pathUnix(String p) { return p.replace('\\','/'); }



    private Set<String> detectChangedApiEndpoints(List<String> controllerFilesRel) throws IOException {
        Set<String> apis = new HashSet<>();

        for (String rel : controllerFilesRel) {
            Path file = Paths.get(repoRoot, rel);
            log.info("Scanning controller file: {}", file);

            if (!Files.exists(file)) {
                log.warn("Controller file not found on disk: {}", file);
                continue;
            }

            String content = Files.readString(file);

            // 1) Find base path from class-level @RequestMapping
            String basePath = "";
            Matcher baseMatcher = CLASS_BASE_MAPPING.matcher(content);
            if (baseMatcher.find()) {
                basePath = baseMatcher.group(1).trim(); // e.g. "/api/products"
            }
            log.info("Base path detected: {}", basePath);

            // 2) Extract all method-level mappings with explicit paths
            Set<String> foundFullPaths = new HashSet<>();
            Matcher m1 = METHOD_MAPPING_WITH_PATH.matcher(content);
            while (m1.find()) {
                String methodPath = m1.group(2).trim();  // e.g. "/{id}" or "" if explicitly empty
                String full = normalizePath(basePath, methodPath);
                if (!full.isBlank()) {
                    foundFullPaths.add(full);
                }
            }

            // 3) Handle mappings without args (e.g., @GetMapping with no "()")
            // If controller contains "@GetMapping" but NOT "@GetMapping(" with a quote,
            // it means there exists a no-path mapping. That should map to basePath.
            // Same for Post/Put/Delete/Patch.
            if (containsNoPathMapping(content, "GetMapping"))  foundFullPaths.add(basePath);
            if (containsNoPathMapping(content, "PostMapping")) foundFullPaths.add(basePath);
            if (containsNoPathMapping(content, "PutMapping"))  foundFullPaths.add(basePath);
            if (containsNoPathMapping(content, "DeleteMapping")) foundFullPaths.add(basePath);
            if (containsNoPathMapping(content, "PatchMapping")) foundFullPaths.add(basePath);

            // add to final set
            apis.addAll(foundFullPaths);
        }

        // remove empty strings just in case
        apis.removeIf(s -> s == null || s.isBlank());

        return apis;
    }

    private boolean containsNoPathMapping(String content, String mappingName) {
        // matches "@GetMapping" or "@GetMapping()" without a quoted argument
        // and avoids counting "@GetMapping("/x")"
        Pattern p = Pattern.compile("@" + mappingName + "\\s*(\\(\\s*\\))?(?!\\s*\\(\\s*\"|\\s*\\(\\s*(value|path)\\s*=\\s*\")");
        return p.matcher(content).find();
    }

    private String normalizePath(String base, String method) {
        if (base == null) base = "";
        if (method == null) method = "";
        base = base.trim();
        method = method.trim();

        if (base.isEmpty() && method.isEmpty()) return "";

        if (method.isEmpty()) return base; // @GetMapping with explicit "" or no args -> base

        // ensure proper "/" joining
        if (!base.endsWith("/") && !method.startsWith("/")) return base + "/" + method;
        if (base.endsWith("/") && method.startsWith("/")) return base + method.substring(1);
        return base + method;
    }

    private Map<String, List<String>> mapApisToUiComponents(Set<String> apis) throws IOException {
        Map<String, List<String>> map = new HashMap<>();
        Path feSrc = Paths.get(frontendSrcPath);
        if (!Files.exists(feSrc)) {
            throw new NoSuchFileException(frontendSrcPath);
        }

        List<Path> tsFiles = new ArrayList<>();
        try (var stream = Files.walk(feSrc)) {
            stream.filter(p -> p.toString().endsWith(".ts")).forEach(tsFiles::add);
        }

        for (String api : apis) {
            List<String> comps = new ArrayList<>();
            for (Path ts : tsFiles) {
                String content = Files.readString(ts);
                if (content.contains(api)) {
                    comps.add(componentNameFromPath(ts));
                }
            }
            map.put(api, comps);
        }
        return map;
    }

    private String componentNameFromPath(Path ts) {
        String fname = ts.getFileName().toString();
        if (fname.endsWith(".component.ts")) {
            return fname.substring(0, fname.indexOf(".component.ts"));
        }
        return fname.replace(".ts", "");
    }

    private List<RiskItem> computeRisk(Map<String, List<String>> apiToComps) {
        List<RiskItem> list = new ArrayList<>();
        // Simple: each API → components risk = 0.7, can refine later with lines changed, churn, etc.
        for (var e : apiToComps.entrySet()) {
            String api = e.getKey();
            for (String comp : e.getValue()) {
                list.add(new RiskItem(comp, api, 0.7));
            }
        }
        return list;
    }

    private void saveRiskReport(List<RiskItem> items) throws IOException {
        Path out = Paths.get(reportPath);
        Files.createDirectories(out.getParent());

        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"impactedComponents\": [\n");
        for (int i = 0; i < items.size(); i++) {
            var it = items.get(i);
            sb.append("    {")
                    .append("\"component\":\"").append(escape(it.component)).append("\",")
                    .append("\"api\":\"").append(escape(it.api)).append("\",")
                    .append("\"risk\":").append(it.risk)
                    .append("}");
            if (i < items.size()-1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}\n");
        Files.writeString(out, sb.toString());
    }

    private String escape(String s) { return s.replace("\"", "\\\""); }

    private record RiskItem(String component, String api, double risk) {}
}
