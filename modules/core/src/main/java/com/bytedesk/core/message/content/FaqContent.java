package com.bytedesk.core.message.content;

import java.util.List;

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

    // Canonical FAQ fields.
    private String question;

    private String answer;

    // Optional generic display text, usually aligned with answer.
    private String content;

    private List<String> images;

    private List<String> attachments;

    private List<Object> answerList;

    private List<Object> relatedFaqs;

    public static FaqContent fromJson(String json) {
        return BaseContent.fromJson(json, FaqContent.class);
    }
    
}
