package com.bytedesk.service.visitor;

import java.util.List;

public class VisitorInitData {

	/**
	 * 演示访客唯一数据源：modules/service 演示访客、enterprise/ai booking 演示数据（initBookings）、
	 * 前端 agentdemos 演示用户预设（frontend/apps/agentdemos/src/types/demo-user.ts）均以此为对齐基准。
	 * 注意：nickname 为 booking 演示按姓名匹配预订的业务键，必须跨语言固定，不能随 locale 变化。
	 */
	public static List<VisitorRequest> getDemoVisitors(String orgUid) {
		return List.of(
				buildVisitor(orgUid, "visitor_001", "小明", "https://weiyuai.cn/assets/images/avatar/02.jpg", 0, "1001", "12345679",
						"13800138001", "xiaoming@test.com"),
				buildVisitor(orgUid, "visitor_002", "小红", "https://weiyuai.cn/assets/images/avatar/01.jpg", 1, "1002", "12345679",
						"13800138002", "xiaohong@test.com"),
				buildVisitor(orgUid, "visitor_003", "小美", "https://weiyuai.cn/assets/images/avatar/03.jpg", 2, "1003", "12345679",
						"13800138003", "xiaomei@test.com"));
	}

	private static VisitorRequest buildVisitor(String orgUid, String visitorUid, String nickname, String avatar,
			Integer vipLevel, String sipExtension, String sipPassword, String mobile, String email) {
		return VisitorRequest.builder()
				.orgUid(orgUid)
				.visitorUid(visitorUid)
				.nickname(nickname)
				.avatar(avatar)
				.vipLevel(vipLevel)
				.sipExtension(sipExtension)
				.sipPassword(sipPassword)
				.mobile(mobile)
				.email(email)
				.build();
	}
}
