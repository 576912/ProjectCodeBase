package com.maxi.analyser.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitService {

    Logger log= LoggerFactory.getLogger(GitService.class);

    @Value("${risk.repo.root}")
    private String repoRoot;

    private int run(List<String> cmd, StringBuilder out, StringBuilder err) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(new File(repoRoot));
        pb.redirectErrorStream(false);
        Process p = pb.start();

        try (BufferedReader rOut = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8));
             BufferedReader rErr = new BufferedReader(new InputStreamReader(p.getErrorStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = rOut.readLine()) != null) out.append(line).append("\n");
            while ((line = rErr.readLine()) != null) err.append(line).append("\n");
        }
        int code = p.waitFor();
        if (code != 0) log.warn("Command failed ({}): {}\nSTDERR: {}", code, cmd, err.toString());
        return code;
    }

    public void fetchAll() throws IOException, InterruptedException {
        StringBuilder out = new StringBuilder(), err = new StringBuilder();
        run(List.of("git", "fetch", "--all", "--prune"), out, err);
    }

    public boolean hasCommit(String sha) throws IOException, InterruptedException {
        StringBuilder out = new StringBuilder(), err = new StringBuilder();
        int code = run(List.of("git", "rev-parse", "--verify", sha), out, err);
        return code == 0;
    }

    public List<String> diffNameOnly(String base, String head) throws IOException, InterruptedException {
        StringBuilder out = new StringBuilder(), err = new StringBuilder();
        int code = run(List.of("git", "diff", "--name-only", base, head), out, err);
        List<String> files = new ArrayList<>();

        if (code == 0) {
            for (String line : out.toString().split("\n")) {
                String trimmed = line.trim();
                if (!trimmed.isEmpty()) files.add(trimmed);
            }
        }
        return files;
    }
}


