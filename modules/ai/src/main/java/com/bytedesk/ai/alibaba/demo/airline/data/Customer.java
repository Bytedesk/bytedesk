/*
 * @Author: jack ning github@bytedesk.com
 * @Date: 2025-02-10 14:19:13
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-22 09:29:19
 * @FilePath: /playground-flight-booking/src/main/java/ai/spring/demo/ai/playground/data/Customer.java
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
package com.bytedesk.ai.alibaba.demo.airline.data;

import java.util.ArrayList;
import java.util.List;

public class Customer {

	private String name;

	private List<Booking> bookings = new ArrayList<>();

	public Customer() {
	}

	public Customer(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Booking> getBookings() {
		return bookings;
	}

	public void setBookings(List<Booking> bookings) {
		this.bookings = bookings;
	}

}