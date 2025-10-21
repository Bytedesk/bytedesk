package com.bytedesk.call.xml_curl.xml.acl;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author danmo
 * @date 2023年09月14日 16:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AclNetworkLists {

    @JacksonXmlElementWrapper(localName = "list", useWrapping = false)
    private List<AclList> list;
}
