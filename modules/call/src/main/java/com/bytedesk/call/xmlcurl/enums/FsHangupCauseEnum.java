package com.bytedesk.call.xmlcurl.enums;


import lombok.Getter;

/**
 * Freeswitch 呼叫结束原因枚举
 * @author danmo
 * @date 2025/04/14 10:15
 */
@Getter
public enum FsHangupCauseEnum {

    /**
     * 这通常由路由器在未使用其他代码时给出。此原因通常与原因 1、原因 88 和原因 100 发生在同一类型的情况下
     */
    UNSPECIFIED(0, -1,"未指定原因","UNSPECIFIED"),

    /**
     * 此原因表示无法到达被叫方，因为尽管被叫方号码的格式有效，但当前未分配（分配）
     */
    UNALLOCATED_NUMBER(1, 404,"未分配号码","UNALLOCATED_NUMBER"),
    /**
     * 此原因表示发送此原因的设备已收到通过特定中转网络路由呼叫的请求，但无法识别该请求。发送此原因的设备无法识别中转网络，要么是因为中转网络不存在，要么是因为该特定中转网络虽然存在，但不为发送此原因的设备提供服务
     */
    NO_ROUTE_TRANSIT_NET(2, 404,"无路由通达网络","NO_ROUTE_TRANSIT_NET"),

    /**
     * 此原因表示无法联系到被叫方，因为路由呼叫的网络无法为所需的目的地提供服务。此原因的支持取决于网络
     */
    NO_ROUTE_DESTINATION(3, 404,"无通往目的地的路线","NO_ROUTE_DESTINATION"),

    /**
     * 此原因表示发送实体不接受最近识别的通道用于此调用
     */
    CHANNEL_UNACCEPTABLE(6, 404,"通道不可接受","CHANNEL_UNACCEPTABLE"),

    /**
     * 此原因表示用户已获得传入呼叫，并且传入呼叫正在连接到已为该用户建立的通道以进行类似呼叫（例如数据包模式 x.25 虚拟呼叫）
     */
    CALL_AWARDED_DELIVERED(7, 404,"呼叫受赠，在已建立的频道中传送","CALL_AWARDED_DELIVERED"),

    /**
     * 此原因表示呼叫正常结束
     */
    NORMAL_CLEARING(16, 404,"正常挂机","NORMAL_CLEARING"),

    /**
     * 此原因用于表示被叫方由于遇到用户忙情况而无法接受另一个呼叫。此 cause 值可能由被叫用户或网络生成。在用户确定用户忙的情况下，会注意到用户设备与呼叫兼容
     */
    USER_BUSY(17, 486,"用户忙","USER_BUSY"),

    /**
     * 当被叫方在分配的指定时间内未响应带有警报或连接指示的呼叫建立消息时，会使用此原因
     */
    NO_USER_RESPONSE(18, 408,"无用户响应","NO_USER_RESPONSE"),

    /**
     * 当被叫方已收到警报，但在规定的时间内未响应连接指示时，会使用此原因
     */
    NO_ANSWER(19, 480,"用户无应答","NO_ANSWER"),

    /**
     * 当移动站已注销、未与移动站取得无线电联系或个人电信用户在任何用户网络接口上暂时无法寻址时，将使用此原因值
     */
    SUBSCRIBER_ABSENT(20,480,"用户不在线","SUBSCRIBER_ABSENT"),

    /**
     * 此原因表示发送此原因的设备不希望接受此呼叫，尽管它可能已经接受了该呼叫，因为发送此原因的设备既不忙也不不兼容
     */
    CALL_REJECTED(21, 603,"呼叫被拒绝","CALL_REJECTED"),

    /**
     * 当主叫方指示的被叫方号码不再分配时，此原因将返回给主叫方，新的被叫方号码可以选择包含在诊断字段中
     */
    NUMBER_CHANGED(22, 410,"号码已更改","NUMBER_CHANGED"),

    /**
     * 此原因由通用 ISUP 协议机制使用，该机制可由决定应将呼叫设置为不同被叫号码的交换机调用。此类交换可以通过使用此 cause 值来调用重定向机制，以请求呼叫中涉及的先前交换将呼叫路由到新号码
     */
    REDIRECTION_TO_NEW_DESTINATION(23, 410,"重定向到新目的地","REDIRECTION_TO_NEW_DESTINATION"),

