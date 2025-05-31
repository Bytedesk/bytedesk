package com.bytedesk.core.rbac.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import jakarta.persistence.EntityManager;

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
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.role.RoleConsts;
import com.bytedesk.core.rbac.role.RoleEntity;
import com.bytedesk.core.rbac.role.RoleRestService;
import com.bytedesk.core.uid.UidUtils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Test for verifying the fix of detached entity issue in UserService.addRole method
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceDetachedEntityTest {

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
    private OrganizationRepository organizationRepository;

    @Mock
    private BytedeskEventPublisher bytedeskEventPublisher;

    @Mock
    private AuthService authService;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserService userService;

    private UserEntity testUser;
    private RoleEntity testRole;
    private RoleEntity managedRole;
    private OrganizationEntity testOrganization;

    @BeforeEach
    void setUp() {
        // Create test organization
        testOrganization = OrganizationEntity.builder()
                .id(1L)
                .name("Test Organization")
                .build();

        // Create test user
        testUser = UserEntity.builder()
                .id(1L)
                .username("testuser")
                .currentOrganization(testOrganization)
                .build();

        // Create test role (simulating a detached entity)
        testRole = RoleEntity.builder()
                .id(1L)
                .name(RoleConsts.ROLE_ADMIN)
                .build();

        // Create managed role (what EntityManager.merge should return)
        managedRole = RoleEntity.builder()
                .id(1L)
                .name(RoleConsts.ROLE_ADMIN)
                .build();
    }

    @Test
    void testAddRole_ShouldMergeDetachedRoleEntity() {
        // Given
        when(roleService.findByNamePlatform(RoleConsts.ROLE_ADMIN))
                .thenReturn(Optional.of(testRole));
        when(entityManager.merge(testRole)).thenReturn(managedRole);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        UserEntity result = userService.addRole(testUser, RoleConsts.ROLE_ADMIN);

        // Then
        verify(roleService).findByNamePlatform(RoleConsts.ROLE_ADMIN);
        verify(entityManager).merge(testRole); // Verify that merge is called
        verify(userRepository).save(testUser);
        assertNotNull(result);
        assertEquals(testUser, result);
    }

    @Test
    void testAddRole_ShouldThrowExceptionWhenRoleNotFound() {
        // Given
        when(roleService.findByNamePlatform(RoleConsts.ROLE_ADMIN))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.addRole(testUser, RoleConsts.ROLE_ADMIN);
        });

        assertEquals("Role not found..!!", exception.getMessage());
        verify(entityManager, never()).merge(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testAddRoleUser_ShouldMergeDetachedRoleEntity() {
        // Given
        when(roleService.findByNamePlatform(RoleConsts.ROLE_USER))
                .thenReturn(Optional.of(testRole));
        when(entityManager.merge(testRole)).thenReturn(managedRole);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        UserEntity result = userService.addRoleUser(testUser);

        // Then
        verify(roleService).findByNamePlatform(RoleConsts.ROLE_USER);
        verify(entityManager).merge(testRole); // Verify that merge is called
        verify(userRepository).save(testUser);
        assertNotNull(result);
        assertEquals(testUser, result);
    }
}
