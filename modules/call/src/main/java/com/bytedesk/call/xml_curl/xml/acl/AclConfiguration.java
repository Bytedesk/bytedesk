package com.bytedesk.call.xml_curl.xml.acl;

import com.bytedesk.call.xml_curl.xml.Configuration;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author danmo
 * @date 2023年09月14日 16:54
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AclConfiguration extends Configuration implements Serializable {

    @JacksonXmlElementWrapper(localName = "network-lists", useWrapping = false)
    private AclNetworkLists networkLists;
}
