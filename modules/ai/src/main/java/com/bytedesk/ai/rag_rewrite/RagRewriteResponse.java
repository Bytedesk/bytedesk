package com.bytedesk.ai.rag_rewrite;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class RagRewriteResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String robotUid;

    private String threadTopic;

    private String messageUid;

    private String originalQuery;

    private String rewriteType;

    private String status;

    private String rewrittenQuery;

    private String expandedQueries;

    private String effectiveQuery;

    private Integer resultCount;

    private String resultSummary;

    private String errorMessage;

    private Long latencyMs;

    private Boolean fallbackUsed;
}
