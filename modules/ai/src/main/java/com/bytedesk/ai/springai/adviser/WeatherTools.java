/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-08-10 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-10 22:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ai.springai.adviser;

import org.springframework.ai.tool.annotation.Tool;

import com.bytedesk.ai.tool.test.WeatherService;

import lombok.extern.slf4j.Slf4j;

/**
 * 演示用天气工具（{@code @Tool} 注解风格），委托 {@link WeatherService} 获取真实天气数据。
 *
 * <p>配合 {@code ChatClient.builder().defaultTools(new WeatherTools(weatherService))} 使用，
 * 框架会自动注册 {@code ToolCallingAdvisor} 处理工具调用循环。</p>
 *
 * <p>历史版本返回硬编码的"晴 25°C"，现已改为真实数据（Open-Meteo）。</p>
 */
@Slf4j
public class WeatherTools {

	private final WeatherService weatherService;

	/**
	 * @param weatherService 真实天气数据来源
	 */
	public WeatherTools(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	@Tool(description = "查询指定城市的当前天气情况。输入参数 city 为城市名称（中文或英文），"
			+ "返回该城市当前天气的一句话描述，包含天气状况和气温信息。")
	public String getWeather(String city) {
		log.info("[tool] getWeather called: city={}", city);
		return weatherService.getWeatherText(city);
	}

}
