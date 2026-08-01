package com.bytedesk.cli.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class CommandRegistry {

	private final Map<String, CliCommand> commands = new LinkedHashMap<>();
	private final Map<String, CliCommand> aliases = new LinkedHashMap<>();

	public void register(CliCommand command) {
		commands.put(command.name(), command);
		for (String alias : command.aliases()) {
			aliases.put(alias.toLowerCase(Locale.ROOT), command);
		}
	}

	public Optional<CliCommand> find(String name) {
		if (name == null || name.isBlank()) {
			return Optional.empty();
		}
		CliCommand command = commands.get(name);
		if (command != null) {
			return Optional.of(command);
		}
		return Optional.ofNullable(aliases.get(name.toLowerCase(Locale.ROOT)));
	}

	public Collection<CliCommand> all() {
		return commands.values();
	}

}