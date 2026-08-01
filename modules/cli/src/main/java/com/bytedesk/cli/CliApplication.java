package com.bytedesk.cli;

import com.bytedesk.cli.core.BytedeskCli;

public class CliApplication {

	public static void main(String[] args) {
		int exitCode = new BytedeskCli().run(args);
		if (exitCode != 0) {
			System.exit(exitCode);
		}
	}

}