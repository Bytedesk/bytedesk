package com.bytedesk.core.email_template;

/**
 * 邮件模板初始化数据
 * 定义系统默认邮件模板的主题和HTML内容
 */
public class EmailTemplateInitData {

    /** 工单创建通知 - 给客服 */
    public static final String TICKET_CREATED_AGENT_NAME = "工单创建通知（客服）";
    public static final String TICKET_CREATED_AGENT_SUBJECT = "新工单通知 - #{ticketNumber}";
    public static final String TICKET_CREATED_AGENT_CONTENT = """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>新工单通知</h2>
                <p>收到新的工单请求，详情如下：</p>
                <table style="border-collapse: collapse; width: 100%; max-width: 600px;">
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5; width: 120px;">工单编号</td><td style="padding: 8px; border: 1px solid #ddd;">#{ticketNumber}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">工单标题</td><td style="padding: 8px; border: 1px solid #ddd;">#{title}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">优先级</td><td style="padding: 8px; border: 1px solid #ddd;">#{priority}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">提交人</td><td style="padding: 8px; border: 1px solid #ddd;">#{reporter}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">描述</td><td style="padding: 8px; border: 1px solid #ddd;">#{description}</td></tr>
                </table>
                <p style="margin-top: 20px; color: #666;">请及时处理此工单。</p>
            </body>
            </html>
            """;

    /** 工单创建通知 - 给访客/客户 */
    public static final String TICKET_CREATED_VISITOR_NAME = "工单创建确认（客户）";
    public static final String TICKET_CREATED_VISITOR_SUBJECT = "工单提交成功 - #{ticketNumber}";
    public static final String TICKET_CREATED_VISITOR_CONTENT = """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>工单提交成功</h2>
                <p>感谢您的提交，我们的客服团队将尽快处理您的问题。</p>
                <table style="border-collapse: collapse; width: 100%; max-width: 600px;">
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5; width: 120px;">工单编号</td><td style="padding: 8px; border: 1px solid #ddd;">#{ticketNumber}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">工单标题</td><td style="padding: 8px; border: 1px solid #ddd;">#{title}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">提交时间</td><td style="padding: 8px; border: 1px solid #ddd;">#{createdAt}</td></tr>
                </table>
                <p style="margin-top: 20px; color: #666;">请您耐心等待，我们会尽快回复。</p>
            </body>
            </html>
            """;

    /** 工单状态变更通知 - 给访客/客户 */
    public static final String TICKET_STATUS_CHANGED_VISITOR_NAME = "工单状态变更通知（客户）";
    public static final String TICKET_STATUS_CHANGED_VISITOR_SUBJECT = "工单状态更新 - #{ticketNumber}";
    public static final String TICKET_STATUS_CHANGED_VISITOR_CONTENT = """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>工单状态更新</h2>
                <p>您的工单状态已更新：</p>
                <table style="border-collapse: collapse; width: 100%; max-width: 600px;">
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5; width: 120px;">工单编号</td><td style="padding: 8px; border: 1px solid #ddd;">#{ticketNumber}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">工单标题</td><td style="padding: 8px; border: 1px solid #ddd;">#{title}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">当前状态</td><td style="padding: 8px; border: 1px solid #ddd; color: #1890ff;">#{currentStatus}</td></tr>
                    #{previousStatusRow}
                </table>
                <p style="margin-top: 20px; color: #666;">如有疑问，请联系客服。</p>
            </body>
            </html>
            """;

    /** 工单关闭通知 - 给访客/客户 */
    public static final String TICKET_CLOSED_VISITOR_NAME = "工单已关闭通知（客户）";
    public static final String TICKET_CLOSED_VISITOR_SUBJECT = "工单已关闭 - #{ticketNumber}";
    public static final String TICKET_CLOSED_VISITOR_CONTENT = """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>工单已关闭</h2>
                <p>您的工单已处理完毕并关闭：</p>
                <table style="border-collapse: collapse; width: 100%; max-width: 600px;">
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5; width: 120px;">工单编号</td><td style="padding: 8px; border: 1px solid #ddd;">#{ticketNumber}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">工单标题</td><td style="padding: 8px; border: 1px solid #ddd;">#{title}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">关闭时间</td><td style="padding: 8px; border: 1px solid #ddd;">#{closedAt}</td></tr>
                </table>
                <p style="margin-top: 20px; color: #666;">感谢您的耐心等待，如有其他问题请随时联系我们。</p>
            </body>
            </html>
            """;

