package com.bytedesk.call.esl.xmlcurl.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum DirectionEnum {

    /**
     * 呼入
     */
    INBOUND(1, "呼入"),

    /**
     * 外呼
     */
    OUTBOUND(2, "呼出");

    private final Integer type;


    private final String desc;

    DirectionEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static DirectionEnum getByType(Integer type) {
        for (DirectionEnum directionEnum : values()) {
            if (Objects.equals(directionEnum.getType(), type)) {
                return directionEnum;
            }
        }
        return null;
    }
}
