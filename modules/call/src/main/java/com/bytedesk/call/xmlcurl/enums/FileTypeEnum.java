package com.bytedesk.call.xmlcurl.enums;

import lombok.Getter;

@Getter
public enum FileTypeEnum {

    TEMP(0, "临时文件"),

    IMAGE(1, "图片"),

    VOICE(2, "语音"),

    FILE(3, "文件"),

    VIDEO(4, "视频"),

    OTHER(9, "其他");
    ;

    private Integer code;

    private String desc;

    FileTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