    /**
     * 此原因表示无法到达用户指示的目标，因为中间交换由于在执行跃点计数器过程中达到限制而释放了呼叫
     */
    EXCHANGE_ROUTING_ERROR(25, 483,"交换路由错误","EXCHANGE_ROUTING_ERROR"),

    /**
     * 此原因表示由于目标的接口无法正常工作，因此无法访问用户指示的目标。术语“未正常运行”表示无法将信号消息传送到远程方;例如，远程方的物理层或数据链路层故障，或用户设备离线
     */
    DESTINATION_OUT_OF_ORDER(27, 502,"无法访问用户指示的目标","DESTINATION_OUT_OF_ORDER"),

    /**
     * 此原因表示由于被叫号码格式无效或不完整，无法联系到被叫
     */
    INVALID_NUMBER_FORMAT(28, 484,"无效号码格式","INVALID_NUMBER_FORMAT"),

    /**
     * 当用户请求的补充服务无法由网络提供时，将返回此原因
     */
    FACILITY_REJECTED(29, 501,"拒绝服务","FACILITY_REJECTED"),

    /**
     * 当生成 STATUS 消息的原因是之前收到 STATUS INQUIRY 时，此原因将包含在 STATUS 消息中
     */
    RESPONSE_TO_STATUS_ENQUIRY(30, -1,"响应状态查询","RESPONSE_TO_STATUS_ENQUIRY"),

    /**
     * 仅当 normal 类中没有其他原因适用时，此原因才用于报告 normal 事件
     */
    NORMAL_UNSPECIFIED(31, 480,"正常挂机","NORMAL_UNSPECIFIED"),

    /**
     * 此原因表示当前没有合适的电路/通道可用于处理呼叫
     */
    NORMAL_CIRCUIT_CONGESTION(34, 503,"电路/通道拥塞","NORMAL_CIRCUIT_CONGESTION"),

    /**
     * 此原因表明网络未正常运行，并且这种情况可能会持续相对较长的时间，例如，立即重新尝试呼叫不太可能成功
     */
    NETWORK_OUT_OF_ORDER(38, 502,"网络未正常运行","NETWORK_OUT_OF_ORDER"),

    /**
     * 此原因表明网络运行不正常，并且这种情况不太可能持续很长时间;例如，用户可能希望几乎立即尝试另一次调用尝试
     */
    NORMAL_TEMPORARY_FAILURE(41, 503,"临时故障","NORMAL_TEMPORARY_FAILURE"),

    /**
     * 此原因表示产生此原因的交换设备正在经历一段高流量期
     */
    SWITCH_CONGESTION(42, 503,"网络拥塞","SWITCH_CONGESTION"),

    /**
     * 该原因表明网络无法按照要求向远程用户提供访问信息，即用户到用户的信息、低层兼容性、高层兼容性或诊断中指示的子地址。需要注意的是，丢弃的特定类型的访问信息可以选择包含在诊断中
     */
    ACCESS_INFO_DISCARDED(43, 503,"访问信息被丢弃","ACCESS_INFO_DISCARDED"),

    /**
     * 当接口的另一端无法提供请求实体指示的电路或通道时，将返回此原因
     */
    REQUESTED_CHAN_UNAVAIL(44, 503,"请求的通道不可用","REQUESTED_CHAN_UNAVAIL"),

    /**
     * 此原因表示用户已请求补充服务，该服务可用，但用户无权使用
     */
    FACILITY_NOT_SUBSCRIBED(50, -1,"服务未订阅","FACILITY_NOT_SUBSCRIBED"),

    /**
     * 此原因表示，虽然主叫方是传出 CUG 呼叫的 CUG 成员，但不允许此 CUG 成员传出呼叫
     */
    OUTGOING_CALL_BARRED(52, 403,"呼叫被拒绝","OUTGOING_CALL_BARRED"),

    /**
     * 此原因表示，虽然被叫方是传入 CUG 呼叫的 CUG 成员，但不允许传入呼叫到该 CUG 成员
     */
    INCOMING_CALL_BARRED(54, 403,"呼叫被拒绝","INCOMING_CALL_BARRED"),

