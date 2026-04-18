package com.bytedesk.cli.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CliArgs {

	private final List<String> values;

	public CliArgs(List<String> values) {
		this.values = List.copyOf(values);
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public String commandActionOrDefault(String defaultValue) {
		if (values.isEmpty()) {
			return defaultValue;
		}
		String first = values.getFirst();
		return first.startsWith("--") ? defaultValue : first;
	}

	public Optional<String> option(String name) {
		for (int index = 0; index < values.size(); index++) {
			String current = values.get(index);
			if (name.equals(current) && index + 1 < values.size()) {
				return Optional.ofNullable(values.get(index + 1));
			}
			if (current.startsWith(name + "=")) {
				return Optional.of(current.substring((name + "=").length()));
			}
		}
		return Optional.empty();
	}

	public boolean hasFlag(String name) {
		return values.contains(name);
	}

	public List<String> positionalAfterAction() {
		int start = 0;
		if (!values.isEmpty() && !values.getFirst().startsWith("--")) {
			start = 1;
		}
		List<String> positional = new ArrayList<>();
		for (int index = start; index < values.size(); index++) {
			String value = values.get(index);
			if (value.startsWith("--")) {
				if (!value.contains("=") && index + 1 < values.size() && !values.get(index + 1).startsWith("--")) {
					index++;
				}
				continue;
			}
			positional.add(value);
		}
		return positional;
	}

	public List<String> raw() {
		return values;
	}

}