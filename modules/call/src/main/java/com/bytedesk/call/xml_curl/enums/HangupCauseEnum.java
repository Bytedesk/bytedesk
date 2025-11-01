package com.bytedesk.call.xml_curl.enums;

import lombok.Getter;

@Getter
public enum HangupCauseEnum {

    /**
     * 正常挂机
     */
    NORMAL_CLEARING(1000, "正常挂机"),

    AGENT_NO_BAND_SIP(1001, "坐席未绑定SIP号码"),
    NOT_ROUTE(1002, "未配置号码路由"),
    ROUTE_NOT_GATEWAY(1003, "路由未配置网关"),
    /**
     * 坐席全忙挂机
     */
    FULLBUSY(1004, "坐席全忙挂机"),

    /**
     * 排队超时挂机
     */
    QUEUE_TIME_OUT(1005, "排队超时挂机"),
    /**
     * 溢出挂机
     */
    OVERFLOW(1006, "溢出挂机"),

    SKILL_NO_AGENT(1007, "工作组未配置坐席"),
    ;

    private Integer code;


    private String desc;

    HangupCauseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
