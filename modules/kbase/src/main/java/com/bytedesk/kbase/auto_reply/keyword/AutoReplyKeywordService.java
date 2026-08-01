package com.bytedesk.kbase.auto_reply.keyword;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AutoReplyKeywordService {

	private final AutoReplyKeywordRepository autoReplyKeywordRepository;

	public String getKeywordReply(String keyword, String kbUid) {
		if (keyword == null || keyword.isBlank() || kbUid == null || kbUid.isBlank()) {
			return null;
		}

		List<AutoReplyKeywordEntity> keywordObjects = autoReplyKeywordRepository
				.findByKbUidAndEnabledTrueAndDeletedFalse(kbUid);
		if (keywordObjects == null || keywordObjects.isEmpty()) {
			return null;
		}

		List<AutoReplyKeywordEntity> matched = keywordObjects.stream()
				.filter(entity -> matchesKeywordRule(entity, keyword))
				.toList();
		if (matched.isEmpty()) {
			return null;
		}

		AutoReplyKeywordEntity keywordObject = matched.get(ThreadLocalRandom.current().nextInt(matched.size()));
		if (keywordObject.getReplyList() == null || keywordObject.getReplyList().isEmpty()) {
			return null;
		}

		List<String> candidates = keywordObject.getReplyList().stream()
				.filter(reply -> reply != null && !reply.isBlank())
				.toList();
		if (candidates.isEmpty()) {
			return null;
		}
		int randomIndex = ThreadLocalRandom.current().nextInt(candidates.size());
		return candidates.get(randomIndex);
	}

	private boolean matchesKeywordRule(AutoReplyKeywordEntity entity, String query) {
		if (entity == null || query == null || query.isBlank() || entity.getKeywordList() == null
				|| entity.getKeywordList().isEmpty()) {
			return false;
		}

		String matchType = entity.getMatchType();
		if (matchType == null || matchType.isBlank()) {
			matchType = AutoReplyKeywordMatchEnum.FUZZY.name();
		}

		for (String keyword : entity.getKeywordList()) {
			if (keyword == null || keyword.isBlank()) {
				continue;
			}
			if (matchesByType(query, keyword.trim(), matchType)) {
				return true;
			}
		}
		return false;
	}

	private boolean matchesByType(String query, String keyword, String matchType) {
		String queryText = query.trim();
		String keywordText = keyword.trim();

		try {
			AutoReplyKeywordMatchEnum type = AutoReplyKeywordMatchEnum.fromValue(matchType);
			return switch (type) {
				case EXACT -> queryText.equalsIgnoreCase(keywordText);
				case REGULAR -> Pattern.compile(keywordText, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)
						.matcher(queryText)
						.find();
				case FUZZY -> queryText.toLowerCase(Locale.ROOT)
						.contains(keywordText.toLowerCase(Locale.ROOT));
			};
		} catch (PatternSyntaxException ex) {
			log.warn("invalid keyword regex: {}", keywordText);
			return false;
		} catch (IllegalArgumentException ex) {
			return queryText.toLowerCase(Locale.ROOT).contains(keywordText.toLowerCase(Locale.ROOT));
		}
	}
}
