package com.bytedesk.core.sms_template;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsTemplateTestRequest {

    private String templateUid;

    private String providerUid;

    private String mobile;

    private String country;

    private Map<String, String> variables;
}