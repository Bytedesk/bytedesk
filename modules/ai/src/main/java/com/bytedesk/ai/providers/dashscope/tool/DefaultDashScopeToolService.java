package com.bytedesk.ai.providers.dashscope.tool;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(DashScopeToolService.class)
public class DefaultDashScopeToolService implements DashScopeToolService {

	private static final String UNSUPPORTED_MESSAGE = "Tool calling is not supported in current edition";

	@Override
	public boolean isSupported() {
		return false;
	}

	@Override
	public DashScopeToolCallingResult callWeatherTool(String userMessage) {
		return unsupported();
	}

	@Override
	public DashScopeToolCallingResult callDateTimeTool(String userMessage) {
		return unsupported();
	}

	@Override
	public DashScopeToolCallingResult callAlarmTool(String userMessage) {
		return unsupported();
	}

	@Override
	public DashScopeToolCallingResult callMathTool(String userMessage) {
		return unsupported();
	}

	@Override
	public DashScopeToolCallingResult chat(String userMessage) {
		return unsupported();
	}

	@Override
	public DashScopeToolCallingResult chat(String userMessage, String model) {
		return unsupported();
	}

	@Override
	public DashScopeToolCallingResult callMathMemoryDemo(String userMessage) {
		return unsupported();
	}

	private DashScopeToolCallingResult unsupported() {
		DashScopeToolCallingResult result = new DashScopeToolCallingResult();
		result.setFinalReply(UNSUPPORTED_MESSAGE);
		result.setLog(UNSUPPORTED_MESSAGE);
		return result;
	}
}