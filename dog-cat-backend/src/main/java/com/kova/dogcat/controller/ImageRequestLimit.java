package com.kova.dogcat.controller;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

final class ImageRequestLimit {
    private static final long INTERVAL = TimeUnit.SECONDS.toNanos(5);
    private final Map<String, Long> accepted = new LinkedHashMap<>();

    synchronized long retryAfter(String address) {
        long now = System.nanoTime();
        Iterator<Long> entries = accepted.values().iterator();
        while (entries.hasNext()) {
            if (now - entries.next() < INTERVAL) break;
            entries.remove();
        }
        Long previous = accepted.get(address);
        if (previous == null) {
            accepted.put(address, now);
            return 0;
        }
        return Math.ceilDiv(INTERVAL - (now - previous), TimeUnit.SECONDS.toNanos(1));
    }
}
