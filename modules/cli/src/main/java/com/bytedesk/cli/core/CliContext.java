package com.bytedesk.cli.core;

import java.io.PrintStream;

public record CliContext(
		String cliName,
		String version,
		OutputMode outputMode,
		PrintStream out,
		PrintStream err,
		CliConfigStore configStore,
		CommandRegistry registry) {

	public enum OutputMode {
		TEXT,
		JSON
	}

}