package com.bytedesk.kbase.translation;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;
import com.bytedesk.kbase.kbase.KbaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_kbase_translation")
public class KbaseTranslationEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String sourceUid;

    @Builder.Default
    private String sourceType = KbaseTranslationSourceTypeEnum.FAQ.name();

    private String sourceLanguage;

    private String targetLanguage;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String title;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String summary;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentHtml;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String contentMarkdown;

    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> tagList = new ArrayList<>();

    @Builder.Default
    private String translateStatus = KbaseTranslationStatusEnum.NEW.name();

    private String translateProvider;

    private ZonedDateTime translatedAt;

    @Column(columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String errorMessage;

    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private KbaseEntity kbase;
}