package com.bytedesk.cli.command;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bytedesk.cli.core.CliArgs;
import com.bytedesk.cli.core.CliCommand;
import com.bytedesk.cli.core.CliContext;
import com.bytedesk.cli.core.CliResult;
import com.bytedesk.cli.core.HttpApiClient;
import com.bytedesk.cli.core.Jsons;

public class OrgCommand implements CliCommand {

	private final HttpApiClient apiClient = new HttpApiClient();

	@Override
	public String name() {
		return "org";
	}

	@Override
	public String description() {
		return "Organization operations backed by Bytedesk user and organization APIs";
	}

	@Override
	public CliResult execute(CliContext context, List<String> args) {
		CliArgs cliArgs = new CliArgs(args);
		String action = cliArgs.commandActionOrDefault("list");
		return switch (action) {
			case "list" -> list(context);
			case "current" -> current(context);
			case "switch" -> switchOrganization(context, cliArgs);
			case "get", "show" -> get(context, cliArgs);
			default -> CliResult.error("Usage: " + context.cliName() + " org [list|current|switch --org <uid>|get --uid <uid>]");
		};
	}

	private CliResult list(CliContext context) {
		Map<String, Object> response = apiClient.get(context, "/api/v1/user/organizations", Map.of(), true);
		List<Object> organizations = Jsons.array(response, "data");
		String currentOrgUid = context.configStore().get(HttpApiClient.CURRENT_ORG_UID_KEY).orElse(null);
		String text = organizations.stream()
			.map(Jsons::object)
			.map(org -> formatOrganizationLine(org, currentOrgUid))
			.collect(Collectors.joining("\n"));
		return CliResult.ok(text.isBlank() ? "No organizations available." : text, Map.of("organizations", organizations));
	}

	private CliResult current(CliContext context) {
		Map<String, Object> response = apiClient.get(context, "/api/v1/user/profile", Map.of(), true);
		Map<String, Object> currentOrganization = Jsons.object(Jsons.object(response, "data"), "currentOrganization");
		String orgUid = Jsons.string(currentOrganization, "uid");
		String orgName = Jsons.string(currentOrganization, "name");
		if (orgUid != null) {
			context.configStore().put(HttpApiClient.CURRENT_ORG_UID_KEY, orgUid);
		}
		if (orgName != null) {
			context.configStore().put(HttpApiClient.CURRENT_ORG_NAME_KEY, orgName);
		}
		return CliResult.ok((orgName == null ? "No current organization." : orgName + " (" + orgUid + ")"), Map.of("organization", currentOrganization));
	}

	private CliResult switchOrganization(CliContext context, CliArgs args) {
		String orgUid = args.option("--org").or(() -> args.option("--uid")).orElse(null);
		if (orgUid == null) {
			return CliResult.error("Usage: " + context.cliName() + " org switch --org <uid>");
		}
		Map<String, Object> response = apiClient.post(context, "/api/v1/user/switch/organization", HttpApiClient.jsonBody("orgUid", orgUid), true);
		Map<String, Object> user = Jsons.object(response, "data");
		Map<String, Object> currentOrganization = Jsons.object(user, "currentOrganization");
		String currentUid = Jsons.string(currentOrganization, "uid");
		String currentName = Jsons.string(currentOrganization, "name");
		if (currentUid != null) {
			context.configStore().put(HttpApiClient.CURRENT_ORG_UID_KEY, currentUid);
		}
		if (currentName != null) {
			context.configStore().put(HttpApiClient.CURRENT_ORG_NAME_KEY, currentName);
		}
		return CliResult.ok("Switched current organization to " + currentName + " (" + currentUid + ")", Map.of("organization", currentOrganization, "user", user));
	}

	private CliResult get(CliContext context, CliArgs args) {
		String uid = args.option("--uid").orElse(null);
		if (uid == null) {
			return CliResult.error("Usage: " + context.cliName() + " org get --uid <uid>");
		}
		Map<String, Object> response = apiClient.get(context, "/api/v1/organization/query/uid", Map.of("uid", uid), true);
		Map<String, Object> organization = Jsons.object(response, "data");
		String text = formatOrganizationLine(organization, context.configStore().get(HttpApiClient.CURRENT_ORG_UID_KEY).orElse(null));
		return CliResult.ok(text, Map.of("organization", organization));
	}

	private String formatOrganizationLine(Map<String, Object> organization, String currentOrgUid) {
		String uid = Jsons.string(organization, "uid");
		String name = Jsons.string(organization, "name");
		String code = Jsons.string(organization, "code");
		String verifyStatus = Jsons.string(organization, "verifyStatus");
		boolean current = uid != null && uid.equals(currentOrgUid);
		StringBuilder line = new StringBuilder();
		if (current) {
			line.append("* ");
		}
		line.append(name == null ? "(unnamed)" : name);
		if (uid != null) {
			line.append(" [").append(uid).append(']');
		}
		if (code != null && !code.isBlank()) {
			line.append(" code=").append(code);
		}
		if (verifyStatus != null && !verifyStatus.isBlank()) {
			line.append(" status=").append(verifyStatus);
		}
		return line.toString();
	}

}