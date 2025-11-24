package com.bytedesk.kbase.utils;

import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.MessageUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.kbase.llm_faq.FaqMessageExtra;
import com.bytedesk.kbase.llm_faq.FaqResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KbaseMessageUtils {
    
    public static MessageEntity getFaqQuestionMessage(FaqResponse faqResponse, ThreadEntity threadEntity) {
        // 
        FaqMessageExtra questionExtra = FaqMessageExtra.builder()
                .faqUid(faqResponse.getUid())
                .build();
        MessageUtils.attachSequenceNumber(questionExtra, threadEntity.getUid());
        //
        String content = faqResponse.getQuestion();
        String extra = questionExtra.toJson();
        String user = threadEntity.getUser();
        //
        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(content)
                .type(MessageTypeEnum.FAQ_QUESTION.name())
                .status(MessageStatusEnum.READ.name())
                .channel(threadEntity.getChannel())
                .user(user)
                .orgUid(threadEntity.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(threadEntity)
                .extra(extra)
                .build();

        return message;
    }

    public static MessageEntity getFaqAnswerMessage(FaqResponse faqResponse, ThreadEntity threadEntity) {
        // 
        FaqMessageExtra answerExtra = FaqMessageExtra.builder()
                        .faqUid(faqResponse.getUid())
                        .images(faqResponse.getImages())
                        .attachments(faqResponse.getAttachments())
                        .answerList(faqResponse.getAnswerList())
                        .relatedFaqs(faqResponse.getRelatedFaqs())
                        .build();
        MessageUtils.attachSequenceNumber(answerExtra, threadEntity.getUid());
        // 
        String content = faqResponse.getAnswer();
        if (StringUtils.hasText(faqResponse.getAnswerHtml())) {
            content = faqResponse.getAnswerHtml();
        }
        String extra = answerExtra.toJson();
        // 插入答案消息
        String answerUser = threadEntity.getRobot();
        if (threadEntity.isAgentType()) {
            answerUser = threadEntity.getAgent();
        } else if (answerUser == null) {
            answerUser = threadEntity.getWorkgroup();
        }
        // 
        MessageEntity message = MessageEntity.builder()
                .uid(UidUtils.getInstance().getUid())
                .content(content)
                .type(MessageTypeEnum.FAQ_ANSWER.name())
                .status(MessageStatusEnum.READ.name())
                .channel(ChannelEnum.SYSTEM.name())
                .user(answerUser)
                .orgUid(threadEntity.getOrgUid())
                .createdAt(BdDateUtils.now())
                .updatedAt(BdDateUtils.now())
                .thread(threadEntity)
                .extra(extra)
                .build();

        return message;
    }

}
