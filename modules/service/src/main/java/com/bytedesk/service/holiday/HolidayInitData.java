package com.bytedesk.service.holiday;

import java.time.LocalDate;
import java.util.List;

public class HolidayInitData {

	private static final String SOURCE_URL = "https://github.com/NateScarlet/holiday-cn/blob/master/2026.json";
	private static final String COUNTRY_CODE = "CN";

	private HolidayInitData() {
	}

	public static List<HolidaySeed> getChinaOfficialHolidays2026() {
		return List.of(
			seed("元旦", "2026-01-01", true),
			seed("元旦", "2026-01-02", true),
			seed("元旦", "2026-01-03", true),
			seed("元旦", "2026-01-04", false),
			seed("春节", "2026-02-14", false),
			seed("春节", "2026-02-15", true),
			seed("春节", "2026-02-16", true),
			seed("春节", "2026-02-17", true),
			seed("春节", "2026-02-18", true),
			seed("春节", "2026-02-19", true),
			seed("春节", "2026-02-20", true),
			seed("春节", "2026-02-21", true),
			seed("春节", "2026-02-22", true),
			seed("春节", "2026-02-23", true),
			seed("春节", "2026-02-28", false),
			seed("清明节", "2026-04-04", true),
			seed("清明节", "2026-04-05", true),
			seed("清明节", "2026-04-06", true),
			seed("劳动节", "2026-05-01", true),
			seed("劳动节", "2026-05-02", true),
			seed("劳动节", "2026-05-03", true),
			seed("劳动节", "2026-05-04", true),
			seed("劳动节", "2026-05-05", true),
			seed("劳动节", "2026-05-09", false),
			seed("端午节", "2026-06-19", true),
			seed("端午节", "2026-06-20", true),
			seed("端午节", "2026-06-21", true),
			seed("国庆节", "2026-09-20", false),
			seed("中秋节", "2026-09-25", true),
			seed("中秋节", "2026-09-26", true),
			seed("中秋节", "2026-09-27", true),
			seed("国庆节", "2026-10-01", true),
			seed("国庆节", "2026-10-02", true),
			seed("国庆节", "2026-10-03", true),
			seed("国庆节", "2026-10-04", true),
			seed("国庆节", "2026-10-05", true),
			seed("国庆节", "2026-10-06", true),
			seed("国庆节", "2026-10-07", true),
			seed("国庆节", "2026-10-10", false)
		);
	}

	private static HolidaySeed seed(String name, String date, boolean offDay) {
		LocalDate holidayDate = LocalDate.parse(date);
		return new HolidaySeed(
			name,
			holidayDate,
			offDay,
			COUNTRY_CODE,
			name + "-" + holidayDate,
			SOURCE_URL,
			"中国法定节假日与调休日初始化数据");
	}

	public record HolidaySeed(
		String name,
		LocalDate holidayDate,
		boolean offDay,
		String countryCode,
		String holidayKey,
		String sourceUrl,
		String description) {
	}
}
