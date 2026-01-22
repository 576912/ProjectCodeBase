//package com.maxi.analyser.service;
//
//
//
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//import org.springframework.util.FileCopyUtils;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.*;
//import java.util.*;
//import java.util.regex.*;
//
//@Service
//
//public class RiskService {
//    Logger log= LoggerFactory.getLogger(RiskService.class);
//
//    private static final String BACKEND_PATH = "ecommerce-backend-master/src/main/java";
//    private static final String FRONTEND_PATH = "ecom-backend-master/UI/api-impact-ui/src/app";
//    private static final String REPORT_PATH = "ecommerce-backend-master/src/main/resources/risk-report.json";
//
//    /**
//     * Main Entry Method
//     */
//    public void process(String beforeSha, String afterSha) {
//        try {
//            log.info("Processing risk for commits {} -> {}", beforeSha, afterSha);
//
//            // 1. Get changed files
//            List<String> changedFiles = getChangedFiles(beforeSha, afterSha);
//            log.info("Changed files: {}", changedFiles);
//
//            // 2. Filter backend API files (only controllers)
//            List<String> backendApiFiles = filterBackendApiFiles(changedFiles);
//            log.info("Backend API Files Changed: {}", backendApiFiles);
//
//            // 3. Detect changed API endpoints
//            Set<String> changedApis = detectChangedApiEndpoints(backendApiFiles);
//            log.info("Detected changed APIs: {}", changedApis);
//
//            // 4. Map APIs → Angular Components
//            Map<String, List<String>> apiToComponents = mapApisToUiComponents(changedApis);
//            log.info("API → UI Map: {}", apiToComponents);
//
//            // 5. Generate risk score
//            List<RiskItem> riskItems = calculateRisk(apiToComponents);
//
//            // 6. Save report
//            saveRiskReport(riskItems);
//
//        } catch (Exception e) {
//            log.error("Error processing risk: ", e);
//        }
//    }
//
//    /**
//     * Run git diff command
//     */
//    private List<String> getChangedFiles(String beforeSha, String afterSha) throws IOException, InterruptedException {
//        ProcessBuilder pb = new ProcessBuilder(
//                "git", "diff", "--name-only", beforeSha, afterSha
//        );
//        pb.redirectErrorStream(true);
//
//        Process process = pb.start();
//        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//        List<String> files = new ArrayList<>();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            files.add(line);
//        }
//
//        process.waitFor();
//        return files;
//    }
//
//    /**
//     * Returns only backend controller files
//     */
//    private List<String> filterBackendApiFiles(List<String> files) {
//        List<String> controllerFiles = new ArrayList<>();
//        for (String file : files) {
//            if (file.startsWith(BACKEND_PATH) && file.contains("Controller")) {
//                controllerFiles.add(file);
//            }
//        }
//        return controllerFiles;
//    }
//
//    /**
//     * Detect API annotations like:
//     *   @GetMapping("/api/products")
//     *   @PostMapping("/api/cart")
//     */
//    private Set<String> detectChangedApiEndpoints(List<String> apiFiles) throws IOException {
//        Set<String> apis = new HashSet<>();
//
//        Pattern p = Pattern.compile("@(GetMapping|PostMapping|PutMapping|DeleteMapping)\\(\"(.*?)\"\\)");
//
//        for (String filePath : apiFiles) {
//            File file = new File(filePath);
//            String content = Files.readString(file.toPath());
//
//            Matcher matcher = p.matcher(content);
//            while (matcher.find()) {
//                apis.add(matcher.group(2)); // the "/api/xyz" part
//            }
//        }
//
//        return apis;
//    }
//
//    /**
//     * Scan Angular files for usage of such APIs
//     */
//    private Map<String, List<String>> mapApisToUiComponents(Set<String> apis) throws IOException {
//        Map<String, List<String>> apiToComponent = new HashMap<>();
//
//        List<Path> tsFiles = listAllTsFiles(FRONTEND_PATH);
//
//        for (String api : apis) {
//            apiToComponent.put(api, new ArrayList<>());
//
//            for (Path tsFile : tsFiles) {
//                String content = Files.readString(tsFile);
//
//                if (content.contains(api)) {
//                    apiToComponent.get(api).add(getComponentName(tsFile));
//                }
//            }
//        }
//
//        return apiToComponent;
//    }
//
//    private List<Path> listAllTsFiles(String folderPath) throws IOException {
//        List<Path> list = new ArrayList<>();
//
//        Files.walk(Paths.get(folderPath))
//                .filter(path -> path.toString().endsWith(".ts"))
//                .forEach(list::add);
//
//        return list;
//    }
//
//    /**
//     * Extract Angular Component Name from File Path
//     */
//    private String getComponentName(Path tsFile) {
//        String name = tsFile.getFileName().toString();
//        return name.replace(".component.ts", "");
//    }
//
//    /**
//     * Risk calculation: simple MVP
//     */
//    private List<RiskItem> calculateRisk(Map<String, List<String>> apiToComponents) {
//        List<RiskItem> list = new ArrayList<>();
//
//        Random random = new Random();
//
//        for (Map.Entry<String, List<String>> entry : apiToComponents.entrySet()) {
//            for (String comp : entry.getValue()) {
//                list.add(new RiskItem(comp, entry.getKey(), random.nextDouble()));
//            }
//        }
//        return list;
//    }
//
//    /**
//     * Save to JSON file
//     */
//    private void saveRiskReport(List<RiskItem> items) throws IOException {
//        StringBuilder sb = new StringBuilder();
//        sb.append("[\n");
//        for (RiskItem item : items) {
//            sb.append("  {\n");
//            sb.append("    \"component\": \"" + item.component + "\",\n");
//            sb.append("    \"api\": \"" + item.api + "\",\n");
//            sb.append("    \"risk\": " + item.risk + "\n");
//            sb.append("  },\n");
//        }
//        if (!items.isEmpty()) sb.deleteCharAt(sb.length() - 2); // remove last comma
//        sb.append("]");
//
//        Files.writeString(Paths.get(REPORT_PATH), sb.toString(), StandardCharsets.UTF_8);
//    }
//
//
//    /**
//     * Inner class
//     */
//    record RiskItem(String component, String api, double risk) {}
//}
//

package com.maxi.analyser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
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

//    private static final Pattern MAPPING =
//            Pattern.compile("@(GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)\\s*\\(\\s*\"([^\"]+)\"");


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

            // 3) Filter backend controller java files
//            List<String> backendApiFiles = changed.stream()
//                    .filter(f -> f.replace('\\','/').startsWith(pathUnix(backendJavaPath)))
//                    .filter(f -> f.contains("Controller"))
//                    .toList();
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

//    private Set<String> detectChangedApiEndpoints(List<String> apiFilesRelative) throws IOException {
//        Set<String> apis = new HashSet<>();
//        for (String rel : apiFilesRelative) {
//            Path file = Paths.get(repoRoot, rel);
//            log.info("Scanning controller file: {}", file);
//            if (!Files.exists(file)) {
//                log.warn("Changed file not found on disk: {}", file);
//                continue;
//            }
//            String content = Files.readString(file);
//            Matcher m = MAPPING.matcher(content);
//            while (m.find()) {
//                apis.add(m.group(2)); // "/api/..." path
//            }
//        }
//        return apis;
//    }


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
