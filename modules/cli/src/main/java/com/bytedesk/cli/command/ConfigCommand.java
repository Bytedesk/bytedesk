package com.bytedesk.cli.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bytedesk.cli.core.CliCommand;
import com.bytedesk.cli.core.CliContext;
import com.bytedesk.cli.core.CliResult;

public class ConfigCommand implements CliCommand {

	@Override
	public String name() {
		return "config";
	}

	@Override
	public String description() {
		return "Manage local CLI configuration";
	}

	@Override
	public CliResult execute(CliContext context, List<String> args) {
		if (args.isEmpty() || "list".equals(args.getFirst())) {
			Map<String, String> config = new LinkedHashMap<>(context.configStore().list());
			if (config.isEmpty()) {
				return CliResult.ok("No CLI config stored yet.", Map.of("config", config));
			}
			StringBuilder builder = new StringBuilder("Stored CLI config:\n");
			config.forEach((key, value) -> builder.append("  ").append(key).append('=').append(value).append('\n'));
			return CliResult.ok(builder.toString().trim(), Map.of("config", config));
		}

		String action = args.getFirst();
		if ("get".equals(action) && args.size() >= 2) {
			String key = args.get(1);
			return context.configStore().get(key)
				.map(value -> CliResult.ok(key + "=" + value, Map.of("key", key, "value", value)))
				.orElseGet(() -> CliResult.error("Config key not found: " + key));
		}

		if ("set".equals(action) && args.size() >= 3) {
			String key = args.get(1);
			String value = args.get(2);
			context.configStore().put(key, value);
			return CliResult.ok("Saved config: " + key, Map.of("key", key, "value", value));
		}

		if ("remove".equals(action) && args.size() >= 2) {
			String key = args.get(1);
			context.configStore().remove(key);
			return CliResult.ok("Removed config: " + key, Map.of("key", key));
		}

		return CliResult.error("Usage: " + context.cliName() + " config [list|get <key>|set <key> <value>|remove <key>]");
	}

}