    /** 工单状态变更通知 - 给客服 */
    public static final String TICKET_STATUS_CHANGED_AGENT_NAME = "工单状态变更通知（客服）";
    public static final String TICKET_STATUS_CHANGED_AGENT_SUBJECT = "工单状态变更 - #{ticketNumber}";
    public static final String TICKET_STATUS_CHANGED_AGENT_CONTENT = """
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>工单状态变更</h2>
                <p>您负责的工单状态已更新：</p>
                <table style="border-collapse: collapse; width: 100%; max-width: 600px;">
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5; width: 120px;">工单编号</td><td style="padding: 8px; border: 1px solid #ddd;">#{ticketNumber}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">工单标题</td><td style="padding: 8px; border: 1px solid #ddd;">#{title}</td></tr>
                    <tr><td style="padding: 8px; border: 1px solid #ddd; background: #f5f5f5;">当前状态</td><td style="padding: 8px; border: 1px solid #ddd; color: #1890ff;">#{currentStatus}</td></tr>
                    #{previousStatusRow}
                </table>
            </body>
            </html>
            """;

    /** 所有默认邮件模板定义 */
    public static final EmailTemplateDef[] DEFAULT_TICKET_TEMPLATES = {
        new EmailTemplateDef(
            "TICKET_CREATED_AGENT",
            TICKET_CREATED_AGENT_NAME,
            TICKET_CREATED_AGENT_SUBJECT,
            TICKET_CREATED_AGENT_CONTENT,
            EmailTemplateTypeEnum.TICKET_REPLY.name(),
            "工单创建时通知客服的邮件模板"
        ),
        new EmailTemplateDef(
            "TICKET_CREATED_VISITOR",
            TICKET_CREATED_VISITOR_NAME,
            TICKET_CREATED_VISITOR_SUBJECT,
            TICKET_CREATED_VISITOR_CONTENT,
            EmailTemplateTypeEnum.TICKET_REPLY.name(),
            "工单创建后确认通知访客的邮件模板"
        ),
        new EmailTemplateDef(
            "TICKET_STATUS_CHANGED_VISITOR",
            TICKET_STATUS_CHANGED_VISITOR_NAME,
            TICKET_STATUS_CHANGED_VISITOR_SUBJECT,
            TICKET_STATUS_CHANGED_VISITOR_CONTENT,
            EmailTemplateTypeEnum.TICKET_REPLY.name(),
            "工单状态变更时通知访客的邮件模板"
        ),
        new EmailTemplateDef(
            "TICKET_CLOSED_VISITOR",
            TICKET_CLOSED_VISITOR_NAME,
            TICKET_CLOSED_VISITOR_SUBJECT,
            TICKET_CLOSED_VISITOR_CONTENT,
            EmailTemplateTypeEnum.TICKET_REPLY.name(),
            "工单关闭时通知访客的邮件模板"
        ),
        new EmailTemplateDef(
            "TICKET_STATUS_CHANGED_AGENT",
            TICKET_STATUS_CHANGED_AGENT_NAME,
            TICKET_STATUS_CHANGED_AGENT_SUBJECT,
            TICKET_STATUS_CHANGED_AGENT_CONTENT,
            EmailTemplateTypeEnum.TICKET_REPLY.name(),
            "工单状态变更时通知客服的邮件模板"
        )
    };

    /**
     * 邮件模板定义
     */
    public record EmailTemplateDef(
        String uid,
        String name,
        String subject,
        String content,
        String templateType,
        String description
    ) {}
}
