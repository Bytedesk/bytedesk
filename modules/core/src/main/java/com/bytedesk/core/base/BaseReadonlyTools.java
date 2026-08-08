package com.bytedesk.core.base;

import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base readonly tool abstraction for LLM tool-calling.
 *
 * Subclasses can expose query-style tool methods and reuse the protected
 * request parsing and rest-service helpers without inheriting write helpers.
 */
public abstract class BaseReadonlyTools<TRequest extends BaseRequest, TResponse> {

    private final String entityName;
    private final Class<TRequest> requestClass;
    private final Supplier<? extends BaseRestService<?, TRequest, TResponse>> restServiceSupplier;
    private final ObjectMapper objectMapper;

    protected BaseReadonlyTools(
            String entityName,
            Class<TRequest> requestClass,
            BaseRestService<?, TRequest, TResponse> restService,
            ObjectMapper objectMapper) {
        this(entityName, requestClass, () -> restService, objectMapper);
    }

    protected BaseReadonlyTools(
            String entityName,
            Class<TRequest> requestClass,
            Supplier<? extends BaseRestService<?, TRequest, TResponse>> restServiceSupplier,
            ObjectMapper objectMapper) {
        this.entityName = entityName;
        this.requestClass = requestClass;
        this.restServiceSupplier = restServiceSupplier;
        this.objectMapper = objectMapper;
    }

    protected Object doQueryByUid(String uid, String orgUid) {
        TRequest request = newRequest();
        request.setUid(uid);
        request.setOrgUid(orgUid);
        return restService().queryByUid(request);
    }

    protected Object doQueryByOrg(String requestJson) {
        TRequest request = parseRequest(requestJson);
        return restService().queryByOrg(request);
    }

    protected Object doQueryByUser(String requestJson) {
        TRequest request = parseRequest(requestJson);
        return restService().queryByUser(request);
    }

    protected BaseRestService<?, TRequest, TResponse> restService() {
        return restServiceSupplier.get();
    }

    protected TRequest parseRequest(String requestJson) {
        try {
            return objectMapper.readValue(requestJson, requestClass);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid " + entityName + " request json: " + e.getMessage(), e);
        }
    }

    protected TRequest newRequest() {
        try {
            return requestClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot instantiate request class for " + entityName, e);
        }
    }

    protected String entityName() {
        return entityName;
    }
}