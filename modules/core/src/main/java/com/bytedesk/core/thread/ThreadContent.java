package com.bytedesk.core.thread;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.MessageTypeConverter;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.QueueContent;
import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.core.message.content.WelcomeContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadContent {

	/**
	 * ThreadContent 版本号：用于后续字段演进
	 */
	@Builder.Default
	private Integer v = 1;

	/**
	 * 最近一条消息的类型（MessageTypeEnum.name()）
	 */
	private String msgType;

	/**
	 * 用于会话列表展示的摘要文本（建议前端优先展示该字段）
	 */
	private String preview;

	/**
	 * 原始 payload：通常为 MessageProtobuf.content（可能是纯文本，也可能是 WelcomeContent/QueueContent/RobotContent 等 JSON）
	 */
	private String payload;

	/**
	 * 兼容：用于标记历史数据来源，便于排查（LEGACY_TEXT/LEGACY_JSON 等）
	 */
	private String legacyType;

	public String toJson() {
		return JSON.toJSONString(this);
	}

	public static ThreadContent of(MessageTypeEnum type, String preview, String payload) {
		return ThreadContent.builder()
				.msgType(type != null ? type.name() : null)
				.preview(normalizePreview(preview))
				.payload(payload)
				.build();
	}

	public static ThreadContent fromStored(String stored) {
		if (stored == null) {
			return null;
		}
		String raw = stored.trim();
		if (raw.isEmpty()) {
			return ThreadContent.builder().preview("").payload("").legacyType("LEGACY_EMPTY").build();
		}

		if (raw.startsWith("{")) {
			try {
				JSONObject obj = JSON.parseObject(raw);

				// 1) 新格式：ThreadContent JSON（必须包含关键字段，避免误把 WelcomeContent 等旧 JSON 当成 ThreadContent）
				boolean isThreadContent = obj.containsKey("msgType")
						|| obj.containsKey("preview")
						|| obj.containsKey("payload")
						|| obj.containsKey("legacyType")
						|| (obj.containsKey("v") && (obj.containsKey("preview") || obj.containsKey("payload")));
				if (isThreadContent) {
					ThreadContent parsed = obj.toJavaObject(ThreadContent.class);
					if (parsed != null) {
						parsed.setPreview(normalizePreview(parsed.getPreview()));
						if (!StringUtils.hasText(parsed.getPayload())) {
							parsed.setPayload(parsed.getPreview());
						}
						return parsed;
					}
				}

				// 2) 旧格式：WelcomeContent/QueueContent/RobotContent 或其它 JSON
				String preview = extractPreviewFromLegacyJson(obj);
				return ThreadContent.builder()
						.msgType(null)
						.preview(normalizePreview(preview))
						.payload(stored)
						.legacyType("LEGACY_JSON")
						.build();
			} catch (Exception ignore) {
				// 解析失败则当作纯文本
			}
		}

		// 3) 纯字符串
		return ThreadContent.builder()
				.msgType(null)
				.preview(normalizePreview(stored))
				.payload(stored)
				.legacyType("LEGACY_TEXT")
				.build();
	}

	public static ThreadContent fromMessage(MessageTypeEnum type, MessageProtobuf message) {
		String msgType = type != null ? type.name() : null;
		String payload = message != null ? message.getContent() : null;

		String preview;
		if (MessageTypeEnum.TEXT.equals(type)) {
			preview = payload;
		} else if (type != null && (type.name().startsWith("ROBOT") || MessageTypeEnum.ROBOT.equals(type))) {
			preview = tryExtractRobotAnswer(payload);
			if (!StringUtils.hasText(preview)) {
				preview = MessageTypeConverter.convertToChineseType(msgType);
			}
		} else if (MessageTypeEnum.WELCOME.equals(type)) {
			preview = tryExtractWelcomeText(payload);
			if (!StringUtils.hasText(preview)) {
				preview = MessageTypeConverter.convertToChineseType(msgType);
			}
		} else if (MessageTypeEnum.QUEUE.equals(type)
				|| MessageTypeEnum.QUEUE_UPDATE.equals(type)
				|| MessageTypeEnum.QUEUE_ACCEPT.equals(type)
				|| MessageTypeEnum.QUEUE_NOTICE.equals(type)
				|| MessageTypeEnum.QUEUE_TIMEOUT.equals(type)
				|| MessageTypeEnum.QUEUE_CANCEL.equals(type)) {
			preview = tryExtractQueueText(payload);
			if (!StringUtils.hasText(preview)) {
				preview = MessageTypeConverter.convertToChineseType(msgType);
			}
		} else {
			preview = MessageTypeConverter.convertToChineseType(msgType);
		}

		if (preview == null) {
			preview = "";
		}
		return ThreadContent.builder()
				.msgType(msgType)
				.preview(normalizePreview(preview))
				.payload(payload)
				.legacyType(null)
				.build();
	}

	public String getDisplayText() {
		if (StringUtils.hasText(preview)) {
			return preview;
		}
		return payload;
	}

	private static String normalizePreview(String input) {
		if (input == null) {
			return "";
		}
		String s = input.replaceAll("\\s+", " ").trim();
		int max = 200;
		if (s.length() > max) {
			return s.substring(0, max);
		}
		return s;
	}

	private static String extractPreviewFromLegacyJson(JSONObject obj) {
		if (obj == null) {
			return "";
		}
		// WelcomeContent / QueueContent
		String content = obj.getString("content");
		if (StringUtils.hasText(content)) {
			return content;
		}
		// RobotContent
		String answer = obj.getString("answer");
		if (StringUtils.hasText(answer)) {
			return answer;
		}
		// 兜底：常见字段
		String text = obj.getString("text");
		if (StringUtils.hasText(text)) {
			return text;
		}
		return obj.toJSONString();
	}

	private static String tryExtractWelcomeText(String payload) {
		if (!StringUtils.hasText(payload)) {
			return null;
		}
		try {
			if (payload.trim().startsWith("{")) {
				WelcomeContent wc = WelcomeContent.fromJson(payload, WelcomeContent.class);
				if (wc != null && StringUtils.hasText(wc.getContent())) {
					return wc.getContent();
				}
			}
		} catch (Exception ignore) {
		}
		return payload;
	}

	private static String tryExtractQueueText(String payload) {
		if (!StringUtils.hasText(payload)) {
			return null;
		}
		try {
			if (payload.trim().startsWith("{")) {
				QueueContent qc = QueueContent.fromJson(payload, QueueContent.class);
				if (qc != null && StringUtils.hasText(qc.getContent())) {
					return qc.getContent();
				}
			}
		} catch (Exception ignore) {
		}
		return payload;
	}

	private static String tryExtractRobotAnswer(String payload) {
		if (!StringUtils.hasText(payload)) {
			return null;
		}
		try {
			if (payload.trim().startsWith("{")) {
				RobotContent rc = RobotContent.fromJson(payload, RobotContent.class);
				if (rc != null && StringUtils.hasText(rc.getAnswer())) {
					return rc.getAnswer();
				}
			}
		} catch (Exception ignore) {
		}
		return null;
	}

}
