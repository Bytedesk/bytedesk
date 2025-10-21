package com.bytedesk.call.xml_curl.enums;

import lombok.Getter;

@Getter
public enum FlowNodeTypeEnum {

    //0-开始 1-结束 2-放音 3-菜单 4-收号 5-转接 6-挂机 7-满意度 8-外部调用节点
    START(0, "FlowStartHandler"),
    END(1, "FlowEndHandler"),
    PLAY(2, "FlowPlaybackHandler"),
    MENU(3, "FlowMenuHandler"),
    RECEIVE(4, "FlowReceiveHandler"),
    TRANSFER(5, "FlowTransferHandler"),
    HANGUP(6, "FlowHangupHandler"),
    SATISFACTION(7, "FlowSatisfactionHandler"),
    HTTP(8, "FlowHttpHandler"),
    ;

    private final Integer type;

    private final String handler;

    FlowNodeTypeEnum(Integer type, String handler) {
        this.type = type;
        this.handler = handler;
    }

    public static String getHandler(Integer type) {
        for (FlowNodeTypeEnum value : FlowNodeTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value.getHandler();
            }
        }
        return null;
    }
}
