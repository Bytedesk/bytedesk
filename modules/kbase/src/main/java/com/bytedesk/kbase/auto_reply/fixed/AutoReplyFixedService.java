package com.bytedesk.kbase.auto_reply.fixed;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutoReplyFixedService {

	private final AutoReplyFixedRepository autoReplyFixedRepository;

	/**
	 * 根据知识库 UID 获取一条可用固定回复内容。
	 */
	public String getFixedReply(String kbUid) {
		if (kbUid == null || kbUid.isBlank()) {
			return null;
		}
		List<AutoReplyFixedEntity> fixedList = autoReplyFixedRepository.findByKbUidAndEnabledTrueAndDeletedFalse(kbUid);
		if (fixedList == null || fixedList.isEmpty()) {
			return null;
		}

		List<String> candidates = fixedList.stream()
				.map(AutoReplyFixedEntity::getContent)
				.filter(content -> content != null && !content.isBlank())
				.toList();
		if (candidates.isEmpty()) {
			return null;
		}

		int idx = ThreadLocalRandom.current().nextInt(candidates.size());
		return candidates.get(idx);
	}
}
