/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 14:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 11:06:03
 * @Description: 消息类型转换工具类
 */
package com.bytedesk.core.utils;

import java.util.HashMap;
import java.util.Map;

import com.bytedesk.core.message.MessageTypeEnum;

public class MessageTypeConverter {
    
    private static final Map<String, String> typeToChineseMap = new HashMap<>();
    
    static {
        // 初始化消息类型与中文描述的映射
        typeToChineseMap.put(MessageTypeEnum.WELCOME.name(), "欢迎消息");
        typeToChineseMap.put(MessageTypeEnum.CONTINUE.name(), "继续消息");
        typeToChineseMap.put(MessageTypeEnum.SYSTEM.name(), "系统消息");
        typeToChineseMap.put(MessageTypeEnum.QUEUE.name(), "排队消息");
        typeToChineseMap.put(MessageTypeEnum.NOTICE.name(), "通知消息");
        typeToChineseMap.put(MessageTypeEnum.TEXT.name(), "文本消息");
        typeToChineseMap.put(MessageTypeEnum.IMAGE.name(), "图片消息");
        typeToChineseMap.put(MessageTypeEnum.FILE.name(), "文件消息");
        typeToChineseMap.put(MessageTypeEnum.AUDIO.name(), "语音消息");
        typeToChineseMap.put(MessageTypeEnum.VIDEO.name(), "视频消息");
        typeToChineseMap.put(MessageTypeEnum.MUSIC.name(), "音乐消息");
        typeToChineseMap.put(MessageTypeEnum.LOCATION.name(), "位置消息");
        typeToChineseMap.put(MessageTypeEnum.LINK.name(), "链接消息");
        typeToChineseMap.put(MessageTypeEnum.GOODS.name(), "商品消息");
        typeToChineseMap.put(MessageTypeEnum.CARD.name(), "卡片消息");
        typeToChineseMap.put(MessageTypeEnum.EVENT.name(), "事件消息");
        typeToChineseMap.put(MessageTypeEnum.GUESS.name(), "猜你想问");
        typeToChineseMap.put(MessageTypeEnum.HOT.name(), "热门话题");
        typeToChineseMap.put(MessageTypeEnum.SHORTCUT.name(), "快捷路径");
        typeToChineseMap.put(MessageTypeEnum.ORDER.name(), "订单消息");
        typeToChineseMap.put(MessageTypeEnum.POLL.name(), "投票消息");
        typeToChineseMap.put(MessageTypeEnum.POLL_SUBMIT.name(), "投票提交");
        typeToChineseMap.put(MessageTypeEnum.FORM.name(), "表单消息");
        typeToChineseMap.put(MessageTypeEnum.FORM_SUBMIT.name(), "表单提交");
        typeToChineseMap.put(MessageTypeEnum.LEAVE_MSG.name(), "留言消息");
        typeToChineseMap.put(MessageTypeEnum.LEAVE_MSG_SUBMIT.name(), "留言提交");
        typeToChineseMap.put(MessageTypeEnum.TICKET.name(), "工单消息");
        typeToChineseMap.put(MessageTypeEnum.TICKET_SUBMIT.name(), "工单提交");
        typeToChineseMap.put(MessageTypeEnum.TYPING.name(), "正在输入");
        typeToChineseMap.put(MessageTypeEnum.PROCESSING.name(), "处理中");
        typeToChineseMap.put(MessageTypeEnum.STREAM.name(), "流式消息");
        typeToChineseMap.put(MessageTypeEnum.STREAM_END.name(), "流式结束");
        typeToChineseMap.put(MessageTypeEnum.PREVIEW.name(), "消息预览");
        typeToChineseMap.put(MessageTypeEnum.RECALL.name(), "消息撤回");
        typeToChineseMap.put(MessageTypeEnum.DELIVERED.name(), "已送达");
        typeToChineseMap.put(MessageTypeEnum.READ.name(), "已读");
        typeToChineseMap.put(MessageTypeEnum.QUOTATION.name(), "引用消息");
        typeToChineseMap.put(MessageTypeEnum.KICKOFF.name(), "踢出");
        typeToChineseMap.put(MessageTypeEnum.SHAKE.name(), "窗口抖动");
        typeToChineseMap.put(MessageTypeEnum.FAQ_QUESTION.name(), "常见问题问题");
        typeToChineseMap.put(MessageTypeEnum.FAQ_ANSWER.name(), "常见问题答案");
        typeToChineseMap.put(MessageTypeEnum.FAQ_UP.name(), "常见问题点赞");
        typeToChineseMap.put(MessageTypeEnum.FAQ_DOWN.name(), "常见问题点踩");
        typeToChineseMap.put(MessageTypeEnum.ROBOT_ANSWER.name(), "机器人回答");
        typeToChineseMap.put(MessageTypeEnum.ROBOT_UP.name(), "机器人点赞");
        typeToChineseMap.put(MessageTypeEnum.ROBOT_DOWN.name(), "机器人点踩");
        typeToChineseMap.put(MessageTypeEnum.RATE.name(), "评价");
        typeToChineseMap.put(MessageTypeEnum.RATE_INVITE.name(), "邀请评价");
        typeToChineseMap.put(MessageTypeEnum.RATE_SUBMIT.name(), "评价提交");
        typeToChineseMap.put(MessageTypeEnum.RATE_CANCEL.name(), "评价取消");
        typeToChineseMap.put(MessageTypeEnum.AUTO_CLOSED.name(), "自动关闭");
        typeToChineseMap.put(MessageTypeEnum.AGENT_CLOSED.name(), "客服关闭");
        typeToChineseMap.put(MessageTypeEnum.TRANSFER.name(), "转接");
        typeToChineseMap.put(MessageTypeEnum.TRANSFER_REJECT.name(), "拒绝转接");
        typeToChineseMap.put(MessageTypeEnum.TRANSFER_ACCEPT.name(), "接受转接");
        typeToChineseMap.put(MessageTypeEnum.TRANSFER_TIMEOUT.name(), "转接超时");
        typeToChineseMap.put(MessageTypeEnum.TRANSFER_CANCEL.name(), "取消转接");
        typeToChineseMap.put(MessageTypeEnum.INVITE.name(), "邀请");
        typeToChineseMap.put(MessageTypeEnum.INVITE_REJECT.name(), "拒绝邀请");
        typeToChineseMap.put(MessageTypeEnum.INVITE_ACCEPT.name(), "接受邀请");
        typeToChineseMap.put(MessageTypeEnum.INVITE_TIMEOUT.name(), "邀请超时");
        typeToChineseMap.put(MessageTypeEnum.INVITE_CANCEL.name(), "取消邀请");
        typeToChineseMap.put(MessageTypeEnum.ARTICLE.name(), "文章");
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_AUDIO_INVITE.name(), "音频通话邀请");
        typeToChineseMap.put(MessageTypeEnum.WEBRTC_VIDEO_INVITE.name(), "视频通话邀请");
        typeToChineseMap.put(MessageTypeEnum.GROUP_CREATE.name(), "创建群组");
        typeToChineseMap.put(MessageTypeEnum.GROUP_INVITE.name(), "邀请入群");
        typeToChineseMap.put(MessageTypeEnum.GROUP_DISMISS.name(), "解散群组");
        typeToChineseMap.put(MessageTypeEnum.ERROR.name(), "错误消息");
        // 添加更多映射...
    }
    
    /**
     * 将消息类型转换为中文描述
     * 
     * @param type 消息类型字符串
     * @return 对应的中文描述，如果没有对应关系则返回原字符串
     */
    public static String convertToChineseType(String type) {
        if (type == null || type.isEmpty()) {
            return "未知类型";
        }
        
        return typeToChineseMap.getOrDefault(type, type);
    }
}
