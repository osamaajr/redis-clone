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

    public String get(String key) {
        Entry entry = data.get(key);
        return entry != null ? entry.getValue() : null;
    }

    public boolean delete(String key) {
        return data.remove(key) != null;
    }
}