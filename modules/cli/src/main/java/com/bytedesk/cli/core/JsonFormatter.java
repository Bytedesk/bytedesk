package com.bytedesk.cli.core;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class JsonFormatter {

	private JsonFormatter() {
	}

	public static String toJson(Object value) {
		if (value == null) {
			return "null";
		}
		if (value instanceof String string) {
			return quote(string);
		}
		if (value instanceof Number || value instanceof Boolean) {
			return String.valueOf(value);
		}
		if (value instanceof Map<?, ?> map) {
			StringBuilder builder = new StringBuilder("{");
			Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<?, ?> entry = iterator.next();
				builder.append(quote(String.valueOf(entry.getKey()))).append(':').append(toJson(entry.getValue()));
				if (iterator.hasNext()) {
					builder.append(',');
				}
			}
			return builder.append('}').toString();
		}
		if (value instanceof List<?> list) {
			StringBuilder builder = new StringBuilder("[");
			for (int index = 0; index < list.size(); index++) {
				builder.append(toJson(list.get(index)));
				if (index < list.size() - 1) {
					builder.append(',');
				}
			}
			return builder.append(']').toString();
		}
		return quote(String.valueOf(value));
	}

	private static String quote(String value) {
		return '"' + value
			.replace("\\", "\\\\")
			.replace("\"", "\\\"")
			.replace("\n", "\\n")
			.replace("\r", "\\r")
			.replace("\t", "\\t") + '"';
	}

}