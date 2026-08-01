package com.bytedesk.kbase.translation;

import java.time.ZonedDateTime;
import java.util.List;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class KbaseTranslationResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String kbUid;

    private String sourceUid;

    private String sourceType;

    private String sourceLanguage;

    private String targetLanguage;

    private String title;

    private String summary;

    private String content;

    private String contentHtml;

    private String contentMarkdown;

    private List<String> tagList;

    private String translateStatus;

    private String translateProvider;

    private ZonedDateTime translatedAt;

    private String errorMessage;

    private Boolean enabled;
}