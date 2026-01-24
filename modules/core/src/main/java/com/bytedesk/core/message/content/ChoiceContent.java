package com.bytedesk.core.message.content;

import java.io.Serializable;
import java.util.List;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 选择消息
 * 客服发送选项消息，让访客选择
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ChoiceContent extends BaseContent {

	private static final long serialVersionUID = 1L;

	/** 选择消息 uid（可选，用于幂等/回传） */
	private String choiceUid;

	/** 展示给用户的提示文本/问题 */
	private String content;

	/** 辅助说明（可选） */
	private String hint;

	/** 是否多选（默认 false） */
	private Boolean multiple;

	/** 最少选择数量（多选时可用） */
	private Integer minSelect;

	/** 最多选择数量（多选时可用） */
	private Integer maxSelect;

	/** 选项列表 */
	private List<ChoiceOption> options;

	/** 默认选中/已选值（单选放一个值，多选放多个值） */
	private List<String> selectedValues;

	@Getter
	@Setter
	@SuperBuilder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ChoiceOption implements Serializable {

		private static final long serialVersionUID = 1L;

		/** 选项 uid（可选） */
		private String optionUid;

		/** 选项标题（展示文本） */
		private String title;

		/** 选项值（回传/提交） */
		private String value;

		/** 选项描述（可选） */
		private String description;

		/** 选项 payload（可选，用于兼容按钮/回传场景） */
		private String payload;

		/** 是否禁用（可选） */
		private Boolean disabled;
	}

	public static ChoiceContent fromJson(String json) {
		return BaseContent.fromJson(json, ChoiceContent.class);
	}

}
