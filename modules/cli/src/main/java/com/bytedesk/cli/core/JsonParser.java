package com.bytedesk.cli.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonParser {

	private JsonParser() {
	}

	public static Object parse(String json) {
		return new Parser(json).parseValue();
	}

	private static final class Parser {

		private final String source;
		private int index;

		private Parser(String source) {
			this.source = source;
		}

		private Object parseValue() {
			skipWhitespace();
			if (index >= source.length()) {
				throw new IllegalArgumentException("Unexpected end of JSON input");
			}
			char current = source.charAt(index);
			return switch (current) {
				case '{' -> parseObject();
				case '[' -> parseArray();
				case '"' -> parseString();
				case 't' -> parseLiteral("true", Boolean.TRUE);
				case 'f' -> parseLiteral("false", Boolean.FALSE);
				case 'n' -> parseLiteral("null", null);
				default -> parseNumber();
			};
		}

		private Map<String, Object> parseObject() {
			expect('{');
			Map<String, Object> object = new LinkedHashMap<>();
			skipWhitespace();
			if (peek('}')) {
				index++;
				return object;
			}
			while (true) {
				String key = parseString();
				skipWhitespace();
				expect(':');
				Object value = parseValue();
				object.put(key, value);
				skipWhitespace();
				if (peek('}')) {
					index++;
					return object;
				}
				expect(',');
			}
		}

		private List<Object> parseArray() {
			expect('[');
			List<Object> array = new ArrayList<>();
			skipWhitespace();
			if (peek(']')) {
				index++;
				return array;
			}
			while (true) {
				array.add(parseValue());
				skipWhitespace();
				if (peek(']')) {
					index++;
					return array;
				}
				expect(',');
			}
		}

		private String parseString() {
			expect('"');
			StringBuilder builder = new StringBuilder();
			while (index < source.length()) {
				char current = source.charAt(index++);
				if (current == '"') {
					return builder.toString();
				}
				if (current == '\\') {
					if (index >= source.length()) {
						throw new IllegalArgumentException("Invalid escape sequence");
					}
					char escaped = source.charAt(index++);
					switch (escaped) {
						case '"' -> builder.append('"');
						case '\\' -> builder.append('\\');
						case '/' -> builder.append('/');
						case 'b' -> builder.append('\b');
						case 'f' -> builder.append('\f');
						case 'n' -> builder.append('\n');
						case 'r' -> builder.append('\r');
						case 't' -> builder.append('\t');
						case 'u' -> builder.append(parseUnicode());
						default -> throw new IllegalArgumentException("Unsupported escape sequence: \\" + escaped);
					}
				} else {
					builder.append(current);
				}
			}
			throw new IllegalArgumentException("Unterminated string literal");
		}

		private char parseUnicode() {
			if (index + 4 > source.length()) {
				throw new IllegalArgumentException("Invalid unicode escape");
			}
			String hex = source.substring(index, index + 4);
			index += 4;
			return (char) Integer.parseInt(hex, 16);
		}

		private Object parseLiteral(String literal, Object value) {
			if (!source.startsWith(literal, index)) {
				throw new IllegalArgumentException("Expected literal " + literal);
			}
			index += literal.length();
			return value;
		}

		private Number parseNumber() {
			int start = index;
			while (index < source.length()) {
				char current = source.charAt(index);
				if ((current >= '0' && current <= '9') || current == '-' || current == '+' || current == '.' || current == 'e' || current == 'E') {
					index++;
				} else {
					break;
				}
			}
			String number = source.substring(start, index);
			if (number.contains(".") || number.contains("e") || number.contains("E")) {
				return Double.parseDouble(number);
			}
			try {
				return Integer.parseInt(number);
			} catch (NumberFormatException ignored) {
				return Long.parseLong(number);
			}
		}

		private void skipWhitespace() {
			while (index < source.length() && Character.isWhitespace(source.charAt(index))) {
				index++;
			}
		}

		private void expect(char expected) {
			skipWhitespace();
			if (index >= source.length() || source.charAt(index) != expected) {
				throw new IllegalArgumentException("Expected '" + expected + "' at position " + index);
			}
			index++;
		}

		private boolean peek(char expected) {
			skipWhitespace();
			return index < source.length() && source.charAt(index) == expected;
		}
	}

}