/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-27 11:18:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-21 10:45:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.demo.booking;

import org.springframework.stereotype.Service;

import com.bytedesk.ai.alibaba.demo.booking.BookingTools.BookingDetails;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class BookingService {

	private final BookingData db;

	public BookingService() {
		db = new BookingData();

		initDemoData();
	}

	private void initDemoData() {
		List<String> names = List.of("云小宝", "李千问", "张百炼", "王通义", "刘魔搭");
		List<String> airportCodes = List.of("北京", "上海", "广州", "深圳", "杭州", "南京", "青岛", "成都", "武汉", "西安", "重庆", "大连",
				"天津");
		Random random = new Random();

		var customers = new ArrayList<Customer>();
		var bookings = new ArrayList<BookingEntity>();

		for (int i = 0; i < 5; i++) {
			String name = names.get(i);
			String from = airportCodes.get(random.nextInt(airportCodes.size()));
			String to = airportCodes.get(random.nextInt(airportCodes.size()));
			// 
			BookingClassEnum bookingClass = BookingClassEnum.values()[random.nextInt(BookingClassEnum.values().length)];
			Customer customer = new Customer();
			customer.setName(name);

			LocalDate date = LocalDate.now().plusDays(2 * (i + 1));

			BookingEntity booking = new BookingEntity();
			booking.setBookingNumber("10" + (i + 1));
			booking.setDate(date.atStartOfDay(ZoneId.systemDefault()));
			booking.setCustomer(customer);
			booking.setStatus(BookingStatusEnum.CONFIRMED.name());
			booking.setFrom(from);
			booking.setTo(to);
			booking.setType(bookingClass.name());
			customer.getBookings().add(booking);

			customers.add(customer);
			bookings.add(booking);
		}

		// Reset the database on each start
		db.setCustomers(customers);
		db.setBookings(bookings);
	}

	public List<BookingDetails> getBookings() {
		return db.getBookings().stream().map(this::toBookingDetails).toList();
	}

	private BookingEntity findBooking(String bookingNumber, String name) {
		return db.getBookings()
			.stream()
			.filter(b -> b.getBookingNumber().equalsIgnoreCase(bookingNumber))
			.filter(b -> b.getCustomer().getName().equalsIgnoreCase(name))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("Booking not found"));
	}

	public BookingDetails getBookingDetails(String bookingNumber, String name) {
		var booking = findBooking(bookingNumber, name);
		return toBookingDetails(booking);
	}

	public void changeBooking(String bookingNumber, String name, String newDate, String from, String to) {
		var booking = findBooking(bookingNumber, name);
		if (booking.getDate().isBefore(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()))) {
			throw new IllegalArgumentException("Booking cannot be changed within 24 hours of the start date.");
		}
		booking.setDate(LocalDate.parse(newDate).atStartOfDay(ZoneId.systemDefault()));
		booking.setFrom(from);
		booking.setTo(to);
	}

	public void cancelBooking(String bookingNumber, String name) {
		var booking = findBooking(bookingNumber, name);
		if (booking.getDate().isBefore(LocalDate.now().plusDays(2).atStartOfDay(ZoneId.systemDefault()))) {
			throw new IllegalArgumentException("Booking cannot be cancelled within 48 hours of the start date.");
		}
		booking.setStatus(BookingStatusEnum.CANCELLED.name());
	}
	
	private BookingDetails toBookingDetails(BookingEntity booking) {
		return new BookingDetails(booking.getBookingNumber(), booking.getCustomer().getName(), booking.getDate().toLocalDate(),
					BookingStatusEnum.valueOf(booking.getStatus()), booking.getFrom(), booking.getTo(), booking.getType());
	}

}
