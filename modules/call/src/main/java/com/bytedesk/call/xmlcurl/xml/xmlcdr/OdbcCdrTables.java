package com.bytedesk.call.xmlcurl.xml.xmlcdr;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author danmo
 * @date 2023年09月15日 16:47
 */
@Data
@Accessors(chain = true)
public class OdbcCdrTables {

    @JacksonXmlElementWrapper(localName = "table ", useWrapping = false)
    private List<OdbcCdrTable> table;
}
