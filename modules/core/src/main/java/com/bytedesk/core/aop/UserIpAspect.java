package com.bytedesk.core.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.bytedesk.core.ip.IpUtils;

import lombok.AllArgsConstructor;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class UserIpAspect {

	private final HttpServletRequest request;

	@Pointcut("@annotation(com.bytedesk.core.annotation.UserIp)")
	public void logUserIp() {

	}

	@After("logUserIp()")
	public void after(){

		String ip = IpUtils.getClientIp(request);

		log.info("User IP: {}",  ip);
	}

}