    /**
     * 此原因表示用户已请求由生成此原因的设备实现的承载功能，但用户无权使用
     */
    BEARERCAPABILITY_NOTAUTH(57, 403,"不支持的媒体类型","BEARERCAPABILITY_NOTAUTH"),

    /**
     * 此原因表示用户已请求承载功能，该功能由生成此原因的设备实现，但目前不可用
     */
    BEARERCAPABILITY_NOTAVAIL(58, 503,"不支持的媒体类型","BEARERCAPABILITY_NOTAVAIL"),

    /**
     * 仅当服务或选项不可用类中没有其他原因适用时，此原因才用于报告服务或选项不可用事件
     */
    SERVICE_UNAVAILABLE(63, 503,"服务不可用","SERVICE_UNAVAILABLE"),

    /**
     * 此原因表示发送此原因的设备不支持请求的承载能力
     */
    BEARERCAPABILITY_NOTIMPL(65, 488,"不支持的媒体类型","BEARERCAPABILITY_NOTIMPL"),

    /**
     * 此原因表明发送此原因的设备不支持请求的通道类型
     */
    CHAN_NOT_IMPLEMENTED(66, 488,"不支持的媒体类型","CHAN_NOT_IMPLEMENTED"),

    /**
     * 此原因表明发送此原因的设备不支持请求的补充服务
     */
    FACILITY_NOT_IMPLEMENTED(69, 501,"不支持的媒体类型","FACILITY_NOT_IMPLEMENTED"),

    /**
     * 仅当服务或选项未实施类中没有其他原因时，此原因才用于报告服务或选项未实施事件
     */
    SERVICE_NOT_IMPLEMENTED(79, 501,"不支持的媒体类型","SERVICE_NOT_IMPLEMENTED"),

    /**
     * 此原因表示发送此原因的设备收到了一条带有呼叫参考的消息，该消息当前未在用户网络接口上使用
     */
    INVALID_CALL_REFERENCE(81, 500,"无效的呼叫参数","INVALID_CALL_REFERENCE"),

    /**
     * 此原因表示发送此原因的设备已收到建立呼叫的请求，该请求具有低层兼容性、高层兼容性或其他无法容纳的兼容性属性（例如数据速率）
     */
    INCOMPATIBLE_DESTINATION(88, 488,"设备不兼容","INCOMPATIBLE_DESTINATION"),

    /**
     * 仅当无效消息类中没有其他原因适用时，此原因才用于报告无效消息事件
     */
    INVALID_MSG_UNSPECIFIED(95, 488,"无效的消息","INVALID_MSG_UNSPECIFIED"),

    /**
     * 此原因表示发送此原因的设备收到了一条消息，该消息缺少信息元素，该信息元素必须存在于消息中，然后才能处理该消息
     */
    MANDATORY_IE_MISSING(96, 488,"缺少必选字段","MANDATORY_IE_MISSING"),

    /**
     * 此原因表示发送此原因的设备收到了一条消息，其消息类型无法识别，因为这是一条未定义或已定义但未由发送此原因的设备实施的消息
     */
    MESSAGE_TYPE_NONEXIST(97, 488,"消息类型无法识别","MESSAGE_TYPE_NONEXIST"),

    /**
     * 此原因表示发送此原因的设备已收到一条消息，因此程序不会指示这是在呼叫状态下允许接收的消息，或者收到指示呼叫状态不兼容的 STATUS 消息
     */
    WRONG_MESSAGE(98, 488,"错误的消息","WRONG_MESSAGE"),

    /**
     * 此原因表示发送此原因的设备收到了一条消息，其中包含无法识别的信息元素/参数，因为信息元素/参数名称未定义或已定义但未由发送原因的设备实施。此原因表明信息元素/参数已被丢弃。但是，消息中不需要存在信息元素，发送原因的设备就可以处理消息
     */
    IE_NONEXIST(99	, 488,"信息元素不存在","IE_NONEXIST"),

    /**
     * 此原因表明发送此原因的设备已收到其已实施的信息元素;但是，I.E. 中的一个或多个字段的编码方式尚未由发送此原因的设备实现
     */
    INVALID_IE_CONTENTS(100, 488,"信息元素内容无效","INVALID_IE_CONTENTS"),

