/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-24 09:00:00
 * @Description: 消息类型转换工具类
 */
package com.bytedesk.core.message;

import java.util.HashMap;
import java.util.Map;

import com.bytedesk.core.constant.I18Consts;

public class MessageTypeConverter {

    private static final Map<String, String> typeToChineseMap = new HashMap<>();

    static {
        // 初始化消息类型与中文描述的映射，使用 I18Consts 国际化常量
        typeToChineseMap.put(MessageTypeEnum.WELCOME.name(), I18Consts.I18N_MESSAGE_TYPE_WELCOME);
        typeToChineseMap.put(MessageTypeEnum.CONTINUE.name(), I18Consts.I18N_MESSAGE_TYPE_CONTINUE);
        typeToChineseMap.put(MessageTypeEnum.SYSTEM.name(), I18Consts.I18N_MESSAGE_TYPE_SYSTEM);
        typeToChineseMap.put(MessageTypeEnum.DOCUMENT.name(), I18Consts.I18N_MESSAGE_TYPE_DOCUMENT);
        typeToChineseMap.put(MessageTypeEnum.QUEUE.name(), I18Consts.I18N_MESSAGE_TYPE_QUEUE);
        typeToChineseMap.put(MessageTypeEnum.QUEUE_NOTICE.name(), I18Consts.I18N_MESSAGE_TYPE_QUEUE_NOTICE);
        typeToChineseMap.put(MessageTypeEnum.QUEUE_UPDATE.name(), I18Consts.I18N_MESSAGE_TYPE_QUEUE_UPDATE);
        typeToChineseMap.put(MessageTypeEnum.QUEUE_ACCEPT.name(), I18Consts.I18N_MESSAGE_TYPE_QUEUE_ACCEPT);
        typeToChineseMap.put(MessageTypeEnum.QUEUE_TIMEOUT.name(), I18Consts.I18N_MESSAGE_TYPE_QUEUE_TIMEOUT);
        typeToChineseMap.put(MessageTypeEnum.QUEUE_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_QUEUE_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.NOTICE.name(), I18Consts.I18N_MESSAGE_TYPE_NOTICE);
        typeToChineseMap.put(MessageTypeEnum.TEXT.name(), I18Consts.I18N_MESSAGE_TYPE_TEXT);
        typeToChineseMap.put(MessageTypeEnum.IMAGE.name(), I18Consts.I18N_MESSAGE_TYPE_IMAGE);
        typeToChineseMap.put(MessageTypeEnum.FILE.name(), I18Consts.I18N_MESSAGE_TYPE_FILE);
        typeToChineseMap.put(MessageTypeEnum.EXTRA.name(), I18Consts.I18N_MESSAGE_TYPE_EXTRA);
        typeToChineseMap.put(MessageTypeEnum.AUDIO.name(), I18Consts.I18N_MESSAGE_TYPE_AUDIO);
        typeToChineseMap.put(MessageTypeEnum.VOICE.name(), I18Consts.I18N_MESSAGE_TYPE_VOICE);
        typeToChineseMap.put(MessageTypeEnum.VIDEO.name(), I18Consts.I18N_MESSAGE_TYPE_VIDEO);
        typeToChineseMap.put(MessageTypeEnum.MUSIC.name(), I18Consts.I18N_MESSAGE_TYPE_MUSIC);
        typeToChineseMap.put(MessageTypeEnum.LOCATION.name(), I18Consts.I18N_MESSAGE_TYPE_LOCATION);
        typeToChineseMap.put(MessageTypeEnum.LINK.name(), I18Consts.I18N_MESSAGE_TYPE_LINK);
        typeToChineseMap.put(MessageTypeEnum.GOODS.name(), I18Consts.I18N_MESSAGE_TYPE_GOODS);
        typeToChineseMap.put(MessageTypeEnum.CARD.name(), I18Consts.I18N_MESSAGE_TYPE_CARD);
        typeToChineseMap.put(MessageTypeEnum.EVENT.name(), I18Consts.I18N_MESSAGE_TYPE_EVENT);
        typeToChineseMap.put(MessageTypeEnum.GUESS.name(), I18Consts.I18N_MESSAGE_TYPE_GUESS);
        typeToChineseMap.put(MessageTypeEnum.HOT.name(), I18Consts.I18N_MESSAGE_TYPE_HOT);
        typeToChineseMap.put(MessageTypeEnum.SHORTCUT.name(), I18Consts.I18N_MESSAGE_TYPE_SHORTCUT);
        typeToChineseMap.put(MessageTypeEnum.ORDER.name(), I18Consts.I18N_MESSAGE_TYPE_ORDER);
        typeToChineseMap.put(MessageTypeEnum.POLL.name(), I18Consts.I18N_MESSAGE_TYPE_POLL);
        typeToChineseMap.put(MessageTypeEnum.POLL_SUBMIT.name(), I18Consts.I18N_MESSAGE_TYPE_POLL_SUBMIT);
        typeToChineseMap.put(MessageTypeEnum.FORM.name(), I18Consts.I18N_MESSAGE_TYPE_FORM);
        typeToChineseMap.put(MessageTypeEnum.FORM_SUBMIT.name(), I18Consts.I18N_MESSAGE_TYPE_FORM_SUBMIT);
        typeToChineseMap.put(MessageTypeEnum.CHOICE.name(), I18Consts.I18N_MESSAGE_TYPE_CHOICE);
        typeToChineseMap.put(MessageTypeEnum.CHOICE_SUBMIT.name(), I18Consts.I18N_MESSAGE_TYPE_CHOICE_SUBMIT);
        typeToChineseMap.put(MessageTypeEnum.CONFIRM.name(), I18Consts.I18N_MESSAGE_TYPE_CONFIRM);
        typeToChineseMap.put(MessageTypeEnum.CONFIRM_SUBMIT.name(), I18Consts.I18N_MESSAGE_TYPE_CONFIRM_SUBMIT);
        typeToChineseMap.put(MessageTypeEnum.LEAVE_MSG.name(), I18Consts.I18N_MESSAGE_TYPE_LEAVE_MSG);
        typeToChineseMap.put(MessageTypeEnum.LEAVE_MSG_SUBMIT.name(), I18Consts.I18N_MESSAGE_TYPE_LEAVE_MSG_SUBMIT);
        typeToChineseMap.put(MessageTypeEnum.LEAVE_MSG_REPLIED.name(), I18Consts.I18N_MESSAGE_TYPE_LEAVE_MSG_REPLIED);
        typeToChineseMap.put(MessageTypeEnum.CUSTOMER_SUBMIT.name(), I18Consts.I18N_MESSAGE_TYPE_CUSTOMER_SUBMIT);
        typeToChineseMap.put(MessageTypeEnum.SYSTEM_ALARM.name(), I18Consts.I18N_MESSAGE_TYPE_SYSTEM_ALARM);
        typeToChineseMap.put(MessageTypeEnum.TICKET.name(), I18Consts.I18N_MESSAGE_TYPE_TICKET);
        typeToChineseMap.put(MessageTypeEnum.TICKET_SUBMIT.name(), I18Consts.I18N_MESSAGE_TYPE_TICKET_SUBMIT);
        typeToChineseMap.put(MessageTypeEnum.TYPING.name(), I18Consts.I18N_MESSAGE_TYPE_TYPING);
        typeToChineseMap.put(MessageTypeEnum.PROCESSING.name(), I18Consts.I18N_MESSAGE_TYPE_PROCESSING);
        typeToChineseMap.put(MessageTypeEnum.STICKER.name(), I18Consts.I18N_MESSAGE_TYPE_STICKER);
        typeToChineseMap.put(MessageTypeEnum.EMAIL.name(), I18Consts.I18N_MESSAGE_TYPE_EMAIL);
        typeToChineseMap.put(MessageTypeEnum.BUTTON.name(), I18Consts.I18N_MESSAGE_TYPE_BUTTON);
        typeToChineseMap.put(MessageTypeEnum.BUTTON_SUBMIT.name(), I18Consts.I18N_MESSAGE_TYPE_BUTTON_SUBMIT);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_STREAM.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_STREAM);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_STREAM_START.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_STREAM_START);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_STREAM_END.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_STREAM_END);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_STREAM_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_STREAM_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_STREAM_UNANSWERED.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_STREAM_UNANSWERED);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_STREAM_ERROR.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_STREAM_ERROR);
        typeToChineseMap.put(MessageTypeEnum.PREVIEW.name(), I18Consts.I18N_MESSAGE_TYPE_PREVIEW);
        typeToChineseMap.put(MessageTypeEnum.RECALL.name(), I18Consts.I18N_MESSAGE_TYPE_RECALL);
        typeToChineseMap.put(MessageTypeEnum.DELIVERED.name(), I18Consts.I18N_MESSAGE_TYPE_DELIVERED);
        typeToChineseMap.put(MessageTypeEnum.READ.name(), I18Consts.I18N_MESSAGE_TYPE_READ);
        typeToChineseMap.put(MessageTypeEnum.QUOTATION.name(), I18Consts.I18N_MESSAGE_TYPE_QUOTATION);
        typeToChineseMap.put(MessageTypeEnum.KICKOFF.name(), I18Consts.I18N_MESSAGE_TYPE_KICKOFF);
        typeToChineseMap.put(MessageTypeEnum.SHAKE.name(), I18Consts.I18N_MESSAGE_TYPE_SHAKE);
        typeToChineseMap.put(MessageTypeEnum.FAQ.name(), I18Consts.I18N_MESSAGE_TYPE_FAQ);
        typeToChineseMap.put(MessageTypeEnum.FAQ_QUESTION.name(), I18Consts.I18N_MESSAGE_TYPE_FAQ_QUESTION);
        typeToChineseMap.put(MessageTypeEnum.FAQ_ANSWER.name(), I18Consts.I18N_MESSAGE_TYPE_FAQ_ANSWER);
        typeToChineseMap.put(MessageTypeEnum.ROBOT.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_UNANSWERED.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_UNANSWERED);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_ERROR.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_ERROR);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_UP.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_UP);
        typeToChineseMap.put(MessageTypeEnum.ROBOT_DOWN.name(), I18Consts.I18N_MESSAGE_TYPE_ROBOT_DOWN);
        typeToChineseMap.put(MessageTypeEnum.RATE.name(), I18Consts.I18N_MESSAGE_TYPE_RATE);
        typeToChineseMap.put(MessageTypeEnum.RATE_INVITE.name(), I18Consts.I18N_MESSAGE_TYPE_RATE_INVITE);
        typeToChineseMap.put(MessageTypeEnum.RATE_SUBMIT.name(), I18Consts.I18N_MESSAGE_TYPE_RATE_SUBMIT);
        typeToChineseMap.put(MessageTypeEnum.RATE_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_RATE_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.AUTO_CLOSED.name(), I18Consts.I18N_MESSAGE_TYPE_AUTO_CLOSED);
        typeToChineseMap.put(MessageTypeEnum.AGENT_CLOSED.name(), I18Consts.I18N_MESSAGE_TYPE_AGENT_CLOSED);
        typeToChineseMap.put(MessageTypeEnum.TRANSFER.name(), I18Consts.I18N_MESSAGE_TYPE_TRANSFER);
        typeToChineseMap.put(MessageTypeEnum.TRANSFER_REJECT.name(), I18Consts.I18N_MESSAGE_TYPE_TRANSFER_REJECT);
        typeToChineseMap.put(MessageTypeEnum.TRANSFER_ACCEPT.name(), I18Consts.I18N_MESSAGE_TYPE_TRANSFER_ACCEPT);
        typeToChineseMap.put(MessageTypeEnum.TRANSFER_TIMEOUT.name(), I18Consts.I18N_MESSAGE_TYPE_TRANSFER_TIMEOUT);
        typeToChineseMap.put(MessageTypeEnum.TRANSFER_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_TRANSFER_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.INVITE.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE);
        typeToChineseMap.put(MessageTypeEnum.INVITE_REJECT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_REJECT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_ACCEPT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_ACCEPT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_TIMEOUT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_TIMEOUT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.INVITE_EXIT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_EXIT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_REMOVE.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_REMOVE);
        typeToChineseMap.put(MessageTypeEnum.INVITE_VISITOR.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_VISITOR);
        typeToChineseMap.put(MessageTypeEnum.INVITE_VISITOR_REJECT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_VISITOR_REJECT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_VISITOR_ACCEPT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_VISITOR_ACCEPT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_VISITOR_TIMEOUT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_VISITOR_TIMEOUT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_VISITOR_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_VISITOR_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.INVITE_GROUP.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_GROUP);
        typeToChineseMap.put(MessageTypeEnum.INVITE_GROUP_REJECT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_GROUP_REJECT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_GROUP_ACCEPT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_GROUP_ACCEPT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_GROUP_TIMEOUT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_GROUP_TIMEOUT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_GROUP_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_GROUP_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.INVITE_KBASE.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_KBASE);
        typeToChineseMap.put(MessageTypeEnum.INVITE_KBASE_REJECT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_KBASE_REJECT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_KBASE_ACCEPT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_KBASE_ACCEPT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_KBASE_TIMEOUT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_KBASE_TIMEOUT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_KBASE_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_KBASE_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.INVITE_ORGANIZATION.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_ORGANIZATION);
        typeToChineseMap.put(MessageTypeEnum.INVITE_ORGANIZATION_REJECT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_REJECT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_ORGANIZATION_ACCEPT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_ACCEPT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_ORGANIZATION_TIMEOUT.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_TIMEOUT);
        typeToChineseMap.put(MessageTypeEnum.INVITE_ORGANIZATION_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_INVITE_ORGANIZATION_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.ARTICLE.name(), I18Consts.I18N_MESSAGE_TYPE_ARTICLE);
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_AUDIO_INVITE.name(), I18Consts.I18N_MESSAGE_TYPE_WEBRTC_AUDIO_INVITE);
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_VIDEO_INVITE.name(), I18Consts.I18N_MESSAGE_TYPE_WEBRTC_VIDEO_INVITE);
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_AUDIO_INVITE_REJECT.name(), I18Consts.I18N_MESSAGE_TYPE_WEBRTC_AUDIO_INVITE_REJECT);
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_VIDEO_INVITE_REJECT.name(), I18Consts.I18N_MESSAGE_TYPE_WEBRTC_VIDEO_INVITE_REJECT);
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_AUDIO_INVITE_ACCEPT.name(), I18Consts.I18N_MESSAGE_TYPE_WEBRTC_AUDIO_INVITE_ACCEPT);
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_VIDEO_INVITE_ACCEPT.name(), I18Consts.I18N_MESSAGE_TYPE_WEBRTC_VIDEO_INVITE_ACCEPT);
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_AUDIO_INVITE_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_WEBRTC_AUDIO_INVITE_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_VIDEO_INVITE_CANCEL.name(), I18Consts.I18N_MESSAGE_TYPE_WEBRTC_VIDEO_INVITE_CANCEL);
        typeToChineseMap.put(MessageTypeEnum.GROUP_CREATE.name(), I18Consts.I18N_MESSAGE_TYPE_GROUP_CREATE);
        typeToChineseMap.put(MessageTypeEnum.GROUP_INVITE.name(), I18Consts.I18N_MESSAGE_TYPE_GROUP_INVITE);
        typeToChineseMap.put(MessageTypeEnum.GROUP_DISMISS.name(), I18Consts.I18N_MESSAGE_TYPE_GROUP_DISMISS);
        typeToChineseMap.put(MessageTypeEnum.NOTIFICATION_AGENT_REPLY_TIMEOUT.name(), I18Consts.I18N_MESSAGE_TYPE_NOTIFICATION_AGENT_REPLY_TIMEOUT);
        typeToChineseMap.put(MessageTypeEnum.NOTIFICATION_RATE_SUBMITTED.name(), I18Consts.I18N_MESSAGE_TYPE_NOTIFICATION_RATE_SUBMITTED);
        typeToChineseMap.put(MessageTypeEnum.ERROR.name(), I18Consts.I18N_MESSAGE_TYPE_ERROR);
        // 补充的消息类型映射
        typeToChineseMap.put(MessageTypeEnum.URL.name(), I18Consts.I18N_MESSAGE_TYPE_URL);
        typeToChineseMap.put(MessageTypeEnum.PHONE_NUMBER.name(), I18Consts.I18N_MESSAGE_TYPE_PHONE_NUMBER);
        typeToChineseMap.put(MessageTypeEnum.EMAILL_ADDRESS.name(), I18Consts.I18N_MESSAGE_TYPE_EMAIL_ADDRESS);
        typeToChineseMap.put(MessageTypeEnum.WECHAT_NUMBER.name(), I18Consts.I18N_MESSAGE_TYPE_WECHAT_NUMBER);
        typeToChineseMap.put(MessageTypeEnum.BLOG.name(), I18Consts.I18N_MESSAGE_TYPE_BLOG);
    }

    /**
     * 将消息类型转换为中文描述
     *
     * @param type 消息类型字符串
     * @return 对应的中文描述，如果没有对应关系则返回原字符串
     */
    public static String convertToChineseType(String type) {
        if (type == null || type.isEmpty()) {
            return I18Consts.I18N_MESSAGE_TYPE_UNKNOWN;
        }

        return typeToChineseMap.getOrDefault(type, type);
    }
}
