package com.bytedesk.core.order;

import com.bytedesk.core.message.MessageProtobuf;

public interface OrderService {

	void persistFromMessage(
			MessageProtobuf messageProtobuf,
			String orgUid,
			String visitorUid,
			String visitorDbUid,
			String threadUid,
			String sourceMessageUid);
}
