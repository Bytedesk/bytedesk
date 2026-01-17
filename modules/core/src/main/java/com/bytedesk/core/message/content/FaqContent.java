package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class FaqContent extends BaseContent {

    private static final long serialVersionUID = 1L;

    private String faqUid;

    private String faqQuestion;

    private String faqAnswer;

    public static FaqContent fromJson(String json) {
        return BaseContent.fromJson(json, FaqContent.class);
    }
    
}
