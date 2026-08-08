package com.bytedesk.ai.providers.zhipuai.tool;

public interface ZhipuaiToolService {

	boolean isSupported();

	String time(String message);

	String alarm(String message);

	String methodToolCallback(String message);

	String getWeather(String message);

	String getMathMemory(String message);

	String chatWithWebSearch(String message, String searchQuery);

	String chat(String message);

	String chat(String message, String model);
}
