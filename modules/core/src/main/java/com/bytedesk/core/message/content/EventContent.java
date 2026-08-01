package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

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
public class EventContent extends BaseContent {

	private static final long serialVersionUID = 1L;

	private String type;

	private String title;

	private String content;

	private String extra;

	public static EventContent fromJson(String json) {
		if (json == null || json.isEmpty()) {
			return null;
		}
		EventContent parsed = BaseContent.fromJson(json, EventContent.class);
		if (parsed != null) {
			return parsed;
		}
		return EventContent.builder().content(json).title(json).build();
	}
}