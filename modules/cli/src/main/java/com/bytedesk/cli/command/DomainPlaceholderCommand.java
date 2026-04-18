package com.bytedesk.cli.command;

import java.util.List;
import java.util.Map;

import com.bytedesk.cli.core.CliCommand;
import com.bytedesk.cli.core.CliContext;
import com.bytedesk.cli.core.CliResult;

public class DomainPlaceholderCommand implements CliCommand {

	private final String name;
	private final String description;
	private final List<String> plannedActions;

	public DomainPlaceholderCommand(String name, String description, List<String> plannedActions) {
		this.name = name;
		this.description = description;
		this.plannedActions = plannedActions;
	}

	@Override
	public String name() {
		return name;
	}

	@Override
	public String description() {
		return description;
	}

	@Override
	public CliResult execute(CliContext context, List<String> args) {
		String action = args.isEmpty() ? "(none)" : args.getFirst();
		String message = "Command group '" + name + "' is scaffolded. Planned actions: " + String.join(", ", plannedActions);
		return CliResult.ok(message, Map.of(
			"command", name,
			"requestedAction", action,
			"plannedActions", plannedActions,
			"status", "scaffolded"));
	}

}