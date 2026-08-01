package com.bytedesk.kbase.auto_reply;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AutoReplyServiceImpl implements AutoReplyService {

    @Override
    public VisitorAutoReplyCheckResult checkVisitorAutoReplyBeforeSse(
            String messageJson,
            String fallbackOrgUid,
            String autoReplySettingsUid) {
        if (!StringUtils.hasText(messageJson)) {
            throw new IllegalArgumentException("messageJson required");
        }
        return new VisitorAutoReplyCheckResult(false, null);
    }
}
