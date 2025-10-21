package com.bytedesk.call.xmlcurl.xml.group;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Groups implements Serializable {
    @JacksonXmlElementWrapper(localName = "group", useWrapping = false)
    private List<Group> group;
}
