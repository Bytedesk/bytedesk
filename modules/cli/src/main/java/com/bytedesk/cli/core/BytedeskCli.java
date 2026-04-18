package com.bytedesk.cli.core;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.bytedesk.cli.command.AuthCommand;
import com.bytedesk.cli.command.ConfigCommand;
import com.bytedesk.cli.command.DomainPlaceholderCommand;
import com.bytedesk.cli.command.HelpCommand;
import com.bytedesk.cli.command.OrgCommand;
import com.bytedesk.cli.command.TicketCommand;
import com.bytedesk.cli.command.VersionCommand;

public class BytedeskCli {

	private static final String APPLICATION_VERSION_PROPERTY = "application.version";
	private static final String DEFAULT_VERSION = "unknown";

	private final String cliName;
	private final String version;
	private final CliConfigStore configStore;
	private final PrintStream out;
	private final PrintStream err;
	private final CommandRegistry registry = new CommandRegistry();

	public BytedeskCli() {
		this("bytedesk", resolveVersion(BytedeskCli.class), new FileCliConfigStore(), System.out, System.err);
	}

	public BytedeskCli(String cliName, String version, CliConfigStore configStore, PrintStream out, PrintStream err) {
		this.cliName = cliName;
		this.version = version;
		this.configStore = configStore;
		this.out = out;
		this.err = err;
		registerBaseCommands();
		registerAdditionalCommands(registry);
	}

	protected void registerAdditionalCommands(CommandRegistry registry) {
	}

	public static String resolveVersion(Class<?> sourceType) {
		String configuredVersion = System.getProperty(APPLICATION_VERSION_PROPERTY);
		if (configuredVersion != null && !configuredVersion.isBlank()) {
			return configuredVersion;
		}
		Package sourcePackage = sourceType.getPackage();
		if (sourcePackage != null) {
			String implementationVersion = sourcePackage.getImplementationVersion();
			if (implementationVersion != null && !implementationVersion.isBlank()) {
				return implementationVersion;
			}
		}
		return DEFAULT_VERSION;
	}

	public int run(String[] args) {
		ParsedInput input = parse(args);
		CliContext context = new CliContext(cliName, version, input.outputMode(), out, err, configStore, registry);
		if (input.showVersionOnly()) {
			return emit(context, CliResult.ok(versionPayloadMessage(), versionPayload()));
		}
		if (input.commandName() == null) {
			return emit(context, HelpCommand.helpResult(context));
		}
		if (input.showHelpOnly()) {
			return emit(context, HelpCommand.commandHelpResult(context, input.commandName()));
		}
		return registry.find(input.commandName())
			.map(command -> emit(context, command.execute(context, input.commandArgs())))
			.orElseGet(() -> emit(context, CliResult.error(
				"Unknown command: " + input.commandName(),
				Map.of("command", input.commandName(), "availableCommands", commandNames()))));
	}

	private void registerBaseCommands() {
		registry.register(new HelpCommand());
		registry.register(new VersionCommand());
		registry.register(new ConfigCommand());
		registry.register(new AuthCommand());
		registry.register(new OrgCommand());
		registry.register(new DomainPlaceholderCommand("thread", "Conversation and queue operations", List.of("list", "show", "assign")));
		registry.register(new DomainPlaceholderCommand("message", "Message send and export operations", List.of("send", "list", "export")));
		registry.register(new DomainPlaceholderCommand("knowledge", "Knowledge base search and sync operations", List.of("search", "sync", "upload")));
		registry.register(new TicketCommand());
	}

	private int emit(CliContext context, CliResult result) {
		if (context.outputMode() == CliContext.OutputMode.JSON) {
			Map<String, Object> payload = new LinkedHashMap<>();
			payload.put("cli", cliName);
			payload.put("version", version);
			payload.put("success", result.exitCode() == 0);
			payload.put("message", result.message());
			payload.put("data", result.data());
			context.out().println(JsonFormatter.toJson(payload));
		} else {
			PrintStream stream = result.exitCode() == 0 ? context.out() : context.err();
			stream.println(result.message());
		}
		return result.exitCode();
	}

	private ParsedInput parse(String[] args) {
		CliContext.OutputMode outputMode = CliContext.OutputMode.TEXT;
		boolean showVersionOnly = false;
		boolean showHelpOnly = false;
		String commandName = null;
		List<String> commandArgs = new ArrayList<>();

		for (String arg : args) {
			if (commandName == null && arg.startsWith("--format=")) {
				String value = arg.substring("--format=".length()).toLowerCase(Locale.ROOT);
				outputMode = "json".equals(value) ? CliContext.OutputMode.JSON : CliContext.OutputMode.TEXT;
				continue;
			}
			if (commandName == null && ("--version".equals(arg) || "-v".equals(arg))) {
				showVersionOnly = true;
				continue;
			}
			if (commandName == null && ("--help".equals(arg) || "-h".equals(arg))) {
				showHelpOnly = true;
				continue;
			}
			if (commandName == null) {
				commandName = arg;
			} else {
				commandArgs.add(arg);
			}
		}
		return new ParsedInput(outputMode, showVersionOnly, showHelpOnly, commandName, List.copyOf(commandArgs));
	}

	private String versionPayloadMessage() {
		return cliName + " " + version;
	}

	private Map<String, Object> versionPayload() {
		return Map.of("cli", cliName, "version", version);
	}

	private List<String> commandNames() {
		return registry.all().stream().map(CliCommand::name).toList();
	}

	private record ParsedInput(
			CliContext.OutputMode outputMode,
			boolean showVersionOnly,
			boolean showHelpOnly,
			String commandName,
			List<String> commandArgs) {
	}

}