    /**
     * 此原因表示收到了与呼叫状态不兼容的消息
     */
    WRONG_CALL_STATE(101, 488,"错误的呼叫状态","WRONG_CALL_STATE"),

    /**
     * 此原因表示与错误处理过程相关的计时器过期已启动过程。这通常与 NAT 问题有关。确保在 ATA 中打开“启用 NAT 映射”。如果它与 NAT 无关，则有时可能与提供程序相关，请确保确保其他出站提供程序无法解决问题。当远程方发送 408 for call expired 时，FreeSWITCH 也会返回此消息
     */
    RECOVERY_ON_TIMER_EXPIRE(102, 504,"呼叫超时","RECOVERY_ON_TIMER_EXPIRE"),

    /**
     * 此原因表示发送此原因的设备收到了一条消息，其中包含无法识别的参数，因为这些参数未定义或已定义但未由发送此原因的设备实施。原因指示参数被忽略。此外，如果发送此原因的设备是中间点，则此原因表示参数传递未更改
     */
    MANDATORY_IE_LENGTH_ERROR(103, 504,"参数不存在或无法识别","MANDATORY_IE_LENGTH_ERROR"),

    /**
     * 仅当协议错误类中没有其他原因适用时，此原因才用于报告协议错误事件
     */
    PROTOCOL_ERROR(111, 504,"协议错误","PROTOCOL_ERROR"),

    /**
     * 此原因表示网络调用（通常是对 SW56 服务的调用）已结束
     */
    INTERWORKING(127, 504,"网络未指定","INTERWORKING"),

    ORIGINATOR_CANCEL(487, 487,"呼叫被取消","ORIGINATOR_CANCEL"),

    CRASH(500, 487,"设备故障","CRASH"),

    SYSTEM_SHUTDOWN(501, 487,"设备故障","SYSTEM_SHUTDOWN"),
    LOSE_RACE(502, 487,"设备故障","LOSE_RACE"),
    MANAGER_REQUEST(503, 487,"设备故障","MANAGER_REQUEST"),

    BLIND_TRANSFER(600, 487,"盲转接","BLIND_TRANSFER"),
    ATTENDED_TRANSFER(601, 487,"转接失败","ATTENDED_TRANSFER"),
    ALLOTTED_TIMEOUT(602, 487,"分配超时","ALLOTTED_TIMEOUT"),
    USER_CHALLENGE(603, 487,"用户验证失败","USER_CHALLENGE"),
    MEDIA_TIMEOUT(604, 487,"媒体超时","MEDIA_TIMEOUT"),
    /**
     * 此原因意味着呼叫是通过从另一个分机拦截来接听的
     */
    PICKED_OFF(605, 487,"被叫被挂断","PICKED_OFF"),

    /**
     * 这意味着您尝试向忘记注册的 SIP 用户发起呼叫
     */
    USER_NOT_REGISTERED(606, 487,"用户未注册","USER_NOT_REGISTERED"),

    PROGRESS_TIMEOUT(607, 487,"呼叫进度超时","PROGRESS_TIMEOUT"),

    GATEWAY_DOWN(609, 487,"网关故障","GATEWAY_DOWN"),
    ;


    private Integer code;

    private Integer sipCause;

    private String desc;

    private String value;

    FsHangupCauseEnum(Integer code, Integer sipCause, String desc, String value) {
        this.code = code;
        this.sipCause = sipCause;
        this.desc = desc;
        this.value = value;
    }

    public static FsHangupCauseEnum getByCode(Integer code) {
        for (FsHangupCauseEnum hangupCauseEnum : FsHangupCauseEnum.values()) {
            if (hangupCauseEnum.getCode().equals(code)) {
                return hangupCauseEnum;
            }
        }
        return null;
    }

    public static FsHangupCauseEnum getBySipCause(Integer sipCause) {
        for (FsHangupCauseEnum hangupCauseEnum : FsHangupCauseEnum.values()) {
            if (hangupCauseEnum.getSipCause().equals(sipCause)) {
                return hangupCauseEnum;
            }
        }
        return null;
    }

    public static FsHangupCauseEnum getByValue(String value) {
        for (FsHangupCauseEnum hangupCauseEnum : FsHangupCauseEnum.values()) {
            if (hangupCauseEnum.getValue().equals(value)) {
                return hangupCauseEnum;
            }
        }
        return null;
    }


}
