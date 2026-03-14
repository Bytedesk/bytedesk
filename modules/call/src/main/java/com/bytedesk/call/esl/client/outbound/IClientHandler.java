package com.bytedesk.call.esl.client.outbound;

import com.bytedesk.call.esl.client.inbound.IEslEventListener;
import com.bytedesk.call.esl.client.internal.Context;
import com.bytedesk.call.esl.client.transport.event.EslEvent;

public interface IClientHandler extends IEslEventListener {
	void onConnect(Context ctx, EslEvent event);
}
