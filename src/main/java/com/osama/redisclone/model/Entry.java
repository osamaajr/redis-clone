package com.osama.redisclone.model;

public class Entry {
    private final String value;
    private final Long expiresAt;

    public Entry(String value) {
        this(value, null);
    }

    public Entry(String value, Long expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public String getValue() {
        return value;
    }

    public Long getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired(long currentTimeMillis) {
        return expiresAt != null && currentTimeMillis > expiresAt;
    }

    public boolean isExpired() {
        return isExpired(System.currentTimeMillis());
    }
}