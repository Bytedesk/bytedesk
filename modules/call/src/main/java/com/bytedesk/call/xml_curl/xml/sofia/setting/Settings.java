package com.bytedesk.call.xml_curl.xml.sofia.setting;

import com.bytedesk.call.xml_curl.xml.sofia.Param;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author danmo
 */
@Data
@Accessors(chain = true)
public class Settings implements Serializable {

    @JacksonXmlElementWrapper(localName = "param", useWrapping = false)
    private List<Param> param;
}
