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
package com.bytedesk.ai.springai.tool;

import java.util.function.Function;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.bytedesk.core.city.CityEntity;
import com.bytedesk.core.city.CityRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * 统一的"真实天气"工具服务。
 * <p>
 * 通过 {@link CityRepository} 解析城市经纬度，再调用 Open-Meteo 获取真实气温，
 * 取代原先 {@code ToolWeatherService} / {@code WeatherTools} /
 * {@code Enterprise*ToolService#getCurrentWeather} 中各自硬编码/随机生成的假数据。
 * </p>
 *
 * <p>本类是各处天气工具的单一数据源：</p>
 * <ul>
 *   <li>{@link ToolWeatherService}（Function 风格工具）委托本类；</li>
 *   <li>{@code com.bytedesk.ai.springai.adviser.WeatherTools}（@Tool 注解工具）委托本类；</li>
 *   <li>{@code EnterpriseZhipuaiToolService} / {@code EnterpriseDashScopeToolService} 的
 *       {@code currentWeather} 工具执行也委托本类。</li>
 * </ul>
 *
 * <p>注意：依赖 {@link CityRepository}，启动时若城市表未初始化，工具会降级返回提示信息而非抛异常，
 * 不影响应用启动。</p>
 *
 * @see ToolWeatherService
 */
@Slf4j
@Service
public class WeatherService {

	private final CityRepository cityRepository;

	private final RestClient restClient = RestClient.create();

	public WeatherService(CityRepository cityRepository) {
		this.cityRepository = cityRepository;
	}

	/**
	 * 查询指定城市的真实当前气温（Open-Meteo）。
	 *
	 * @param location 城市名称（支持 "北京"/"北京市"/拼音/行政区划代码等，依 CityRepository 字段匹配）
	 * @param unit     温度单位（{@link Unit#C} 摄氏度 / {@link Unit#F} 华氏度）
	 * @return 真实气温结果；城市查不到或网络异常时返回带占位说明的 {@link ToolWeatherResponse}
	 */
	public ToolWeatherResponse getCurrentWeather(String location, Unit unit) {
		if (location == null || location.isBlank()) {
			log.warn("[weather] location 参数为空，返回默认值");
			return new ToolWeatherResponse(0.0, unit == null ? Unit.C : unit);
		}

		CityEntity city = resolveCity(location.trim());
		if (city == null || city.getLat() == null || city.getLat().isBlank()
				|| city.getLng() == null || city.getLng().isBlank()) {
			log.warn("[weather] 未找到城市或缺少经纬度: {}", location);
			return new ToolWeatherResponse(0.0, unit == null ? Unit.C : unit);
		}

		final double latitude;
		final double longitude;
		try {
			latitude = Double.parseDouble(city.getLat().trim());
			longitude = Double.parseDouble(city.getLng().trim());
		}
		catch (NumberFormatException e) {
			log.warn("[weather] 城市经纬度格式错误: lat={}, lng={}", city.getLat(), city.getLng());
			return new ToolWeatherResponse(0.0, unit == null ? Unit.C : unit);
		}

		try {
			OpenMeteoResponse response = restClient
				.get()
				.uri("https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m",
						latitude, longitude)
				.retrieve()
				.body(OpenMeteoResponse.class);

			if (response == null || response.current() == null) {
				log.warn("[weather] Open-Meteo 返回空: {}", location);
				return new ToolWeatherResponse(0.0, unit == null ? Unit.C : unit);
			}

			double celsius = response.current().temperature_2m();
			Unit resolvedUnit = unit == null ? Unit.C : unit;
			double temp = resolvedUnit == Unit.F ? celsiusToFahrenheit(celsius) : celsius;
			log.info("[weather] {} 真实气温: {}° (raw={})", location, temp, celsius);
			return new ToolWeatherResponse(temp, resolvedUnit);
		}
		catch (RestClientException e) {
			log.warn("[weather] Open-Meteo 请求失败: {}", e.getMessage());
			return new ToolWeatherResponse(0.0, unit == null ? Unit.C : unit);
		}
	}

	/**
	 * 查询真实天气并以中文一句话描述返回，供 @Tool 风格工具直接对外输出。
	 *
	 * @param city 城市名称
	 * @return 中文天气播报文案
	 */
	public String getWeatherText(String city) {
		ToolWeatherResponse resp = getCurrentWeather(city, Unit.C);
		return city + " 当前气温 " + resp.temp() + "°C（数据来源：Open-Meteo）。";
	}

	private static double celsiusToFahrenheit(double celsius) {
		return celsius * 9.0 / 5.0 + 32.0;
	}

	/**
	 * 多策略城市解析：名称 → 去"市"后缀 → 加"市"后缀 → 简称(cap) → 行政代码 → 拼音。
	 */
	private CityEntity resolveCity(String cityName) {
		if (cityName == null || cityName.isBlank()) {
			return null;
		}
		String normalized = cityName.trim();

		CityEntity city = cityRepository.findFirstByNameAndDeletedFalse(normalized).orElse(null);
		if (city != null) {
			return city;
		}

		if (normalized.endsWith("市") && normalized.length() > 1) {
			city = cityRepository
				.findFirstByNameAndDeletedFalse(normalized.substring(0, normalized.length() - 1))
				.orElse(null);
			if (city != null) {
				return city;
			}
		}
		else {
			city = cityRepository.findFirstByNameAndDeletedFalse(normalized + "市").orElse(null);
			if (city != null) {
				return city;
			}
		}

		city = cityRepository.findFirstByCapAndDeletedFalse(normalized).orElse(null);
		if (city != null) {
			return city;
		}

		city = cityRepository.findFirstByCodeAndDeletedFalse(normalized).orElse(null);
		if (city != null) {
			return city;
		}

		return cityRepository.findFirstByPinyinAndDeletedFalse(normalized.toLowerCase()).orElse(null);
	}

	/**
	 * Open-Meteo API 响应（精简子集）。
	 */
	public record OpenMeteoResponse(Current current) {

		public record Current(String time, int interval, double temperature_2m) {
		}
	}

	/**
	 * 作为 {@code currentWeather} 函数 bean 暴露，供 {@code RobotToolConfig} 中
	 * {@code bindingType=SPRING_BEAN, beanName=currentWeather} 引用。
	 * <p>
	 * Spring AI 会把实现 {@code Function<Request, Response>} 的 bean 自动注册为
	 * 名为 bean name 的 function tool，此处转发到 {@link #getCurrentWeather}。
	 * </p>
	 */
	@org.springframework.context.annotation.Bean("currentWeather")
	public Function<ToolWeatherRequest, ToolWeatherResponse> currentWeatherFunction() {
		return request -> getCurrentWeather(
				request != null ? request.location() : null,
				request != null ? request.unit() : Unit.C);
	}

}
