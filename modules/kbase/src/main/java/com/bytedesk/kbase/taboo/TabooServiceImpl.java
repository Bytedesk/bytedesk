package com.bytedesk.kbase.taboo;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * 开源模块空实现：企业版未启用时返回空结果。
 */
@Service
@ConditionalOnMissingBean(TabooService.class)
public class TabooServiceImpl implements TabooService {

    @Override
    public Boolean existsByContent(String content, String orgUid) {
        return false;
    }

    @Override
    public List<String> listEnabledWordsWithSynonyms(String orgUid) {
        return List.of();
    }

    @Override
    public Optional<String> resolveReplyForContent(String orgUid, String content) {
        return Optional.empty();
    }

    @Override
    public VisitorTabooCheckResult checkVisitorTabooBeforeSse(String messageJson, String fallbackOrgUid) {
        if (messageJson == null || messageJson.isBlank()) {
            throw new IllegalArgumentException("messageJson required");
        }
        return new VisitorTabooCheckResult(false, null, null, null, null);
    }
}
