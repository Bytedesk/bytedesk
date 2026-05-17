package com.bytedesk.core.message.content;

import com.bytedesk.core.base.BaseContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 询前问卷内容
 */
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PreFormContent extends BaseContent {

    private static final long serialVersionUID = 1L;

    /** 表单 uid */
    private String formUid;

    /** 表单 schema 快照 */
    private String formSchema;

    /** 表单版本（可选） */
    private Integer formVersion;

    public static PreFormContent fromJson(String json) {
        return BaseContent.fromJson(json, PreFormContent.class);
    }
    
}
