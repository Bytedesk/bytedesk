/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-21 10:09:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 10:11:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.tool;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 函数式天气工具（Function Tool），委托 {@link WeatherService} 获取真实天气数据。
 *
 * <p>实现 {@code Function<ToolWeatherRequest, ToolWeatherResponse>}，可作为 Spring AI
 * function tool 注册；真实数据来源为 Open-Meteo（经 {@link WeatherService} 解析城市经纬度后查询）。</p>
 *
 * <p>历史版本返回硬编码的 {@code 30.0°C}，现已改为真实数据。</p>
 *
 * @see WeatherService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToolWeatherService implements Function<ToolWeatherRequest, ToolWeatherResponse> {

	private final WeatherService weatherService;

	@Override
	public ToolWeatherResponse apply(ToolWeatherRequest request) {
		String location = request != null ? request.location() : null;
		Unit unit = request != null ? request.unit() : Unit.C;
		return weatherService.getCurrentWeather(location, unit);
	}
}

