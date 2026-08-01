package com.bytedesk.cli.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class FileCliConfigStore implements CliConfigStore {

	private final Path configPath;

	public FileCliConfigStore() {
		this(Paths.get(System.getProperty("user.home"), ".bytedesk", "config.properties"));
	}

	public FileCliConfigStore(Path configPath) {
		this.configPath = configPath;
	}

	@Override
	public Optional<String> get(String key) {
		return Optional.ofNullable(load().getProperty(key));
	}

	@Override
	public Map<String, String> list() {
		Properties properties = load();
		Map<String, String> values = new LinkedHashMap<>();
		for (String name : properties.stringPropertyNames()) {
			values.put(name, properties.getProperty(name));
		}
		return values;
	}

	@Override
	public void put(String key, String value) {
		Properties properties = load();
		properties.setProperty(key, value);
		store(properties);
	}

	@Override
	public void remove(String key) {
		Properties properties = load();
		properties.remove(key);
		store(properties);
	}

	private Properties load() {
		Properties properties = new Properties();
		if (!Files.exists(configPath)) {
			return properties;
		}
		try (InputStream inputStream = Files.newInputStream(configPath)) {
			properties.load(inputStream);
			return properties;
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to read CLI config: " + configPath, exception);
		}
	}

	private void store(Properties properties) {
		try {
			Files.createDirectories(configPath.getParent());
			try (OutputStream outputStream = Files.newOutputStream(configPath)) {
				properties.store(outputStream, "Bytedesk CLI");
			}
		} catch (IOException exception) {
			throw new IllegalStateException("Failed to write CLI config: " + configPath, exception);
		}
	}

}