package com.bytedesk.core.order;

import org.springframework.stereotype.Service;

import com.bytedesk.core.message.MessageProtobuf;

@Service
public class OrderServiceImpl implements OrderService {

    @Override
    public void persistFromMessage(MessageProtobuf messageProtobuf, String orgUid, String visitorUid,
            String visitorDbUid, String threadUid, String sourceMessageUid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'persistFromMessage'");
    }


}
