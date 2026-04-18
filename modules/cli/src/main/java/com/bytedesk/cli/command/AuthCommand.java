package com.bytedesk.cli.command;

import java.util.List;
import java.util.Map;

import com.bytedesk.cli.core.CliArgs;
import com.bytedesk.cli.core.CliCommand;
import com.bytedesk.cli.core.CliContext;
import com.bytedesk.cli.core.CliResult;
import com.bytedesk.cli.core.HttpApiClient;
import com.bytedesk.cli.core.Jsons;

public class AuthCommand implements CliCommand {

	private final HttpApiClient apiClient = new HttpApiClient();

	@Override
	public String name() {
		return "auth";
	}

	@Override
	public String description() {
		return "Persist server and token credentials for API-backed commands";
	}

	@Override
	public CliResult execute(CliContext context, List<String> args) {
		CliArgs cliArgs = new CliArgs(args);
		if (cliArgs.isEmpty()) {
			return usage(context);
		}

		String action = cliArgs.commandActionOrDefault("whoami");
		if ("login".equals(action)) {
			return login(context, cliArgs);
		}
		if ("whoami".equals(action)) {
			return whoami(context);
		}
		if ("logout".equals(action)) {
			return logout(context);
		}
		return usage(context);
	}

	private CliResult login(CliContext context, CliArgs args) {
		String server = args.option("--server").orElse(null);
		if (server == null) {
			return CliResult.error("Usage: " + context.cliName() + " auth login --server <url> [--username <name> --password <password> | --access-token <token>] [--platform <platform>] [--channel <channel>] [--captcha-uid <uid> --captcha-code <code>]");
		}
		context.configStore().put(HttpApiClient.SERVER_KEY, server);

		String platform = args.option("--platform").orElse(context.configStore().get(HttpApiClient.PLATFORM_KEY).orElse("BYTEDESK"));
		String channel = args.option("--channel").orElse(context.configStore().get(HttpApiClient.CHANNEL_KEY).orElse("WEB"));
		Map<String, Object> payload;
		Map<String, Object> response;
		if (args.option("--access-token").isPresent()) {
			payload = HttpApiClient.jsonBody(
				"accessToken", args.option("--access-token").orElse(null),
				"platform", platform,
				"channel", channel);
			response = apiClient.post(context, "/auth/v1/login/accessToken", payload, false);
		} else {
			String username = args.option("--username").orElse(null);
			String password = args.option("--password").orElse(null);
			if (username == null || password == null) {
				return CliResult.error("Usage: " + context.cliName() + " auth login --server <url> --username <name> --password <password> [--platform <platform>] [--channel <channel>] [--captcha-uid <uid> --captcha-code <code>]");
			}
			payload = HttpApiClient.jsonBody(
				"username", username,
				"password", password,
				"platform", platform,
				"channel", channel,
				"captchaUid", args.option("--captcha-uid").orElse(null),
				"captchaCode", args.option("--captcha-code").orElse(null));
			response = apiClient.post(context, "/auth/v1/login", payload, false);
		}

		Map<String, Object> authData = Jsons.object(response, "data");
		String token = Jsons.string(authData, "accessToken");
		if (token == null || token.isBlank()) {
			return CliResult.error("Login succeeded but response did not contain accessToken.");
		}
		context.configStore().put(HttpApiClient.TOKEN_KEY, token);
		context.configStore().put(HttpApiClient.PLATFORM_KEY, platform);
		context.configStore().put(HttpApiClient.CHANNEL_KEY, channel);
		Map<String, Object> user = Jsons.object(authData, "user");
		Map<String, Object> currentOrg = Jsons.object(user, "currentOrganization");
		String orgUid = Jsons.string(currentOrg, "uid");
		String orgName = Jsons.string(currentOrg, "name");
		if (orgUid != null) {
			context.configStore().put(HttpApiClient.CURRENT_ORG_UID_KEY, orgUid);
		}
		if (orgName != null) {
			context.configStore().put(HttpApiClient.CURRENT_ORG_NAME_KEY, orgName);
		}
		return CliResult.ok("Stored CLI credentials.", Map.of(
			"server", server,
			"tokenStored", true,
			"platform", platform,
			"channel", channel,
			"currentOrganization", currentOrg));
	}

	private CliResult whoami(CliContext context) {
		Map<String, Object> response = apiClient.get(context, "/api/v1/user/profile", Map.of(), true);
		Map<String, Object> user = Jsons.object(response, "data");
		Map<String, Object> currentOrg = Jsons.object(user, "currentOrganization");
		String server = context.configStore().get(HttpApiClient.SERVER_KEY).orElse("(unset)");
		String username = Jsons.string(user, "username");
		String orgName = Jsons.string(currentOrg, "name");
		String message = "User: " + username + "\nServer: " + server + "\nCurrent organization: " + (orgName == null ? "(unset)" : orgName);
		return CliResult.ok(message, Map.of("server", server, "user", user));
	}

	private CliResult logout(CliContext context) {
		apiClient.post(context, "/api/v1/user/logout", Map.of(), true);
		context.configStore().remove(HttpApiClient.TOKEN_KEY);
		context.configStore().remove(HttpApiClient.CURRENT_ORG_UID_KEY);
		context.configStore().remove(HttpApiClient.CURRENT_ORG_NAME_KEY);
		return CliResult.ok("Logged out and cleared stored auth token.", Map.of("tokenStored", false));
	}

	private CliResult usage(CliContext context) {
		return CliResult.error("Usage: " + context.cliName() + " auth [login --server <url> --username <name> --password <password>|login --server <url> --access-token <token>|whoami|logout]");
	}

}