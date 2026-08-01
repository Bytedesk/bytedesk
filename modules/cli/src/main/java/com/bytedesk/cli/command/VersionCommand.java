package com.bytedesk.cli.command;

import java.util.List;
import java.util.Map;

import com.bytedesk.cli.core.CliCommand;
import com.bytedesk.cli.core.CliContext;
import com.bytedesk.cli.core.CliResult;

public class VersionCommand implements CliCommand {

	@Override
	public String name() {
		return "version";
	}

	@Override
	public String description() {
		return "Show CLI version";
	}

	@Override
	public List<String> aliases() {
		return List.of("v");
	}

	@Override
	public CliResult execute(CliContext context, List<String> args) {
		return CliResult.ok(context.cliName() + " " + context.version(), Map.of(
			"cli", context.cliName(),
			"version", context.version()));
	}

}