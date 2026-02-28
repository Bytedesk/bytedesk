package com.bytedesk.core.goods;

import com.bytedesk.core.message.MessageProtobuf;

public interface GoodsService {

	void persistFromMessage(
			MessageProtobuf messageProtobuf,
			String orgUid,
			String visitorUid,
			String visitorDbUid,
			String threadUid,
			String sourceMessageUid);
}
