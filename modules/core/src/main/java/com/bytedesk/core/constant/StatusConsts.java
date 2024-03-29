package com.bytedesk.core.constant;

/**
 *
 * @author xiaper.io
 */
public class StatusConsts {

    // Prevents instantiation
    private StatusConsts() {}

    /**
     * app、网站已经上线
     */
    public static final String APP_STATUS_RELEASE = "release";
    /**
     * 开发对接中...
     */
    public static final String APP_STATUS_DEBUG = "debug";
    /**
     * 已上线，开发新版本中
     */
    public static final String APP_STATUS_VERSION = "version";


    /**
     * 消息发送状态:
     *
     * 1. 发送中
     */
    public static final String MESSAGE_STATUS_SENDING = "sending";
    /**
     * 2. 已经存储到服务器
     */
    public static final String MESSAGE_STATUS_STORED = "stored";
    /**
     * 3. 对方已收到
     */
    public static final String MESSAGE_STATUS_RECEIVED = "received";
    /**
     * 4. 对方已读
     */
    public static final String MESSAGE_STATUS_READ = "read";
    /**
     * 5. 发送错误
     */
    public static final String MESSAGE_STATUS_ERROR =  "error";
    /**
     * 6. 阅后即焚已销毁
     */
    public static final String MESSAGE_STATUS_DESTROYED = "destroyed";
    /**
     * 7. 消息撤回
     */
    public static final String MESSAGE_STATUS_RECALL = "recall";
    /**
     * 8. 对方拒收
     */
    public static final String MESSAGE_STATUS_REJECT = "reject";


    /**
     * 用户在线状态：
     */
    /**
     * 跟服务器建立长连接
     */
    public static final String USER_STATUS_CONNECTED = "connected";
    /**
     * 断开长连接
     */
    public static final String USER_STATUS_DISCONNECTED = "disconnected";
    /**
     * 在线状态
     */
    public static final String USER_STATUS_ONLINE = "online";
    /**
     * 离线状态
     */
    public static final String USER_STATUS_OFFLINE = "offline";
    /**
     * 忙
     */
    public static final String USER_STATUS_BUSY = "busy";
    /**
     * 离开
     */
    public static final String USER_STATUS_AWAY = "away";
    /**
     * 登出
     */
    public static final String USER_STATUS_LOGOUT = "logout";
    /**
     * 登录
     */
    public static final String USER_STATUS_LOGIN = "login";
    /**
     * 离开
     */
    public static final String USER_STATUS_LEAVE = "leave";
    /**
     * 话后
     */
    public static final String USER_STATUS_AFTER  = "after";
    /**
     * 就餐
     */
    public static final String USER_STATUS_EAT = "eat";
    /**
     * 小休
     */
    public static final String USER_STATUS_REST = "rest";
    /**
     * 签入
     */
    public static final String USER_STATUS_SIGN_IN = "sign_in";
    /**
     * 签出
     */
    public static final String USER_STATUS_SIGN_OUT = "sign_out";


    /**
     * 排队状态: 排队中
     */
    public static final String QUEUE_STATUS_QUEUING = "queuing";
    /**
     * 已接入
     */
    public static final String QUEUE_STATUS_ACCEPTED = "accepted";
    /**
     * 已忽略
     */
    public static final String QUEUE_STATUS_IGNORED = "ignored";
    /**
     * 已离开
     */
    public static final String QUEUE_STATUS_LEAVED = "leaved";
    /**
     * 超时
     */
    public static final String QUEUE_STATUS_TIMEOUT = "timeout";


    /**
     * 留言状态: 未被领取
     */
    public static final String LEAVE_MESSAGE_STATUS_UNCLAIMED = "unclaimed";
    /**
     * 已领取
     */
    public static final String LEAVE_MESSAGE_STATUS_CLAIMED = "claimed";
    /**
     * 已小结
     */
    public static final String LEAVE_MESSAGE_STATUS_SUMMARIZED = "summarized";

    /**
     * 支付：等待支付、支付成功、支付失败
     */
    public static final String PAY_STATUS_WAITING = "waiting";
    public static final String PAY_STATUS_SUCCESS = "success";
    public static final String PAY_STATUS_FAILED = "failed";

    /**
     * iOS app status
     */
    public static final String IOS_BUILD_DEBUG = "debug";
    public static final String IOS_BUILD_RELEASE = "release";

    /**
     * 工单状态(客服角度): 待认领、待回复、处理中、待评价、已解决、已忽略、已关闭
     */
    public static final String TICKET_STATUS_UNCLAIM = "unclaim";
    // public static final String TICKET_STATUS_WAITING = "waiting";
    public static final String TICKET_STATUS_TO_BE_REPLIED = "to_be_replied";
    public static final String TICKET_STATUS_PROCESSING = "processing";
    // public static final String TICKET_STATUS_TO_BE_RATED = "to_be_rated";
    public static final String TICKET_STATUS_DONE = "done"; // 客服解决
    public static final String TICKET_STATUS_IGNORE = "ignore"; // 客服忽略
    public static final String TICKET_STATUS_CLOSED = "closed"; // 访客关闭

    /**
     * 瀑布状态：发布、审核中、下线
     */
    public static final String WATERFALL_STATUS_ON = "on";
    public static final String WATERFALL_STATUS_WAITING = "waiting";
    public static final String WATERFALL_STATUS_OFF = "off";

    /**
     * 学校状态：审核中、被拒绝、审核通过
     */
    public static final String SCHOOL_STATUS_WAITING = "waiting";
    public static final String SCHOOL_STATUS_REJECT = "reject";
    public static final String SCHOOL_STATUS_APPROVE = "approve";

    /**
     * 课程状态：在售、待售、下架
     */
    public static final String COURSE_STATUS_ON = "on";
    public static final String COURSE_STATUS_WAITING = "waiting";
    public static final String COURSE_STATUS_OFF = "off";

    /**
     * 教师状态
     */
    public static final String TEACHER_STATUS_ON = "on";
    public static final String TEACHER_STATUS_OFF = "off";

    /**
     * 预约状态: 提交待确认，已确认，已取消
     */
    public static final String APPOINT_STATUS_PENDING = "pending";
    public static final String APPOINT_STATUS_CONFIRM = "confirmed";
    public static final String APPOINT_STATUS_CANCEL = "canceled";

    /**
     * 好友关系：未添加、已发送待对方同意、好友、已删除、已拉黑
     */
    public static final String FRIEND_STATUS_NO = "no";
    public static final String FRIEND_STATUS_SEND = "send";
    public static final String FRIEND_STATUS_OK = "ok";
    public static final String FRIEND_STATUS_DELETE = "delete";
    public static final String FRIEND_STATUS_BLOCK = "block";

    /**
     * 来源：附近、通讯录、扫一扫、搜索
     */
    public static final String FRIEND_SOURCE_NEARBY = "nearby";
    public static final String FRIEND_SOURCE_ADDRESS = "address";
    public static final String FRIEND_SOURCE_SCAN = "scan";
    public static final String FRIEND_SOURCE_SEARCH = "search";

    /**
     * FAQ问答状态：已发布、未发布
     */
    public static final String ANSWER_STATUS_PUBLISHED = "published";
    public static final String ANSWER_STATUS_UNPUBLISHED = "unpublished";

    /**
     * 学校认领：待审核、审核通过、审核拒绝
     */
    public static final String CLAIM_STATUS_WAITING = "waiting";
    public static final String CLAIM_STATUS_ACCEPT = "accept";
    public static final String CLAIM_STATUS_REJECT = "reject";
    

}




