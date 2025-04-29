package com.bytedesk.kbase.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Builder
@Embeddable
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class RateDownSettings implements Serializable {
    
    /**
     * 点踩选项
     */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "rate_down_tag_list", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> rateDownTagList = new ArrayList<>(Arrays.asList(
        "回答不相关",
        "解释不清楚",
        "信息过时",
        "解决方案无效",
        "操作步骤错误",
        "态度不好",
        "回复太慢",
        "没有解决我的问题"
    ));
}
