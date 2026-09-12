/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-17 09:13:48
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.user;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.exception.EmailNotFoundException;
import com.bytedesk.core.exception.MobileNotFoundException;
import com.bytedesk.core.exception.UserDisabledException;
// import com.bytedesk.core.config.properties.BytedeskProperties;
// import com.bytedesk.core.rbac.user.cache.UserEntityRedisCacheService;
import com.bytedesk.core.utils.CountryCodeUtils;
import com.bytedesk.core.utils.JwtSubject;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;

/**
 * https://wankhedeshubham.medium.com/spring-boot-security-with-userdetailsservice-and-custom-authentication-provider-3df3a188993f
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

    @Cacheable(value = "user", key = "#email + '-' + #platform", unless = "#result == null")
    public Optional<UserEntity> findByEmailAndPlatform(String email, String platform) {
        // 拦截空参数：避免仓储层抛出 IllegalArgumentException 堆栈，按未找到处理
        if (!StringUtils.hasText(email) || !StringUtils.hasText(platform)) {
            return Optional.empty();
        }
        return userRepository.findByEmailAndPlatformAndDeletedFalse(email, platform);
    }

    @Cacheable(value = "user", key = "#mobile + '-' + (T(com.bytedesk.core.utils.CountryCodeUtils).normalize(#country)) + '-' + #platform", unless = "#result == null")
    public Optional<UserEntity> findByMobileAndPlatform(String mobile, String country, String platform) {
        // 拦截空参数：避免仓储层抛出 IllegalArgumentException 堆栈，按未找到处理（country 为空时 normalize 兜底为默认区号）
        if (!StringUtils.hasText(mobile) || !StringUtils.hasText(platform)) {
            return Optional.empty();
        }
        return userRepository.findByMobileAndCountryAndPlatformAndDeletedFalse(
                mobile,
                CountryCodeUtils.normalize(country),
                platform);
    }

    @Cacheable(value = "user", key = "#username + '-' + #platform", unless = "#result == null")
    public Optional<UserEntity> findByUsernameAndPlatform(String username, String platform) {
        // 拦截空参数：避免仓储层抛出 IllegalArgumentException 堆栈，按未找到处理
        if (!StringUtils.hasText(username) || !StringUtils.hasText(platform)) {
            return Optional.empty();
        }
        return userRepository.findByUsernameAndPlatformAndDeletedFalse(username, platform);
    }

	/**
	 * 按 username 查找用户，未命中时回退按 email 查找。
	 * 第三方登录（CAS/LDAP/OIDC 等企业模块）自动注册的用户，username 列可能存的是第三方账号
	 * （如学工号），而签发 JWT 时对有 email 的用户将 email 写入 subject 的 username 字段，
	 * 仅按 username 列查找会抛 UsernameNotFoundException 导致 401。
	 * 回退复用 findByEmailAndPlatform（含 deleted=false 过滤），使两类存量数据都能认证通过。
	 * https://github.com/Bytedesk/bytedesk/pull/27
	 */
	private Optional<UserEntity> findByUsernameOrEmailAndPlatform(String username, String platform) {
		Optional<UserEntity> userOptional = findByUsernameAndPlatform(username, platform);
		if (userOptional.isPresent()) {
			return userOptional;
		}
		// 回退按 email 查找
		return findByEmailAndPlatform(username, platform);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 
		Optional<UserEntity> userOptional = findByUsernameOrEmailAndPlatform(username, PlatformEnum.BYTEDESK.name());
		if (!userOptional.isPresent()) {
			throw new UsernameNotFoundException("username " + username + " is not found");
		}
		if (!userOptional.get().isEnabled()) {
			throw new UserDisabledException("username " + username + " is not enabled");
		}
		return UserDetailsImpl.build(userOptional.get());
	}

	public UserDetailsImpl loadUserByUsernameAndPlatform(String username, String platform) {
		log.debug("loadUserByUsernameAndPlatform username: {}, platform: {}", username, platform);
		//
		Optional<UserEntity> userOptional = findByUsernameOrEmailAndPlatform(username, platform);
		if (!userOptional.isPresent()) {
			throw new UsernameNotFoundException("username " + username + " is not found");
		}
		if (!userOptional.get().isEnabled()) {
			throw new UserDisabledException("username " + username + " is not enabled");
		}
		return UserDetailsImpl.build(userOptional.get());
	}

	public UserDetails loadUserByUsernameAndPlatform(String subject) {
		String username = JSON.parseObject(subject, JwtSubject.class).getUsername();
		String platform = JSON.parseObject(subject, JwtSubject.class).getPlatform();
		// log.debug("loadUserByUsername {}, username {}, platform {}", subject, username, platform);
		//
		Optional<UserEntity> userOptional = findByUsernameOrEmailAndPlatform(username, PlatformEnum.fromValue(platform).name());
		if (!userOptional.isPresent()) {
			throw new UsernameNotFoundException("username " + username + " is not found");
		}
		if (!userOptional.get().isEnabled()) {
			throw new UserDisabledException("username " + username + " is not enabled");
		}
		return UserDetailsImpl.build(userOptional.get());
	}

	public UserDetailsImpl loadUserByEmailAndPlatform(String email, String platform) {
		log.debug("loadUserByEmail {}", email);
		//
		Optional<UserEntity> userOptional = findByEmailAndPlatform(email, platform);
		if (!userOptional.isPresent()) {
			throw new EmailNotFoundException("email " + email + " is not found");
		}
		if (!userOptional.get().isEnabled()) {
			throw new UserDisabledException("email " + email + " is not enabled");
		}
		return UserDetailsImpl.build(userOptional.get());
	}

	public UserDetailsImpl loadUserByMobileAndPlatform(String mobile, String country, String platform) {
		log.debug("loadUserByMobile {}, country {}", mobile, CountryCodeUtils.normalize(country));
		//
		Optional<UserEntity> userOptional = findByMobileAndPlatform(mobile, country, platform);
		if (!userOptional.isPresent()) {
			throw new MobileNotFoundException("mobile " + mobile + " is not found");
		}
		if (!userOptional.get().isEnabled()) {
			throw new UserDisabledException("mobile " + mobile + " is not enabled");
		}
		return UserDetailsImpl.build(userOptional.get());
	}

}
