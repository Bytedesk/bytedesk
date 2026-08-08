package com.bytedesk.core.base;

import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base CRUD tool abstraction for LLM tool-calling.
 *
 * Subclasses provide entity-specific @Tool methods and call protected helpers.
 */
public abstract class BaseTools<TRequest extends BaseRequest, TResponse>
        extends BaseReadonlyTools<TRequest, TResponse> {

    protected BaseTools(
            String entityName,
            Class<TRequest> requestClass,
            BaseRestService<?, TRequest, TResponse> restService,
            ObjectMapper objectMapper) {
        super(entityName, requestClass, restService, objectMapper);
    }

    protected BaseTools(
            String entityName,
            Class<TRequest> requestClass,
            Supplier<? extends BaseRestService<?, TRequest, TResponse>> restServiceSupplier,
            ObjectMapper objectMapper) {
        super(entityName, requestClass, restServiceSupplier, objectMapper);
    }

    protected Object doCreate(String requestJson) {
        TRequest request = parseRequest(requestJson);
        return restService().create(request);
    }

    protected Object doUpdate(String requestJson) {
        TRequest request = parseRequest(requestJson);
        return restService().update(request);
    }

    protected Object doDelete(String requestJson) {
        TRequest request = parseRequest(requestJson);
        restService().delete(request);
        return entityName() + " deleted";
    }

    protected Object doDeleteByUid(String uid) {
        restService().deleteByUid(uid);
        return entityName() + " deleted by uid";
    }
}
