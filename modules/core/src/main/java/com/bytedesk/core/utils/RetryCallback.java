package com.bytedesk.core.utils;

/**
 * Generic retry callback interface for database operations
 */
@FunctionalInterface
public interface RetryCallback<T> {
    T execute() throws Exception;
} 