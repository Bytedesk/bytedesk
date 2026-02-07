package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Text message content representation.
 * 文本消息内容表示。需要扩展原先的纯字符串，增加翻译内容字段等。需要兼容原先的字符串使用方式。
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TextContent extends BaseContent {

	private static final long serialVersionUID = 1L;

	/** 原始文本 */
	private String text;

	/** 翻译后的文本 */
	private String translatedText;

	/**
	 * 从JSON字符串反序列化为TextContent对象
	 * 兼容历史版本：若解析失败则把原始字符串作为text
	 * @param json JSON字符串或纯文本
	 * @return TextContent对象，如果json为空返回null
	 */
	public static TextContent fromJson(String json) {
		if (json == null || json.isEmpty()) {
			return null;
		}
		TextContent parsed = BaseContent.fromJson(json, TextContent.class);
		if (parsed != null) {
			return parsed;
		}
		return TextContent.builder().text(json).build();
	}
}
