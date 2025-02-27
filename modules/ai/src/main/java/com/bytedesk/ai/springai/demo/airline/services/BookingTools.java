/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-27 11:18:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-27 15:08:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.demo.airline.services;

import java.time.LocalDate;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.ai.springai.demo.airline.data.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.NestedExceptionUtils;

@Slf4j
@Configuration
public class BookingTools {

	@Autowired
	private FlightBookingService flightBookingService;

	private static final String BOOKING_DETAILS_SCHEMA = """
			{
				"type": "object",
				"properties": {
					"bookingNumber": {
						"type": "string",
						"description": "预订号"
					},
					"name": {
						"type": "string",
						"description": "客户姓名"
					}
				},
				"required": ["bookingNumber", "name"]
			}
			""";

	private static final String CHANGE_BOOKING_SCHEMA = """
			{
				"type": "object",
				"properties": {
					"bookingNumber": {
						"type": "string",
						"description": "预订号"
					},
					"name": {
						"type": "string",
						"description": "客户姓名"
					},
					"date": {
						"type": "string",
						"description": "新的航班日期"
					},
					"from": {
						"type": "string",
						"description": "出发地"
					},
					"to": {
						"type": "string",
						"description": "目的地"
					}
				},
				"required": ["bookingNumber", "name", "date", "from", "to"]
			}
			""";

	private static final String CANCEL_BOOKING_SCHEMA = """
			{
				"type": "object",
				"properties": {
					"bookingNumber": {
						"type": "string",
						"description": "预订号"
					},
					"name": {
						"type": "string",
						"description": "客户姓名"
					}
				},
				"required": ["bookingNumber", "name"]
			}
			""";

	public record BookingDetailsRequest(String bookingNumber, String name) {
	}

	public record ChangeBookingDatesRequest(String bookingNumber, String name, String date, String from, String to) {
	}

	public record CancelBookingRequest(String bookingNumber, String name) {
	}

	@JsonInclude(Include.NON_NULL)
	public record BookingDetails(String bookingNumber, String name, LocalDate date, BookingStatus bookingStatus,
			String from, String to, String bookingClass) {
	}

	@Bean("getBookingDetails")
	@Description("获取机票预定详细信息")
	public ToolCallback getBookingDetails() {
		return new ToolCallback() {
			@Override
			public ToolDefinition getToolDefinition() {
				return ToolDefinition.builder()
					.name("getBookingDetails")
					.description("获取机票预定详细信息")
					.inputSchema(BOOKING_DETAILS_SCHEMA)
					.build();
			}

			@Override
			public String call(String json) {
				try {
					BookingDetailsRequest request = JSON.parseObject(json, BookingDetailsRequest.class);
					BookingDetails details = flightBookingService.getBookingDetails(request.bookingNumber(), request.name());
					return JSON.toJSONString(details);
				} catch (Exception e) {
					log.warn("Booking details: {}", NestedExceptionUtils.getMostSpecificCause(e).getMessage());
					return "获取预订信息失败: " + e.getMessage();
				}
			}
		};
	}

	@Bean("changeBooking")
	@Description("修改机票预定日期")
	public ToolCallback changeBooking() {
		return new ToolCallback() {
			@Override
			public ToolDefinition getToolDefinition() {
				return ToolDefinition.builder()
					.name("changeBooking")
					.description("修改机票预定日期")
					.inputSchema(CHANGE_BOOKING_SCHEMA)
					.build();
			}

			@Override
			public String call(String json) {
				try {
					ChangeBookingDatesRequest request = JSON.parseObject(json, ChangeBookingDatesRequest.class);
					flightBookingService.changeBooking(request.bookingNumber(), request.name(), request.date(),
							request.from(), request.to());
					return "预订已修改成功";
				} catch (Exception e) {
					log.warn("Change booking: {}", NestedExceptionUtils.getMostSpecificCause(e).getMessage());
					return "修改预订失败: " + e.getMessage();
				}
			}
		};
	}

	@Bean("cancelBooking")
	@Description("取消机票预定")
	public ToolCallback cancelBooking() {
		return new ToolCallback() {
			@Override
			public ToolDefinition getToolDefinition() {
				return ToolDefinition.builder()
					.name("cancelBooking")
					.description("取消机票预定")
					.inputSchema(CANCEL_BOOKING_SCHEMA)
					.build();
			}

			@Override
			public String call(String json) {
				try {
					CancelBookingRequest request = JSON.parseObject(json, CancelBookingRequest.class);
					flightBookingService.cancelBooking(request.bookingNumber(), request.name());
					return "预订已取消成功";
				} catch (Exception e) {
					log.warn("Cancel booking: {}", NestedExceptionUtils.getMostSpecificCause(e).getMessage());
					return "取消预订失败: " + e.getMessage();
				}
			}
		};
	}

}
