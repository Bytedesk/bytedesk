package com.bytedesk.ai.alibaba.demo.booking;

import java.util.ArrayList;
import java.util.List;

public class BookingData {

	private List<Customer> customers = new ArrayList<>();

	private List<BookingEntity> bookings = new ArrayList<>();

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}

	public List<BookingEntity> getBookings() {
		return bookings;
	}

	public void setBookings(List<BookingEntity> bookings) {
		this.bookings = bookings;
	}

}
