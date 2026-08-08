package com.bytedesk.ai.providers.dashscope.tool;

public class DashScopeToolCallingResult {

	private String finalReply;

	private String log;

	public String getFinalReply() {
		return finalReply;
	}

	public void setFinalReply(String finalReply) {
		this.finalReply = finalReply;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}
}