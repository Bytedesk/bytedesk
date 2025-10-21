package com.bytedesk.call.xmlcurl.xml.odbccdr;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.bytedesk.call.xmlcurl.xml.Configuration;
import com.bytedesk.call.xmlcurl.xml.sofia.setting.Settings;
import com.bytedesk.call.xmlcurl.xml.xmlcdr.OdbcCdrTables;

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
