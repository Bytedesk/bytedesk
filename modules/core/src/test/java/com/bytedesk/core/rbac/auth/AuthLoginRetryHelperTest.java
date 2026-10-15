package com.bytedesk.core.rbac.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.redis.RedisLoginRetryService;
import com.bytedesk.core.utils.JsonResult;

@ExtendWith(MockitoExtension.class)
class AuthLoginRetryHelperTest {

    @Mock
    private RedisLoginRetryService redisLoginRetryService;

    @Mock
    private BytedeskProperties bytedeskProperties;

    @InjectMocks
    private AuthLoginRetryHelper authLoginRetryHelper;

    private AuthLoginRetryHelper.LoginRetryConfig config(int maxRetry, int lockMinutes) {
        return new AuthLoginRetryHelper.LoginRetryConfig(maxRetry, lockMinutes);
    }

    @Test
    void checkUserLockedReturnsErrorResponseWhenLockedWithRemainingTime() {
        when(redisLoginRetryService.isUserLocked("admin")).thenReturn(true);
        when(redisLoginRetryService.getLockRemainingTime("admin")).thenReturn(300L);

        ResponseEntity<?> response = authLoginRetryHelper.checkUserLocked("admin");

        assertNotNull(response);
        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertEquals(-1, body.getCode());
        // 锁定期间不得解锁、不得重置失败次数
        verify(redisLoginRetryService, never()).unlockUser(anyString());
    }

    @Test
    void checkUserLockedUnlocksAndResetsCountWhenLockExpired() {
        when(redisLoginRetryService.isUserLocked("admin")).thenReturn(true);
        when(redisLoginRetryService.getLockRemainingTime("admin")).thenReturn(0L);

        ResponseEntity<?> response = authLoginRetryHelper.checkUserLocked("admin");

        assertNull(response);
        // 锁定到期后：解锁并重置失败次数，避免残留计数导致立即被再次锁定
        verify(redisLoginRetryService).unlockUser("admin");
        verify(redisLoginRetryService).resetLoginFailedCount("admin");
    }

    @Test
    void checkMaxRetryReachedLocksUserAndResetsFailedCount() {
        when(redisLoginRetryService.getLoginFailedCount("admin")).thenReturn(3);

        ResponseEntity<?> response = authLoginRetryHelper.checkMaxRetryReached("admin", config(3, 10));

        assertNotNull(response);
        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertEquals(-1, body.getCode());
        // 锁定时同步重置失败次数，确保10分钟锁定到期后可重新尝试
        verify(redisLoginRetryService).lockUser("admin", 600L);
        verify(redisLoginRetryService).resetLoginFailedCount("admin");
    }

    @Test
    void checkMaxRetryReachedReturnsNullWhenBelowLimit() {
        when(redisLoginRetryService.getLoginFailedCount("admin")).thenReturn(2);

        ResponseEntity<?> response = authLoginRetryHelper.checkMaxRetryReached("admin", config(3, 10));

        assertNull(response);
        verify(redisLoginRetryService, never()).lockUser(anyString(), anyLong());
    }

    @Test
    void handleLoginFailureLocksAndResetsCountWhenMaxRetryReached() {
        when(redisLoginRetryService.incrementLoginFailedCount("admin", 3600L)).thenReturn(3);

        ResponseEntity<?> response = authLoginRetryHelper.handleLoginFailure("admin", config(3, 10), null);

        assertNotNull(response);
        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertEquals(-1, body.getCode());
        verify(redisLoginRetryService).lockUser("admin", 600L);
        verify(redisLoginRetryService).resetLoginFailedCount("admin");
    }

    @Test
    void handleLoginFailureReturnsAttemptsRemainingWhenBelowLimit() {
        when(redisLoginRetryService.incrementLoginFailedCount("admin", 3600L)).thenReturn(1);

        ResponseEntity<?> response = authLoginRetryHelper.handleLoginFailure("admin", config(3, 10), null);

        assertNotNull(response);
        JsonResult<?> body = (JsonResult<?>) response.getBody();
        assertEquals(-1, body.getCode());
        verify(redisLoginRetryService, never()).lockUser(anyString(), anyLong());
    }
}
