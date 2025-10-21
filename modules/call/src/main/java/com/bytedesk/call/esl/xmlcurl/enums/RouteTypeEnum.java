package com.bytedesk.call.esl.xmlcurl.enums;

import lombok.Getter;

@Getter
public enum RouteTypeEnum {

    AGENT(1, "坐席"),
    CALLOUT(2, "外呼"),
    SIP(3, "sip"),
    SKILL_GROUP(4, "技能组"),
    VOICE(5, "放音"),
    IVR(6, "ivr"),

    ;

    //路由类型 1-坐席 2-外呼 3-sip 4-技能组 5-放音 6-ivr
    private Integer type;

    private String desc;

    RouteTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
