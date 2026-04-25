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
public class NoticeContent extends BaseContent {

	private static final long serialVersionUID = 1L;

	private String noticeUid;

	private String title;

	private String content;

	private String type;

	private String status;

	private String level;

	private String orgUid;

	private String userUid;

	private String deptUid;

	private String senderUid;

	private String senderNickname;

	private String extra;

	public static NoticeContent fromJson(String json) {
		return BaseContent.fromJson(json, NoticeContent.class);
	}
}
