package com.bytedesk.core.base;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base CRUD tool abstraction for LLM tool-calling.
 *
 * Subclasses provide entity-specific @Tool methods and call protected helpers.
 */
public abstract class BaseTools<TRequest extends BaseRequest, TResponse> {

    private final String entityName;
    private final Class<TRequest> requestClass;
    private final BaseRestService<?, TRequest, TResponse> restService;
    private final ObjectMapper objectMapper;

    protected BaseTools(
            String entityName,
            Class<TRequest> requestClass,
            BaseRestService<?, TRequest, TResponse> restService,
            ObjectMapper objectMapper) {
        this.entityName = entityName;
        this.requestClass = requestClass;
        this.restService = restService;
        this.objectMapper = objectMapper;
    }

    protected Object doQueryByUid(String uid, String orgUid) {
        TRequest request = newRequest();
        request.setUid(uid);
        request.setOrgUid(orgUid);
        return restService.queryByUid(request);
    }

    protected Object doQueryByOrg(String requestJson) {
        TRequest request = parseRequest(requestJson);
        return restService.queryByOrg(request);
    }

    protected Object doQueryByUser(String requestJson) {
        TRequest request = parseRequest(requestJson);
        return restService.queryByUser(request);
    }

    protected Object doCreate(String requestJson) {
        TRequest request = parseRequest(requestJson);
        return restService.create(request);
    }

    protected Object doUpdate(String requestJson) {
        TRequest request = parseRequest(requestJson);
        return restService.update(request);
    }

    protected Object doDelete(String requestJson) {
        TRequest request = parseRequest(requestJson);
        restService.delete(request);
        return entityName + " deleted";
    }

    protected Object doDeleteByUid(String uid) {
        restService.deleteByUid(uid);
        return entityName + " deleted by uid";
    }

    private TRequest parseRequest(String requestJson) {
        try {
            return objectMapper.readValue(requestJson, requestClass);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid " + entityName + " request json: " + e.getMessage(), e);
        }
    }

    private TRequest newRequest() {
        try {
            return requestClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Cannot instantiate request class for " + entityName, e);
        }
    }
}
