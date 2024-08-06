package com.bytedesk.core.utils;

import java.io.IOException;

import com.bytedesk.core.socket.protobuf.model.MessageProto;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

public class MessageConvertUtils {
    private MessageConvertUtils() {
    }

    public static String toJson(Message sourceMessage) throws IOException {
        return JsonFormat.printer().print(sourceMessage);
    }

    public static MessageProto.Message toProtoBean(MessageProto.Message.Builder targetBuilder, String json)
            throws IOException {
        JsonFormat.parser().merge(json, targetBuilder);
        return targetBuilder.build();
    }

    // public static MessageProto.Message convertMessageJsonToProto(String
    // messageJson) {
    // return null;
    // }

    // public static String convertMessageProtoToJson(MessageProto.Message
    // messageProto) {
    // return "";
    // }

}
