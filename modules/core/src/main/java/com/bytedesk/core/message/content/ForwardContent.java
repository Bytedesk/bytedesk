package com.bytedesk.core.message.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseContent;
import com.bytedesk.core.message.MessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 合并转发消息内容
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ForwardContent extends BaseContent {

	private static final long serialVersionUID = 1L;

	/**
	 * 可选：展示标题，例如“合并转发”。
	 */
	private String title;

	/**
	 * 可选：来源会话信息，便于审计/追溯。
	 */
	private String fromThreadUid;

	private String fromThreadTopic;

	@Builder.Default
	private List<ForwardItem> messages = new ArrayList<>();

	/**
	 * 合并转发条目：尽量使用精简字段，避免前端反序列化失败。
	 */
	@Getter
	@Setter
	@lombok.Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ForwardItem implements Serializable {
		private static final long serialVersionUID = 1L;

		/** 原始消息 uid */
		private String uid;

		/** 原始消息类型 */
		private MessageTypeEnum type;

		/** 原始消息内容（文本/HTML/JSON 字符串均可） */
		private String content;

		/** 原始消息创建时间戳（毫秒） */
		private Long createdAt;

		/** 发送者 uid */
		private String senderUid;

		/** 发送者昵称 */
		private String senderNickname;

		/** 发送者名称（可选） */
		private String senderName;

		/** 发送者头像 */
		private String senderAvatar;
	}
}
