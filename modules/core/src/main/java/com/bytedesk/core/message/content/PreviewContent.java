package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 输入预览消息内容（MESSAGE_TYPE_PREVIEW）
 *
 * 说明：
 * - 服务端通常将该对象序列化为 JSON 写入 MessageEntity.content
 * - 客户端收到 PREVIEW 消息后优先 JSON.parse(content) 解析为该结构；若解析失败则按旧版纯文本 content 展示
 * - clear=true 表示“清空预览”（例如访客输入框清空），用于让客服端及时移除预览框
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PreviewContent extends BaseContent {

	private static final long serialVersionUID = 1L;

	/**
	 * 版本号：用于后续字段演进
	 */
	@Builder.Default
	private Integer v = 1;

	/**
	 * 预览文本内容
	 */
	private String content;

	/**
	 * 是否为清空预览信号
	 */
	@Builder.Default
	private Boolean clear = false;

	/**
	 * 发送时间戳（毫秒）
	 */
	private Long ts;
}
