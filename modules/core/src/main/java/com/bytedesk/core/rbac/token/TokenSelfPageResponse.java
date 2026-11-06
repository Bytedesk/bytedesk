package com.bytedesk.core.rbac.token;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 本人登录会话分页响应。totalElements 是服务端按有效口径过滤后的精确数量；
 * currentIncluded 表示当前认证凭证对应的记录是否包含在本页/该次查询结果中；
 * asOf 为服务端查询时间，数量仅代表该时刻的快照。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenSelfPageResponse {

    // private static final long serialVersionUID = 1L;

    private List<TokenSelfResponse> content;

    private long totalElements;

    private int totalPages;

    private int number;

    private int size;

    private Boolean currentIncluded;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime asOf;

    public String getAsOfIso() {
        return asOf == null ? null : DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(asOf);
    }
}
