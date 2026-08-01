package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SystemContent extends BaseContent {

	private static final long serialVersionUID = 1L;

	private String type;

	private String title;

	private String content;

	private String extra;

	public static SystemContent of(MessageTypeEnum messageType, String content) {
		return SystemContent.builder()
				.type(messageType != null ? messageType.name() : null)
				.title(content)
				.content(content)
				.build();
	}

	public static SystemContent fromJson(String json) {
		if (json == null || json.isEmpty()) {
			return null;
		}
		SystemContent parsed = BaseContent.fromJson(json, SystemContent.class);
		if (parsed != null) {
			return parsed;
		}
		return SystemContent.builder().content(json).title(json).build();
	}
}