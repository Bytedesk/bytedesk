package com.bytedesk.service.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.content.SystemContent;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.uid.UidUtils;

class ThreadMessageUtilTest {

    @Test
    void getThreadSystemMessageShouldPersistStructuredSystemContent() throws Exception {
        UidUtils uidUtils = mock(UidUtils.class);
        when(uidUtils.getUid()).thenReturn("msg-1");
        setUidUtilsInstance(uidUtils);
        ThreadEntity thread = ThreadEntity.builder()
                .uid("thread-1")
                .orgUid("org-1")
                .build();

        MessageEntity message = ThreadMessageUtil.getThreadSystemMessage("工单状态已更新", thread);

        SystemContent content = SystemContent.fromJson(message.getContent());

        assertThat(message.getType()).isEqualTo(MessageTypeEnum.SYSTEM.name());
        assertThat(content).isNotNull();
        assertThat(content.getType()).isEqualTo(MessageTypeEnum.SYSTEM.name());
        assertThat(content.getContent()).isEqualTo("工单状态已更新");
        assertThat(content.getTitle()).isEqualTo("工单状态已更新");
    }

    @Test
    void getThreadOfflineMessageShouldPersistStructuredSystemContent() throws Exception {
        UidUtils uidUtils = mock(UidUtils.class);
        when(uidUtils.getUid()).thenReturn("msg-2");
        setUidUtilsInstance(uidUtils);

        ThreadEntity thread = ThreadEntity.builder()
                .uid("thread-2")
                .orgUid("org-1")
                .build();

        MessageEntity message = ThreadMessageUtil.getThreadOfflineMessage("请留言，我们会稍后联系您", thread);

        SystemContent content = SystemContent.fromJson(message.getContent());

        assertThat(message.getType()).isEqualTo(MessageTypeEnum.LEAVE_MSG.name());
        assertThat(content).isNotNull();
        assertThat(content.getType()).isEqualTo(MessageTypeEnum.LEAVE_MSG.name());
        assertThat(content.getContent()).isEqualTo("请留言，我们会稍后联系您");
    }

    private void setUidUtilsInstance(UidUtils uidUtils) throws Exception {
        Field instanceField = UidUtils.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, uidUtils);
    }
}