package com.bytedesk.cli.core;

import java.util.Map;

public record CliResult(int exitCode, String message, Map<String, Object> data) {

	public static CliResult ok(String message) {
		return new CliResult(0, message, Map.of());
	}

	public static CliResult ok(String message, Map<String, Object> data) {
		return new CliResult(0, message, data);
	}

	public static CliResult error(String message) {
		return new CliResult(1, message, Map.of());
	}

	public static CliResult error(String message, Map<String, Object> data) {
		return new CliResult(1, message, data);
	}

}