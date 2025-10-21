package com.bytedesk.call.xmlcurl.enums;

import lombok.Getter;

/**
 * @author danmo
 * @date 2023年09月18日 16:34
 */
@Getter
public enum EslEventFormat {

    PLAIN("plain"),
    XML("xml"),
    JSON("json");

    public String text;

    EslEventFormat(String text) {
        this.text = text;
    }

}
