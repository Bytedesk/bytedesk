package com.bytedesk.call.config.esl.client.dptools;

import com.bytedesk.call.config.esl.client.internal.IModEslApi;
import com.bytedesk.call.config.esl.client.transport.SendMsg;

public class DpTools {

	private final IModEslApi api;

	public DpTools(IModEslApi api) {
		this.api = api;
	}

	public DpTools answer() {
		api.sendMessage(new SendMsg().addCallCommand("answer"));
		return this;
	}

}
