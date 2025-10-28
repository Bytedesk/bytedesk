package com.bytedesk.ai.springai.service;

import java.util.List;

import com.bytedesk.core.message.content.RobotContent;
import com.bytedesk.kbase.llm_faq.FaqProtobuf;

/**
 * 封装检索结果与来源引用列表，便于在检索、重排与SSE回复链路中统一传递。
 */
public class SearchResultWithSources {

    private final List<FaqProtobuf> searchResults;
    private final List<RobotContent.SourceReference> sourceReferences;

    public SearchResultWithSources(List<FaqProtobuf> searchResults,
            List<RobotContent.SourceReference> sourceReferences) {
        this.searchResults = searchResults;
        this.sourceReferences = sourceReferences;
    }

    public List<FaqProtobuf> getSearchResults() {
        return searchResults;
    }

    public List<RobotContent.SourceReference> getSourceReferences() {
        return sourceReferences;
    }
}
