package com.bytedesk.call.config.esl.client.outbound;

import com.bytedesk.call.config.esl.client.inbound.IEslEventListener;
import com.bytedesk.call.config.esl.client.internal.Context;
import com.bytedesk.call.config.esl.client.transport.event.EslEvent;

public interface IClientHandler extends IEslEventListener {
	void onConnect(Context ctx, EslEvent event);
}
