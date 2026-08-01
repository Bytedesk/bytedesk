package com.bytedesk.service.visitor;

import java.util.List;

public class VisitorInitData {

	public static List<VisitorRequest> getDemoVisitors(String orgUid) {
		return List.of(
				buildVisitor(orgUid, "visitor_001", "https://weiyuai.cn/assets/images/avatar/02.jpg", 0, "1001", "12345679"),
				buildVisitor(orgUid, "visitor_002", "https://weiyuai.cn/assets/images/avatar/01.jpg", 1, "1002", "12345679"),
				buildVisitor(orgUid, "visitor_003", "https://weiyuai.cn/assets/images/avatar/03.jpg", 2, "1003", "12345679"));
	}

	private static VisitorRequest buildVisitor(String orgUid, String visitorUid, String avatar, Integer vipLevel,
			String sipExtension, String sipPassword) {
		return VisitorRequest.builder()
				.orgUid(orgUid)
				.visitorUid(visitorUid)
				.avatar(avatar)
				.vipLevel(vipLevel)
				.sipExtension(sipExtension)
				.sipPassword(sipPassword)
				.build();
	}
}
