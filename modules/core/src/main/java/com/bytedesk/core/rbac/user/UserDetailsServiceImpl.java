/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-23 07:53:01
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-09 20:26:59
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.enums.PlatformEnum;
import com.bytedesk.core.exception.EmailNotFoundException;
import com.bytedesk.core.exception.MobileNotFoundException;
import com.bytedesk.core.exception.UserDisabledException;
import com.bytedesk.core.utils.JwtSubject;

import lombok.extern.slf4j.Slf4j;

/**
 * https://wankhedeshubham.medium.com/spring-boot-security-with-userdetailsservice-and-custom-authentication-provider-3df3a188993f
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Cacheable(value = "user", key = "#email", unless = "#result == null")
    public Optional<UserEntity> findByEmailAndPlatform(String email, String platform) {
        return userRepository.findByEmailAndPlatformAndDeletedFalse(email, platform);
    }

    @Cacheable(value = "user", key = "#mobile", unless = "#result == null")
    public Optional<UserEntity> findByMobileAndPlatform(String mobile, String platform) {
        return userRepository.findByMobileAndPlatformAndDeletedFalse(mobile, platform);
    }

    @Cacheable(value = "user", key = "#username", unless = "#result == null")
    public Optional<UserEntity> findByUsernameAndPlatform(String username, String platform) {
        return userRepository.findByUsernameAndPlatformAndDeletedFalse(username, platform);
    }

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// log.debug("loadUserByUsername {}", username);
		//
		Optional<UserEntity> userOptional = findByUsernameAndPlatform(username, PlatformEnum.BYTEDESK.name());
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
		Optional<UserEntity> userOptional = findByUsernameAndPlatform(username, PlatformEnum.fromValue(platform).name());
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

	public UserDetailsImpl loadUserByMobileAndPlatform(String mobile, String platform) {
		log.debug("loadUserByMobile {}", mobile);
		//
		Optional<UserEntity> userOptional = findByMobileAndPlatform(mobile, platform);
		if (!userOptional.isPresent()) {
			throw new MobileNotFoundException("mobile " + mobile + " is not found");
		}
		if (!userOptional.get().isEnabled()) {
			throw new UserDisabledException("mobile " + mobile + " is not enabled");
		}
		return UserDetailsImpl.build(userOptional.get());
	}

}
