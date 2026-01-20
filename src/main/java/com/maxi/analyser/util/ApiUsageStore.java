package com.maxi.analyser.util;

import java.util.*;

public class ApiUsageStore {

    private static final Map<String, Set<String>> STORE = new HashMap<>();

    public static void addUsage(String api, String component) {
        STORE.computeIfAbsent(api, k -> new HashSet<>()).add(component);
    }

    public static Set<String> getComponents(String api) {
        return STORE.getOrDefault(api, Set.of());
    }

    public static Map<String, Set<String>> getAll() {
        return STORE;
    }
}
