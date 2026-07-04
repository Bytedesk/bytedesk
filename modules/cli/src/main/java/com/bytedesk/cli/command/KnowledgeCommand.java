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

public class KnowledgeCommand implements CliCommand {

	private final HttpApiClient apiClient = new HttpApiClient();

	@Override
	public String name() {
		return "knowledge";
	}

	@Override
	public String description() {
		return "Knowledge base search backed by Bytedesk HTTP APIs";
	}

	@Override
	public CliResult execute(CliContext context, List<String> args) {
		CliArgs cliArgs = new CliArgs(args);
		String action = cliArgs.commandActionOrDefault("search");
		return switch (action) {
			case "search" -> search(context, cliArgs);
			default -> CliResult.error("Usage: " + context.cliName() + " knowledge search --query <text> [--org <orgUid>] [--kb <kbUid> | --robot <robotUid>] [--topk <n>] [--search-type <FULLTEXT|VECTOR|MIXED>] [--source-type <ALL|FAQ|TEXT|CHUNK|WEBPAGE>]");
		};
	}

	private CliResult search(CliContext context, CliArgs args) {
		String query = args.option("--query").orElse(null);
		String orgUid = args.option("--org")
			.or(() -> context.configStore().get(HttpApiClient.CURRENT_ORG_UID_KEY))
			.orElse(null);
		String kbUid = args.option("--kb").orElse(null);
		String robotUid = args.option("--robot").orElse(null);
		if (query == null || query.isBlank()) {
			return CliResult.error("Usage: " + context.cliName() + " knowledge search --query <text> [--org <orgUid>] [--kb <kbUid> | --robot <robotUid>] [--topk <n>] [--search-type <FULLTEXT|VECTOR|MIXED>] [--source-type <ALL|FAQ|TEXT|CHUNK|WEBPAGE>]");
		}
		if (orgUid == null || orgUid.isBlank()) {
			return CliResult.error("Missing organization uid. Pass --org <orgUid> or run org switch first.");
		}
		if ((kbUid == null || kbUid.isBlank()) && (robotUid == null || robotUid.isBlank())) {
			return CliResult.error("Knowledge search requires --kb <kbUid> or --robot <robotUid>.");
		}

		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("query", query);
		payload.put("orgUid", orgUid);
		putIfPresent(payload, "kbUid", kbUid);
		putIfPresent(payload, "robotUid", robotUid);
		putIfPresent(payload, "searchType", args.option("--search-type").orElse(null));
		putIfPresent(payload, "sourceType", args.option("--source-type").orElse(null));
		Integer topK = parseInteger(args.option("--topk").orElse(null), "--topk");
		if (topK != null) {
			payload.put("topK", topK);
		}

		Map<String, Object> response = apiClient.post(context, "/api/v1/ai/kbase/search", payload, true);
		Map<String, Object> data = Jsons.object(response, "data");
		List<Object> items = Jsons.array(data, "items");
		String text = items.stream()
			.map(Jsons::object)
			.map(this::formatKnowledgeItem)
			.collect(Collectors.joining("\n\n"));
		String header = "Knowledge results: " + Jsons.integer(data, "total", items.size());
		return CliResult.ok(items.isEmpty() ? "No knowledge results found." : header + "\n\n" + text, Map.of("search", data));
	}

	private Integer parseInteger(String rawValue, String optionName) {
		if (rawValue == null || rawValue.isBlank()) {
			return null;
		}
		try {
			return Integer.parseInt(rawValue);
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException(optionName + " must be an integer");
		}
	}

	private void putIfPresent(Map<String, Object> payload, String key, String value) {
		if (value != null && !value.isBlank()) {
			payload.put(key, value);
		}
	}

	private String formatKnowledgeItem(Map<String, Object> item) {
		String title = Jsons.string(item, "title");
		String sourceType = Jsons.string(item, "sourceType");
		String sourceUid = Jsons.string(item, "sourceUid");
		String score = Jsons.string(item, "score");
		String summary = firstNonBlank(Jsons.string(item, "summary"), Jsons.string(item, "content"));
		String fileName = Jsons.string(item, "fileName");
		String searchChannel = Jsons.string(item, "searchChannel");
		StringBuilder builder = new StringBuilder();
		builder.append(title == null || title.isBlank() ? "(untitled knowledge item)" : title);
		if (sourceType != null && !sourceType.isBlank()) {
			builder.append("\nsourceType=").append(sourceType);
		}
		if (sourceUid != null && !sourceUid.isBlank()) {
			builder.append(" sourceUid=").append(sourceUid);
		}
		if (score != null && !score.isBlank()) {
			builder.append(" score=").append(score);
		}
		if (searchChannel != null && !searchChannel.isBlank()) {
			builder.append(" channel=").append(searchChannel);
		}
		if (fileName != null && !fileName.isBlank()) {
			builder.append(" file=").append(fileName);
		}
		if (summary != null && !summary.isBlank()) {
			builder.append("\n").append(summary);
		}
		return builder.toString();
	}

	private String firstNonBlank(String first, String second) {
		if (first != null && !first.isBlank()) {
			return first;
		}
		return second;
	}
}