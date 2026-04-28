package com.bytedesk.service.visitor;

import java.util.List;

public class VisitorInitData {

	public static List<VisitorRequest> getDemoVisitors(String orgUid) {
		return List.of(
				buildVisitor(orgUid, "visitor_001", "https://weiyuai.cn/assets/images/avatar/02.jpg", 0),
				buildVisitor(orgUid, "visitor_002", "https://weiyuai.cn/assets/images/avatar/01.jpg", 1),
				buildVisitor(orgUid, "visitor_003", "https://weiyuai.cn/assets/images/avatar/03.jpg", 2));
	}

	private static VisitorRequest buildVisitor(String orgUid, String visitorUid, String avatar, Integer vipLevel) {
		return VisitorRequest.builder()
				.orgUid(orgUid)
				.visitorUid(visitorUid)
				.avatar(avatar)
				.vipLevel(vipLevel)
				.build();
	}
}
