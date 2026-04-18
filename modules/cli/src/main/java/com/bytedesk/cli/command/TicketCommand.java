package com.bytedesk.cli.command;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bytedesk.cli.core.CliArgs;
import com.bytedesk.cli.core.CliCommand;
import com.bytedesk.cli.core.CliContext;
import com.bytedesk.cli.core.CliResult;
import com.bytedesk.cli.core.HttpApiClient;
import com.bytedesk.cli.core.Jsons;

public class TicketCommand implements CliCommand {

	private final HttpApiClient apiClient = new HttpApiClient();

	@Override
	public String name() {
		return "ticket";
	}

	@Override
	public String description() {
		return "Ticket operations backed by Bytedesk ticket APIs";
	}

	@Override
	public CliResult execute(CliContext context, List<String> args) {
		CliArgs cliArgs = new CliArgs(args);
		String action = cliArgs.commandActionOrDefault("list");
		return switch (action) {
			case "list" -> list(context, cliArgs);
			case "get", "show" -> get(context, cliArgs);
			case "create" -> create(context, cliArgs);
			case "close" -> close(context, cliArgs);
			default -> CliResult.error("Usage: " + context.cliName() + " ticket [list|get --uid <uid>|create --title <title> --description <desc>|close --uid <uid>]");
		};
	}

	private CliResult list(CliContext context, CliArgs args) {
		Map<String, String> query = new LinkedHashMap<>();
		query.put("pageNumber", args.option("--page").orElse("0"));
		query.put("pageSize", args.option("--size").orElse("10"));
		query.put("status", args.option("--status").orElse(null));
		query.put("searchText", args.option("--search").orElse(null));
		Map<String, Object> response = apiClient.get(context, "/api/v1/ticket/query", query, true);
		Map<String, Object> page = Jsons.object(response, "data");
		List<Object> content = Jsons.array(page, "content");
		String text = content.stream()
			.map(Jsons::object)
			.map(this::formatTicketLine)
			.collect(Collectors.joining("\n"));
		String header = "Tickets: " + Jsons.integer(page, "totalElements", content.size());
		return CliResult.ok(content.isEmpty() ? "No tickets found." : header + "\n" + text, Map.of("page", page));
	}

	private CliResult get(CliContext context, CliArgs args) {
		String uid = args.option("--uid").orElse(null);
		if (uid == null) {
			return CliResult.error("Usage: " + context.cliName() + " ticket get --uid <uid>");
		}
		Map<String, Object> response = apiClient.get(context, "/api/v1/ticket/query/uid", Map.of("uid", uid), true);
		Map<String, Object> ticket = Jsons.object(response, "data");
		StringBuilder message = new StringBuilder();
		message.append(formatTicketLine(ticket));
		String description = Jsons.string(ticket, "description");
		if (description != null && !description.isBlank()) {
			message.append("\n").append(description);
		}
		return CliResult.ok(message.toString(), Map.of("ticket", ticket));
	}

	private CliResult create(CliContext context, CliArgs args) {
		String title = args.option("--title").orElse(null);
		String description = args.option("--description").orElse(null);
		if (title == null || description == null) {
			return CliResult.error("Usage: " + context.cliName() + " ticket create --title <title> --description <desc> [--priority <priority>] [--type <type>] [--workgroup <uid>] [--category <uid>]");
		}
		Map<String, Object> payload = HttpApiClient.jsonBody(
			"title", title,
			"description", description,
			"priority", args.option("--priority").orElse(null),
			"type", args.option("--type").orElse(null),
			"workgroupUid", args.option("--workgroup").orElse(null),
			"categoryUid", args.option("--category").orElse(null),
			"orgUid", context.configStore().get(HttpApiClient.CURRENT_ORG_UID_KEY).orElse(null));
		Map<String, Object> response = apiClient.post(context, "/api/v1/ticket/create", payload, true);
		Map<String, Object> ticket = Jsons.object(response, "data");
		return CliResult.ok("Created ticket: " + formatTicketLine(ticket), Map.of("ticket", ticket));
	}

	private CliResult close(CliContext context, CliArgs args) {
		String uid = args.option("--uid").orElse(null);
		if (uid == null) {
			return CliResult.error("Usage: " + context.cliName() + " ticket close --uid <uid>");
		}
		Map<String, Object> payload = HttpApiClient.jsonBody(
			"uid", uid,
			"status", "CLOSED",
			"reason", args.option("--reason").orElse(null));
		Map<String, Object> response = apiClient.post(context, "/api/v1/ticket/update", payload, true);
		Map<String, Object> ticket = Jsons.object(response, "data");
		return CliResult.ok("Closed ticket: " + formatTicketLine(ticket), Map.of("ticket", ticket));
	}

	private String formatTicketLine(Map<String, Object> ticket) {
		String uid = Jsons.string(ticket, "uid");
		String title = Jsons.string(ticket, "title");
		String status = Jsons.string(ticket, "status");
		String priority = Jsons.string(ticket, "priority");
		String createdAt = Jsons.string(ticket, "createdAt");
		StringBuilder builder = new StringBuilder();
		builder.append(title == null ? "(untitled ticket)" : title);
		if (uid != null) {
			builder.append(" [").append(uid).append(']');
		}
		if (status != null) {
			builder.append(" status=").append(status);
		}
		if (priority != null) {
			builder.append(" priority=").append(priority);
		}
		if (createdAt != null) {
			builder.append(" createdAt=").append(createdAt);
		}
		return builder.toString();
	}

}