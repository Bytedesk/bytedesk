package com.bytedesk.core.rbac.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.role.RoleConsts;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleRestService;
import com.bytedesk.core.uid.UidUtils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 乐观锁冲突测试
 * 测试并发场景下角色分配的乐观锁处理
 */
@ExtendWith(MockitoExtension.class)
class UserServiceOptimisticLockTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RoleRestService roleService;

    @Mock
    private BytedeskProperties bytedeskProperties;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private EntityManager entityManager;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private BytedeskEventPublisher bytedeskEventPublisher;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;
    private RoleEntity testRole;

    @BeforeEach
    void setUp() {
        testUser = UserEntity.builder()
                .uid("test-user-uid")
                .username("testuser")
                .email("test@example.com")
                .build();
        testUser.setId(1L);
        testUser.setVersion(1);

        testRole = RoleEntity.builder()
                .name(RoleConsts.ROLE_MEMBER)
                .description("Member role")
                .build();
        testRole.setId(3L);
        testRole.setVersion(1);
        testRole.setUid("test-role-uid");
    }

    @Test
    void testAddRoleWithOptimisticLockException() {
        // Given
        when(roleService.findByNamePlatform(RoleConsts.ROLE_MEMBER))
                .thenReturn(Optional.of(testRole));

        // 第一次调用entityManager.merge时抛出乐观锁异常
        when(entityManager.merge(testRole))
                .thenThrow(new OptimisticLockException("Optimistic lock conflict"));

        // 重新获取时返回最新的角色实体
        RoleEntity freshRole = RoleEntity.builder()
                .name(RoleConsts.ROLE_MEMBER)
                .description("Member role")
                .build();
        freshRole.setId(3L);
        freshRole.setVersion(2); // 版本号已更新
        freshRole.setUid("test-role-uid");

        when(roleService.findByNamePlatform(RoleConsts.ROLE_MEMBER))
                .thenReturn(Optional.of(testRole)) // 第一次调用
                .thenReturn(Optional.of(freshRole)); // 第二次调用（重试时）

        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // When
        UserEntity result = userService.addRole(testUser, RoleConsts.ROLE_MEMBER);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUid(), result.getUid());

        // 验证调用次数
        verify(roleService, times(2)).findByNamePlatform(RoleConsts.ROLE_MEMBER);
        verify(entityManager, times(1)).merge(testRole);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testAddRoleUserWithOptimisticLockException() {
        // Given
        when(roleService.findByNamePlatform(RoleConsts.ROLE_USER))
                .thenReturn(Optional.of(testRole));

        // 第一次调用entityManager.merge时抛出乐观锁异常
        when(entityManager.merge(testRole))
                .thenThrow(new OptimisticLockException("Optimistic lock conflict"));

        // 重新获取时返回最新的角色实体
        RoleEntity freshRole = RoleEntity.builder()
                .name(RoleConsts.ROLE_USER)
                .description("User role")
                .build();
        freshRole.setId(3L);
        freshRole.setVersion(2);
        freshRole.setUid("test-role-uid");

        when(roleService.findByNamePlatform(RoleConsts.ROLE_USER))
                .thenReturn(Optional.of(testRole)) // 第一次调用
                .thenReturn(Optional.of(freshRole)); // 第二次调用（重试时）

        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // When
        UserEntity result = userService.addRoleUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUid(), result.getUid());

        // 验证调用次数
        verify(roleService, times(2)).findByNamePlatform(RoleConsts.ROLE_USER);
        verify(entityManager, times(1)).merge(testRole);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testAddRoleWithOptimisticLockExceptionAndFailedRetry() {
        // Given
        when(roleService.findByNamePlatform(RoleConsts.ROLE_MEMBER))
                .thenReturn(Optional.of(testRole));

        // 第一次调用entityManager.merge时抛出乐观锁异常
        when(entityManager.merge(testRole))
                .thenThrow(new OptimisticLockException("Optimistic lock conflict"));

        // 重新获取时返回空（模拟角色被删除的情况）
        when(roleService.findByNamePlatform(RoleConsts.ROLE_MEMBER))
                .thenReturn(Optional.of(testRole)) // 第一次调用
                .thenReturn(Optional.empty()); // 第二次调用（重试时返回空）

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.addRole(testUser, RoleConsts.ROLE_MEMBER);
        });

        assertTrue(exception.getMessage().contains("重新获取角色失败"));

        // 验证调用次数
        verify(roleService, times(2)).findByNamePlatform(RoleConsts.ROLE_MEMBER);
        verify(entityManager, times(1)).merge(testRole);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testConcurrentRoleAssignment() throws InterruptedException {
        // Given
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger retryCount = new AtomicInteger(0);

        // 模拟并发情况下的乐观锁冲突
        when(roleService.findByNamePlatform(RoleConsts.ROLE_MEMBER))
                .thenReturn(Optional.of(testRole));

        when(entityManager.merge(testRole))
                .thenAnswer(invocation -> {
                    // 模拟一半的请求遇到乐观锁冲突
                    if (retryCount.getAndIncrement() < threadCount / 2) {
                        throw new OptimisticLockException("Optimistic lock conflict");
                    }
                    return testRole;
                });

        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // When
        for (int i = 0; i < threadCount; i++) {
            final int taskId = i;
            executorService.submit(() -> {
                try {
                    UserEntity user = UserEntity.builder()
                            .uid("test-user-" + taskId)
                            .username("testuser" + taskId)
                            .build();
                    user.setId((long) taskId);

                    UserEntity result = userService.addRole(user, RoleConsts.ROLE_MEMBER);
                    if (result != null) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // 记录异常但不影响测试
                    System.err.println("Task " + taskId + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有任务完成
        latch.await();
        executorService.shutdown();

        // Then
        assertTrue(successCount.get() > 0, "至少应有一些成功的操作");
        System.out.println("成功操作数: " + successCount.get());
        System.out.println("重试次数: " + retryCount.get());
    }

    @Test
    void testAddRoleSuccessWithoutOptimisticLock() {
        // Given
        when(roleService.findByNamePlatform(RoleConsts.ROLE_MEMBER))
                .thenReturn(Optional.of(testRole));
        when(entityManager.merge(testRole)).thenReturn(testRole);
        when(userRepository.save(any(UserEntity.class))).thenReturn(testUser);

        // When
        UserEntity result = userService.addRole(testUser, RoleConsts.ROLE_MEMBER);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getUid(), result.getUid());

        // 验证正常流程只调用一次
        verify(roleService, times(1)).findByNamePlatform(RoleConsts.ROLE_MEMBER);
        verify(entityManager, times(1)).merge(testRole);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testAddRoleWithRoleNotFound() {
        // Given
        when(roleService.findByNamePlatform(anyString()))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.addRole(testUser, "NON_EXISTENT_ROLE");
        });

        assertEquals("Role not found..!!", exception.getMessage());

        // 验证没有进行后续操作
        verify(entityManager, never()).merge(any());
        verify(userRepository, never()).save(any());
    }
}
