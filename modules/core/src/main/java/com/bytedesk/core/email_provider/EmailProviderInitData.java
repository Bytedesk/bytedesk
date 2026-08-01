package com.bytedesk.core.email_provider;

/**
 * 邮件服务提供商初始化数据
 * 定义各主流邮件服务商的默认 SMTP/IMAP/POP3 配置，用户只需填写邮箱地址和授权码即可使用
 */
public class EmailProviderInitData {

    /** 所有默认邮件服务提供商定义 */
    public static final EmailProviderDef[] DEFAULT_EMAIL_PROVIDERS = {
        // ========== QQ 邮箱 ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_QQ",
            "QQ邮箱",
            EmailProviderProviderEnum.QQ.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp.qq.com", 587, true, true,
            "imap.qq.com", 993, true,
            "pop.qq.com", 995, true,
            null, null, null,
            5, true, false, ""
        ),
        // ========== 腾讯企业邮箱 ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_TENCENT_ENTERPRISE",
            "腾讯企业邮箱",
            EmailProviderProviderEnum.TENCENT_ENTERPRISE.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp.exmail.qq.com", 587, true, true,
            "imap.exmail.qq.com", 993, true,
            "pop.exmail.qq.com", 995, true,
            null, null, null,
            5, true, false, ""
        ),
        // ========== 新浪邮箱 ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_SINA",
            "新浪邮箱",
            EmailProviderProviderEnum.SINA.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp.sina.com", 587, true, true,
            "imap.sina.com", 993, true,
            "pop.sina.com", 995, true,
            null, null, null,
            5, true, false, ""
        ),
        // ========== Gmail ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_GMAIL",
            "Gmail",
            EmailProviderProviderEnum.GMAIL.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp.gmail.com", 587, false, true,
            "imap.gmail.com", 993, true,
            "pop.gmail.com", 995, true,
            null, null, null,
            5, true, false, ""
        ),
        // ========== 网易 163 邮箱 ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_NETEASE_163",
            "网易163邮箱",
            EmailProviderProviderEnum.NETEASE_163.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp.163.com", 587, true, true,
            "imap.163.com", 993, true,
            "pop.163.com", 995, true,
            null, null, null,
            5, true, false, ""
        ),
        // ========== 网易企业邮箱 ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_NETEASE_ENTERPRISE",
            "网易企业邮箱",
            EmailProviderProviderEnum.NETEASE_ENTERPRISE.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp.ym.163.com", 587, true, true,
            "imap.ym.163.com", 993, true,
            "pop.ym.163.com", 995, true,
            null, null, null,
            5, true, false, ""
        ),
        // ========== 阿里企业邮箱 ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_ALIYUN_ENTERPRISE",
            "阿里企业邮箱",
            EmailProviderProviderEnum.ALIYUN_ENTERPRISE.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp.qiye.aliyun.com", 465, true, true, // 发信服务器
            "imap.qiye.aliyun.com", 993, true, // 收信服务器
            "pop.qiye.aliyun.com", 995, true, // 收信服务器
            null, null, null,
            5, true, false, ""
        ),
        // ========== Hotmail / Outlook ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_HOTMAIL",
            "Outlook/Hotmail",
            EmailProviderProviderEnum.HOTMAIL.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp-mail.outlook.com", 587, false, true,
            "imap-mail.outlook.com", 993, true,
            "pop-mail.outlook.com", 995, true,
            null, null, null,
            5, true, false, ""
        ),
        // ========== 飞书企业邮箱 ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_FEISHU_ENTERPRISE",
            "飞书企业邮箱",
            EmailProviderProviderEnum.FEISHU_ENTERPRISE.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp.feishu.cn", 587, false, true,
            "imap.feishu.cn", 993, true,
            "pop.feishu.cn", 995, true,
            null, null, null,
            5, true, false, ""
        ),
        // ========== Yahoo 邮箱 ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_YAHOO",
            "Yahoo邮箱",
            EmailProviderProviderEnum.YAHOO.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "smtp.mail.yahoo.com", 587, false, true,
            "imap.mail.yahoo.com", 993, true,
            "pop.mail.yahoo.com", 995, true,
            null, null, null,
            5, true, false, ""
        ),
        // ========== 其他（通用模板） ==========
        new EmailProviderDef(
            "EMAIL_PROVIDER_OTHER",
            "其他邮箱",
            EmailProviderProviderEnum.OTHER.name(),
            EmailProviderTypeEnum.TICKET.name(),
            EmailProviderProtocolEnum.IMAP.name(),
            "", 587, true, true,
            "", 993, true,
            "", 995, true,
            null, null, null,
            5, true, false, ""
        )
    };

    /**
     * 邮件服务提供商定义
     * 预设了常见邮件服务商的 SMTP/IMAP/POP3 配置，敏感字段留空由用户填写
     */
    public record EmailProviderDef(
        String uid,
        String name,
        String provider,
        String type,
        String protocol,
        // SMTP
        String smtpHost,
        Integer smtpPort,
        Boolean smtpSslEnabled,
        Boolean smtpTlsEnabled,
        // IMAP
        String imapHost,
        Integer imapPort,
        Boolean imapSslEnabled,
        // POP3
        String pop3Host,
        Integer pop3Port,
        Boolean pop3SslEnabled,
        // Exchange
        String exchangeHost,
        Integer exchangePort,
        Boolean exchangeSslEnabled,
        // 其他
        Integer syncInterval,
        Boolean autoSyncEnabled,
        Boolean autoReplyEnabled,
        String autoReplyContent
    ) {}
}
