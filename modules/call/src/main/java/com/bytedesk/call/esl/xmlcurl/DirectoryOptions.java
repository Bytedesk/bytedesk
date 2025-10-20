package com.bytedesk.call.esl.xmlcurl;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DirectoryOptions {
    private String password;
    private String callerIdName;
    private String callerIdNumber;
    private String userContext;
}
