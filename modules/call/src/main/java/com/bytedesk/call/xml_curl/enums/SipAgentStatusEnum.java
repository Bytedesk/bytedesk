package com.bytedesk.call.xml_curl.enums;

import lombok.Getter;

/**
 * @author danmo
 */

@Getter
public enum SipAgentStatusEnum {

    //状态 1-空闲 2-忙碌 3-勿扰 4-离线 5-通话中 6-振铃中 7-话后
    READY(1,"空闲"),
    NOT_READY(2,"忙碌"),
    NOT_READY_NOT_TALKING(3,"勿扰"),
    OFF_ON(4,"离线"),
    TALKING_IN(5,"通话中"),
    RINGING(6,"振铃中"),
    TALKING_OUT(7,"话后");

    ;

    //状态码
    private int code;

    //状态描述
    private String des;

    SipAgentStatusEnum(int code, String des) {
        this.code = code;
        this.des = des;
    }

    public static boolean isEquals(int code, String des) {
        return SipAgentStatusEnum.valueOf(des).code == code;
    }
}
