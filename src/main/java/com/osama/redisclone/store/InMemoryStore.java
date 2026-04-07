package com.osama.redisclone.store;

import com.osama.redisclone.model.Entry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStore {
    private final Map<String, Entry> data;

    public InMemoryStore() {
        this.data = new ConcurrentHashMap<>();
    }

    public void set(String key, String value) {
        data.put(key, new Entry(value));
    }

    public void set(String key, String value, long ttlSeconds) {
        long now = System.currentTimeMillis();
        long expiresAt = Math.addExact(now, Math.multiplyExact(ttlSeconds, 1000));
        data.put(key, new Entry(value, expiresAt));
    }

    public String get(String key) {
        long now = System.currentTimeMillis();
        Entry entry = data.get(key);

        if (entry == null) {
            return null;
        }

        if (entry.isExpired(now)) {
            data.remove(key, entry);
            return null;
        }

        return entry.getValue();
    }

    public boolean delete(String key) {
        return data.remove(key) != null;
    }
}