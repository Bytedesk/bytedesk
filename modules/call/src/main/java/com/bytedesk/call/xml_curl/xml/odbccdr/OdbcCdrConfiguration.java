package com.bytedesk.call.xml_curl.xml.odbccdr;

import com.bytedesk.call.xml_curl.xml.Configuration;
import com.bytedesk.call.xml_curl.xml.sofia.setting.Settings;
import com.bytedesk.call.xml_curl.xml.xmlcdr.OdbcCdrTables;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author danmo
 * @date 2023年09月15日 16:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OdbcCdrConfiguration extends Configuration implements Serializable {

    @JacksonXmlElementWrapper(localName = "settings", useWrapping = false)
    private Settings settings;

    @JacksonXmlElementWrapper(localName = "tables", useWrapping = false)
    private OdbcCdrTables tables;
}
