package com.bytedesk.core.rbac.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.exception.BusinessException;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.rbac.role.RoleRestService;
import com.bytedesk.core.rbac.token.TokenRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.PasswordCryptoUtils;

import jakarta.persistence.EntityManager;

/**
 * 密码密文传输契约测试：
 * - 密文模式（ciphertext+salt）正确解密并更新密码；
 * - 旧格式（无任何密文元数据）明文回退兼容；
 * - 畸形密文/混传明文/缺字段 fail-closed 拒绝；
 * - resetPassword 不在解析失败时更新密码。
 */
class UserServicePasswordTransportTest {

    private static final String SALT = "testSalt12345678";

    private UserRepository userRepository;
    private AuthService authService;
    private BCryptPasswordEncoder passwordEncoder;
    private UserService userService;

    private UserEntity currentUser;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = mock(AuthService.class);
        passwordEncoder = new BCryptPasswordEncoder();

        userService = new UserService(
                userRepository,
                mock(ModelMapper.class),
                mock(RoleRestService.class),
                mock(BytedeskProperties.class),
                passwordEncoder,
                mock(UidUtils.class),
                mock(OrganizationRepository.class),
                mock(BytedeskEventPublisher.class),
                authService,
                mock(TokenRestService.class),
                mock(MemberRepository.class));
        ReflectionTestUtils.setField(userService, "entityManager", mock(EntityManager.class));

