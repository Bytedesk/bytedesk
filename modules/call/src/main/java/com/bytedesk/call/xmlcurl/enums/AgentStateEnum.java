package com.bytedesk.call.xmlcurl.enums;

import lombok.Getter;

@Getter
public enum AgentStateEnum {

    READY(0,"空闲"),
    NOT_READY(1,"忙碌"),
    CALL_START(2,"正在呼叫"),
    CALL_OUT_RING(3,"来话振铃"),
    CALL_INT_RING(4,"被叫振铃"),
    TALKING(5,"通话中"),
    CALL_END(6,"结束通话"),
    ;

    //状态码
    private int code;

    //状态描述
    private String des;

    AgentStateEnum(int code, String des) {
        this.code = code;
        this.des = des;
    }

    public static boolean isEquals(int code, String des) {
        return AgentStateEnum.valueOf(des).code == code;
    }
}
