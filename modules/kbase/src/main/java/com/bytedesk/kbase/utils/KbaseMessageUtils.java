package com.bytedesk.kbase.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageExtra;
import com.bytedesk.core.message.MessageStatusEnum;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.FaqContent;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.kbase.llm_faq.FaqResponse;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KbaseMessageUtils {
    
    public static MessageEntity getFaqQuestionMessage(FaqResponse faqResponse, ThreadEntity threadEntity) {
        String content = faqResponse.getQuestion();
        String user = threadEntity.getUser();
        MessageExtra messageExtra = MessageExtra.fromOrgUid(threadEntity.getOrgUid());
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
                .extra(messageExtra.toJson())
                .build();

        return message;
    }

    public static MessageEntity getFaqAnswerMessage(FaqResponse faqResponse, ThreadEntity threadEntity) {
        String answerContent = faqResponse.getAnswer();
        if (StringUtils.hasText(faqResponse.getAnswerHtml())) {
            answerContent = faqResponse.getAnswerHtml();
        }

        List<Object> answerList = faqResponse.getAnswerList() == null
            ? null
            : faqResponse.getAnswerList().stream().map(item -> (Object) item).collect(Collectors.toList());

        List<Object> relatedFaqs = faqResponse.getRelatedFaqs() == null
            ? null
            : faqResponse.getRelatedFaqs().stream().map(item -> (Object) item).collect(Collectors.toList());

        FaqContent faqContent = FaqContent.builder()
            .faqUid(faqResponse.getUid())
            .question(faqResponse.getQuestion())
            .answer(answerContent)
            .content(answerContent)
            .images(faqResponse.getImages())
            .attachments(faqResponse.getAttachments())
            .answerList(answerList)
            .relatedFaqs(relatedFaqs)
            .build();

        String content = faqContent.toJson();
        MessageExtra messageExtra = MessageExtra.fromOrgUid(threadEntity.getOrgUid());
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
                .extra(messageExtra.toJson())
                .build();

        return message;
    }

}
