package com.bytedesk.cli.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bytedesk.cli.core.CliCommand;
import com.bytedesk.cli.core.CliContext;
import com.bytedesk.cli.core.CliResult;

public class HelpCommand implements CliCommand {

	@Override
	public String name() {
		return "help";
	}

	@Override
	public String description() {
		return "Show available commands";
	}

	@Override
	public List<String> aliases() {
		return List.of("-h", "--help");
	}

	@Override
	public CliResult execute(CliContext context, List<String> args) {
		if (args.isEmpty()) {
			return helpResult(context);
		}
		return commandHelpResult(context, args.getFirst());
	}

	public static CliResult helpResult(CliContext context) {
		List<Map<String, Object>> commands = context.registry().all().stream()
			.map(command -> Map.of(
				"name", command.name(),
				"description", command.description(),
				"aliases", command.aliases()))
			.toList();
		String message = buildOverview(context);
		return CliResult.ok(message, Map.of("commands", commands));
	}

	public static CliResult commandHelpResult(CliContext context, String commandName) {
		return context.registry().find(commandName)
			.map(command -> CliResult.ok(
				"Usage: " + context.cliName() + " " + command.name() + " [args]\n\n" + command.description(),
				Map.of(
					"name", command.name(),
					"description", command.description(),
					"aliases", command.aliases())))
			.orElseGet(() -> CliResult.error("Unknown command: " + commandName));
	}

	private static String buildOverview(CliContext context) {
		String commands = context.registry().all().stream()
			.map(command -> String.format("  %-12s %s", command.name(), command.description()))
			.collect(Collectors.joining("\n"));
		return context.cliName() + " " + context.version() + "\n\n"
			+ "Usage: " + context.cliName() + " [--format=json] <command> [args]\n\n"
			+ "Commands:\n" + commands;
	}

}