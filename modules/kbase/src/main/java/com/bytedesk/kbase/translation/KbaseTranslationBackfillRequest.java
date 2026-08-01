package com.bytedesk.kbase.translation;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class KbaseTranslationBackfillRequest extends BaseRequest {

    private static final long serialVersionUID = 1L;

    private String kbUid;

    @Builder.Default
    private List<String> sourceTypes = new ArrayList<>();

    @Builder.Default
    private Boolean includeFulltext = true;

    @Builder.Default
    private Boolean includeVector = true;
}