package com.bytedesk.cli.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Jsons {

	private Jsons() {
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> object(Object value) {
		if (value instanceof Map<?, ?> map) {
			return (Map<String, Object>) map;
		}
		return Collections.emptyMap();
	}

	@SuppressWarnings("unchecked")
	public static List<Object> array(Object value) {
		if (value instanceof List<?> list) {
			return (List<Object>) list;
		}
		return List.of();
	}

	public static String string(Map<String, Object> source, String key) {
		Object value = source.get(key);
		return value == null ? null : String.valueOf(value);
	}

	public static Map<String, Object> object(Map<String, Object> source, String key) {
		return object(source.get(key));
	}

	public static List<Object> array(Map<String, Object> source, String key) {
		return array(source.get(key));
	}

	public static int integer(Map<String, Object> source, String key, int defaultValue) {
		Object value = source.get(key);
		if (value instanceof Number number) {
			return number.intValue();
		}
		if (value instanceof String string) {
			try {
				return Integer.parseInt(string);
			} catch (NumberFormatException ignored) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

}