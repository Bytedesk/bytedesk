package com.bytedesk.core.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.client.ResourceAccessException;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleInvalidDataAccessApiUsageExceptionMapsLegacyOrgAccessMessageToI18nKey() {
        InvalidDataAccessApiUsageException exception = new InvalidDataAccessApiUsageException(
                "wrapped",
                new IllegalArgumentException("No permission to access data of other organizations"));

        ResponseEntity<?> response = handler.handleInvalidDataAccessApiUsageException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);

        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
        assertThat(body.getMessage()).isEqualTo(I18Consts.I18N_ORGANIZATION_ACCESS_DENIED);
    }

    @Test
    void handleIllegalArgumentExceptionReturnsBadRequestForMissingOrgUid() {
        IllegalArgumentException exception = new IllegalArgumentException(I18Consts.I18N_ORG_UID_REQUIRED);

        ResponseEntity<?> response = handler.handleIllegalArgumentException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);

        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(body.getMessage()).isEqualTo(I18Consts.I18N_ORG_UID_REQUIRED);
    }

    @Test
    void handleResourceAccessExceptionReturnsServiceUnavailableI18nKey() {
        ResourceAccessException exception = new ResourceAccessException("upstream timeout");

        ResponseEntity<?> response = handler.handleResourceAccessException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);

        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value());
        assertThat(body.getMessage()).isEqualTo(I18Consts.I18N_EXTERNAL_SERVICE_TEMPORARILY_UNAVAILABLE);
    }

    @Test
    void handleOptimisticLockReturnsConflictI18nKey() {
        ObjectOptimisticLockingFailureException exception = new ObjectOptimisticLockingFailureException(
                "thread",
                "uid-1");

        ResponseEntity<?> response = handler.handleOptimisticLock(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);

        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(body.getMessage()).isEqualTo(I18Consts.I18N_RESOURCE_CONCURRENTLY_MODIFIED);
    }

    @Test
    void handleDataIntegrityViolationExceptionReturnsDuplicateI18nKey() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException(
                "Duplicate entry 'x' for key 'uk_test'");

        ResponseEntity<?> response = handler.handleDataIntegrityViolationException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);

        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(body.getMessage()).isEqualTo(I18Consts.I18N_DATA_ALREADY_EXISTS);
    }

    @Test
    void handleAsyncRequestNotUsableExceptionReturnsI18nKey() {
        AsyncRequestNotUsableException exception = new AsyncRequestNotUsableException("broken pipe");

        ResponseEntity<?> response = handler.handleAsyncRequestNotUsableException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);

        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getMessage()).isEqualTo(I18Consts.I18N_CONNECTION_NO_LONGER_AVAILABLE);
    }

    @Test
    void handleRuntimeExceptionReturnsI18nKeyForAgentExists() {
        RuntimeException exception = new RuntimeException(I18Consts.I18N_AGENT_EXISTS);

        ResponseEntity<?> response = handler.handleRuntimeException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);

        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(body.getMessage()).isEqualTo(I18Consts.I18N_AGENT_EXISTS);
    }

    @Test
    void handleRuntimeExceptionWithBlankMessageFallsBackToInternalServerErrorKey() {
        RuntimeException exception = new RuntimeException();

        ResponseEntity<?> response = handler.handleRuntimeException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(JsonResult.class);

        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertThat(body.getCode()).isEqualTo(500);
        assertThat(body.getMessage()).isEqualTo(I18Consts.I18N_INTERNAL_SERVER_ERROR);
    }
}