package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * URL 消息内容类（用于 MessageTypeEnum.URL）
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class UrlContent extends BaseContent {

	private static final long serialVersionUID = 1L;

	/** URL */
	private String url;

	/** 打开方式：_blank/_self 等（可选） */
	private String target;

	/** 标题（可选） */
	private String title;

	/** 描述（可选） */
	private String description;

	/** 预览图（可选） */
	private String imageUrl;

	/** 标签/说明（可选） */
	private String label;

	public static UrlContent fromJson(String json) {
		return BaseContent.fromJson(json, UrlContent.class);
	}
}
