package com.bytedesk.call.xmlcurl.enums;

import lombok.Getter;

@Getter
public enum GatewayTypeEnum {

    //1-分机注册 2-外部对接
    INTERNAL(1,"internal"),

    EXTERNAL(2,"external"),

    ;

    private Integer type;

    private String desc;

    GatewayTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static GatewayTypeEnum getByType(Integer type) {
        for (GatewayTypeEnum gatewayTypeEnum : GatewayTypeEnum.values()) {
            // if (ObjectUtil.equal(gatewayTypeEnum.getType(), type)) {
            //     return gatewayTypeEnum;
            // }
            return gatewayTypeEnum;
        }
        // throw new CommonException(String.format("【%s】不是有效的枚举类型", type));
        return null;
    }
}
