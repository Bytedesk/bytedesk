package com.bytedesk.core.rbac.auth;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.token.TokenRepository;
import com.bytedesk.core.rbac.user.UserDetailsServiceImpl;
import com.bytedesk.core.uid.UidUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private BytedeskProperties bytedeskProperties;

    @InjectMocks
    private AuthService authService;

    @Test
    void authenticateWithPlainPasswordReturnsNullWhenUsernameDoesNotExist() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("100@email.com");
        authRequest.setPlatform("BYTEDESK");
        authRequest.setPassword("secret");

        when(userDetailsService.loadUserByUsernameAndPlatform("100@email.com", "BYTEDESK"))
                .thenThrow(new UsernameNotFoundException("username 100@email.com is not found"));

        Authentication authentication = authService.authenticateWithPlainPassword(authRequest);

        assertNull(authentication);
    }

    @Test
    void authenticateWithPasswordHashReturnsNullWhenUsernameDoesNotExist() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("100@email.com");
        authRequest.setPlatform("BYTEDESK");
        authRequest.setPasswordHash("encrypted");
        authRequest.setPasswordSalt("salt");

        when(userDetailsService.loadUserByUsernameAndPlatform("100@email.com", "BYTEDESK"))
                .thenThrow(new UsernameNotFoundException("username 100@email.com is not found"));

        Authentication authentication = authService.authenticateWithPasswordHash(authRequest);

        assertNull(authentication);
    }
}