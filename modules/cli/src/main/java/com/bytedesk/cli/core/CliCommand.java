package com.bytedesk.cli.core;

import java.util.List;

public interface CliCommand {

	String name();

	String description();

	default List<String> aliases() {
		return List.of();
	}

	CliResult execute(CliContext context, List<String> args);

}