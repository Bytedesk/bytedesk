package com.bytedesk.kbase.translation;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class KbaseTranslationRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String kbUid;

    private String sourceUid;

    private String sourceType;

    @Builder.Default
    private List<String> sourceTypes = new ArrayList<>();

    private String sourceLanguage;

    private String targetLanguage;

    private String title;

    private String summary;

    private String content;

    private String contentHtml;

    private String contentMarkdown;

    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    private String translateStatus;

    private String translateProvider;

    private ZonedDateTime translatedAt;

    private String errorMessage;

    private Boolean enabled;
}