package com.bytedesk.cli.core;

import java.util.Map;
import java.util.Optional;

public interface CliConfigStore {

	Optional<String> get(String key);

	Map<String, String> list();

	void put(String key, String value);

	void remove(String key);

}