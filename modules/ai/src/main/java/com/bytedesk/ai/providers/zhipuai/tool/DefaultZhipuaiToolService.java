package com.bytedesk.ai.providers.zhipuai.tool;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(ZhipuaiToolService.class)
public class DefaultZhipuaiToolService implements ZhipuaiToolService {

	private static final String UNSUPPORTED_MESSAGE = "Tool calling is not supported in current edition";

	@Override
	public boolean isSupported() {
		return false;
	}

	@Override
	public String time(String message) {
		return UNSUPPORTED_MESSAGE;
	}

	@Override
	public String alarm(String message) {
		return UNSUPPORTED_MESSAGE;
	}

	@Override
	public String methodToolCallback(String message) {
		return UNSUPPORTED_MESSAGE;
	}

	@Override
	public String getWeather(String message) {
		return UNSUPPORTED_MESSAGE;
	}

	@Override
	public String getMathMemory(String message) {
		return UNSUPPORTED_MESSAGE;
	}

	@Override
	public String chatWithWebSearch(String message, String searchQuery) {
		return UNSUPPORTED_MESSAGE;
	}

	@Override
	public String chat(String message) {
		return UNSUPPORTED_MESSAGE;
	}

	@Override
	public String chat(String message, String model) {
		return UNSUPPORTED_MESSAGE;
	}
}