/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 15:02:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-03 12:24:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter;

import java.util.Arrays;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
// , "io.antmedia", "org.red5", "org.webrtc"
@ComponentScan(basePackages = {"com.bytedesk"})
@EnableJpaRepositories(basePackages = "com.bytedesk")
@EntityScan(basePackages = "com.bytedesk")
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
public class StarterApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
		int cliExitCode = tryRunCli(args);
		if (cliExitCode >= 0) {
			if (cliExitCode != 0) {
				System.exit(cliExitCode);
			}
			return;
		}
		SpringApplication.run(StarterApplication.class, args);
	}

	private static int tryRunCli(String[] args) {
		if (args == null || args.length == 0) {
			return -1;
		}
		String launcher = args[0];
		String[] cliArgs = Arrays.copyOfRange(args, 1, args.length);
		if ("cli".equals(launcher)) {
			return invokeCli("com.bytedesk.cli.core.BytedeskCli", cliArgs, "OSS CLI");
		}
		if ("enterprise-cli".equals(launcher)) {
			return invokeCli("com.bytedesk.cli.EnterpriseCli", cliArgs, "enterprise CLI");
		}
		return -1;
	}

	private static int invokeCli(String className, String[] cliArgs, String displayName) {
		try {
			Class<?> cliClass = Class.forName(className);
			Object cli = cliClass.getDeclaredConstructor().newInstance();
			Object exitCode = cliClass.getMethod("run", String[].class).invoke(cli, (Object) cliArgs);
			return ((Integer) exitCode).intValue();
		} catch (ClassNotFoundException exception) {
			throw new IllegalStateException(
				"Failed to launch " + displayName + ". CLI classes are not available on the runtime classpath. "
					+ "Build and run from the root reactor, or use the packaged bytedesk-starter jar.",
				exception);
		} catch (ReflectiveOperationException exception) {
			throw new IllegalStateException("Failed to invoke " + displayName + ".", exception);
		}
	}
}