        currentUser = UserEntity.builder().uid("user-1").username("tester").platform("BYTEDESK").build();
        when(authService.getUser()).thenReturn(currentUser);
    }

    private void stubUserWithPassword(String rawPassword) {
        currentUser.setPassword(rawPassword == null ? null : passwordEncoder.encode(rawPassword));
        when(userRepository.findByUid("user-1")).thenReturn(Optional.of(currentUser));
        when(userRepository.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private UserRequest ciphertextRequest(String oldPassword, String newPassword) {
        return UserRequest.builder()
                .oldPasswordCiphertext(oldPassword == null ? null
                        : PasswordCryptoUtils.encryptPassword(oldPassword, SALT))
                .newPasswordCiphertext(PasswordCryptoUtils.encryptPassword(newPassword, SALT))
                .passwordSalt(SALT)
                .build();
    }

    @Test
    void changePasswordWithCiphertextShouldDecryptAndUpdate() {
        stubUserWithPassword("oldPass123");

        UserResponse response = userService.changePassword(ciphertextRequest("oldPass123", "newPass456"));

        assertThat(response).isNotNull();
        assertThat(passwordEncoder.matches("newPass456", currentUser.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("oldPass123", currentUser.getPassword())).isFalse();
    }

    @Test
    void changePasswordWithWrongOldCiphertextShouldRejectAndNotUpdate() {
        stubUserWithPassword("oldPass123");

        assertThatThrownBy(() -> userService.changePassword(ciphertextRequest("wrongOld", "newPass456")))
                .isInstanceOf(BusinessException.class);

        assertThat(passwordEncoder.matches("oldPass123", currentUser.getPassword())).isTrue();
    }

    @Test
    void changePasswordWithoutOldCiphertextWhenUserHasPasswordShouldReject() {
        stubUserWithPassword("oldPass123");

        UserRequest request = UserRequest.builder()
                .newPasswordCiphertext(PasswordCryptoUtils.encryptPassword("newPass456", SALT))
                .passwordSalt(SALT)
                .build();

        // 旧密码为 null 时 matches(null, hash) 为 false，应走旧密码错误分支
        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("old.password.wrong");

        assertThat(passwordEncoder.matches("oldPass123", currentUser.getPassword())).isTrue();
    }

    @Test
    void changePasswordWithoutOldCiphertextWhenUserHasNoPasswordShouldUpdate() {
        stubUserWithPassword(null); // 手机号登录未设置密码场景

        UserRequest request = UserRequest.builder()
                .newPasswordCiphertext(PasswordCryptoUtils.encryptPassword("newPass456", SALT))
                .passwordSalt(SALT)
                .build();

        UserResponse response = userService.changePassword(request);

        assertThat(response).isNotNull();
        assertThat(passwordEncoder.matches("newPass456", currentUser.getPassword())).isTrue();
    }

    @Test
    void changePasswordLegacyPlaintextShouldStillWork() {
        stubUserWithPassword("oldPass123");

        UserRequest request = UserRequest.builder()
                .oldPassword("oldPass123")
                .newPassword("newPass456")
                .build();

        UserResponse response = userService.changePassword(request);

        assertThat(response).isNotNull();
        assertThat(passwordEncoder.matches("newPass456", currentUser.getPassword())).isTrue();
    }

    @Test
    void ciphertextModeMissingSaltShouldFailClosed() {
        stubUserWithPassword("oldPass123");

        UserRequest request = UserRequest.builder()
                .oldPasswordCiphertext(PasswordCryptoUtils.encryptPassword("oldPass123", SALT))
                .newPasswordCiphertext(PasswordCryptoUtils.encryptPassword("newPass456", SALT))
                .build(); // 无 passwordSalt

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(BusinessException.class);

        assertThat(passwordEncoder.matches("oldPass123", currentUser.getPassword())).isTrue();
    }

    @Test
    void ciphertextModeMissingNewPasswordCiphertextShouldFailClosed() {
        stubUserWithPassword("oldPass123");

        UserRequest request = UserRequest.builder()
                .oldPasswordCiphertext(PasswordCryptoUtils.encryptPassword("oldPass123", SALT))
                .passwordSalt(SALT)
                .build(); // 缺 newPasswordCiphertext

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(BusinessException.class);

        assertThat(passwordEncoder.matches("oldPass123", currentUser.getPassword())).isTrue();
    }

    @Test
    void ciphertextMixedWithPlaintextShouldFailClosed() {
        stubUserWithPassword("oldPass123");

        UserRequest request = UserRequest.builder()
                .oldPasswordCiphertext(PasswordCryptoUtils.encryptPassword("oldPass123", SALT))
                .newPasswordCiphertext(PasswordCryptoUtils.encryptPassword("newPass456", SALT))
                .passwordSalt(SALT)
                .newPassword("evil-new") // 密文与明文混传
                .build();

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(BusinessException.class);

        assertThat(passwordEncoder.matches("oldPass123", currentUser.getPassword())).isTrue();
    }

    @Test
    void invalidCiphertextPayloadShouldFailClosed() {
        stubUserWithPassword("oldPass123");

        UserRequest request = UserRequest.builder()
                .oldPasswordCiphertext(PasswordCryptoUtils.encryptPassword("oldPass123", SALT))
                .newPasswordCiphertext("not-base64-cipher!!!")
                .passwordSalt(SALT)
                .build();

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(BusinessException.class);

        assertThat(passwordEncoder.matches("oldPass123", currentUser.getPassword())).isTrue();
    }

    @Test
    void resetPasswordWithCiphertextShouldDecryptAndUpdate() {
        stubUserWithPassword("oldPass123");

        UserRequest request = UserRequest.builder()
                .newPasswordCiphertext(PasswordCryptoUtils.encryptPassword("resetPass789", SALT))
                .passwordSalt(SALT)
                .build();

        UserService.TransportedPasswords passwords = userService.resolveTransportedPasswords(request);
        UserResponse response = userService.resetPassword(request, passwords);

        assertThat(response).isNotNull();
        assertThat(passwordEncoder.matches("resetPass789", currentUser.getPassword())).isTrue();
    }

    @Test
    void resetPasswordLegacyPlaintextShouldStillWork() {
        stubUserWithPassword("oldPass123");

        UserRequest request = UserRequest.builder().newPassword("resetPass789").build();

        UserService.TransportedPasswords passwords = userService.resolveTransportedPasswords(request);
        UserResponse response = userService.resetPassword(request, passwords);

        assertThat(response).isNotNull();
        assertThat(passwordEncoder.matches("resetPass789", currentUser.getPassword())).isTrue();
    }
}
