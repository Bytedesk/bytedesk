package com.bytedesk.call.esl.client.dptools;

import com.bytedesk.call.esl.client.internal.IModEslApi;
import com.bytedesk.call.esl.client.transport.SendMsg;

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
