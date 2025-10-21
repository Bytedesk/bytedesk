package com.bytedesk.call.xmlcurl.enums;

import lombok.Getter;

@Getter
public enum DeleteStatusEnum {
    DELETE_YES(1, "已逻辑删除"),
    DELETE_NO(0, "未删除");

    DeleteStatusEnum(int index, String name) {
        this.index = index;
        this.name = name;
    }

    private final int index;

    private final String name;

    public static boolean isEquals(String enumName, int index) {
        return DeleteStatusEnum.valueOf(enumName).index == index;
    }
}