/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:30:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-05 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.email_provider;

import java.util.Properties;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.email_template.EmailTemplateContentTypeEnum;
import com.bytedesk.core.email_template.EmailTemplateEntity;
import com.bytedesk.core.email_template.EmailTemplateInitData;
import com.bytedesk.core.email_template.EmailTemplateRepository;
import com.bytedesk.core.system_config.email.PlatformEmailConfig;
import com.bytedesk.core.system_config.email.PlatformEmailConfigProvider;
import com.bytedesk.core.utils.Utils;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * 邮件发送服务
 * https://springdoc.cn/spring-boot-email/
 * https://springdoc.cn/spring/integration.html#mail
 * https://mailtrap.io/blog/spring-send-email/
 * https://www.thymeleaf.org/doc/articles/springmail.html
 * http://blog.didispace.com/springbootmailsender/
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class EmailSendService {

    /** 验证码邮件默认文案（模板不可用时的兑底，保持历史行为） */
    private static final String DEFAULT_VERIFY_CODE_CONTENT = "您的验证码是%s, 15分钟内有效。开源在线客服&企业IM系统, https://www.weiyuai.cn";

    /** 验证码邮件默认主题 */
    private static final String DEFAULT_VERIFY_CODE_SUBJECT = "微语验证码";

    private final BytedeskProperties bytedeskProperties;

    private final EmailTemplateRepository emailTemplateRepository;

    /**
     * 平台邮件配置 SPI（实现在 enterprise/core，读取 SuperSystemConfig 平台绑定）。
     * 无实现/未启用/未配置时为 null，自动回退 properties 配置。
     */
    private final ObjectProvider<PlatformEmailConfigProvider> platformEmailConfigProviderProvider;

    @Value("${aliyun.access.key.id:}")
    private String accessKeyId;

    @Value("${aliyun.access.key.secret:}")
    private String accessKeySecret;

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String from;

    /**
     * 发送邮件
     * @param email 邮箱地址
     * @param content 邮件内容
     * @param request HTTP请求
     * @return 是否发送成功
     */
    public boolean sendEmail(String email, String content, HttpServletRequest request) {
        return sendEmailWithResult(email, content, request).isSuccess();
    }
    
    /**
     * 发送邮件并返回详细结果
     * @param email 邮箱地址
     * @param content 邮件内容
     * @param request HTTP请求
     * @return EmailSendResult 发送结果
     */
    public EmailSendResult sendEmailWithResult(String email, String content, HttpServletRequest request) {
        return sendEmailWithResult(email, content, null, request);
    }

    /**
     * 发送邮件并返回详细结果（携带业务用途 type，P1 用途模型）
     * @param type 业务用途代码（AuthTypeEnum 名称，如 EMAIL_PASSWORD_RESET）；空时走平台默认配置
     */
    public EmailSendResult sendEmailWithResult(String email, String content, String type,
            HttpServletRequest request) {
        Assert.hasText(email, "邮箱地址不能为空");
        Assert.hasText(content, "邮件内容不能为空");

        // 测试邮箱不发送邮件
        if (Utils.isTestEmail(email)) {
            return EmailSendResult.success(); // 测试邮箱认为发送成功
        }

        // 白名单邮箱使用固定验证码，无需真正发送验证码。超级管理员邮箱也认为发送成功，无论是否在白名单中，方便测试和管理员使用。
        if (bytedeskProperties.isInWhitelist(email) || bytedeskProperties.isAdminIdentifier(email)) {
            return EmailSendResult.success(); // 白名单邮箱认为发送成功
        }

        try {
            // P1：优先使用该业务用途绑定的邮箱/模板（可不同于平台默认绑定），
            // 未绑定/未启用回退平台默认配置，再回退 properties
            PlatformEmailConfig platformConfig = getPlatformEmailConfig(type);
            if (platformConfig != null) {
                return sendPlatformValidateCodeWithResult(email, content, platformConfig);
            }
            if (bytedeskProperties.getEmailType().equals("aliyun")) {
                return sendAliyunValidateCodeWithResult(email, content);
            } else {
                return sendJavaMailValidateCodeWithResult(email, content);
            }
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            return EmailSendResult.failure(EmailSendResult.SendCodeErrorType.SEND_FAILED,
                    resolveEmailExceptionMessage(e));
        }
    }

    // ============ 平台邮件配置（SuperSystemConfig 平台绑定） ============

    /**
     * 获取平台邮件配置快照；无效/未启用/无 SPI 实现时返回 null（回退 properties）
     */
    PlatformEmailConfig getPlatformEmailConfig() {
        return getPlatformEmailConfig(null);
    }

    /**
     * 获取平台邮件配置快照（用途感知，P1）：purpose 非空时优先读取用途绑定快照，
     * 未绑定/未启用/配置不完整返回 null（回退平台默认配置）
     */
    PlatformEmailConfig getPlatformEmailConfig(String purpose) {
        if (platformEmailConfigProviderProvider == null) {
            return null;
        }
        PlatformEmailConfigProvider provider = platformEmailConfigProviderProvider.getIfAvailable();
        if (provider == null) {
            return null;
        }
        try {
            PlatformEmailConfig config = StringUtils.hasText(purpose)
                    ? provider.getPlatformEmailConfig(purpose)
                    : provider.getPlatformEmailConfig();
            if (config == null || !config.enabled()) {
                return null;
            }
            if (!StringUtils.hasText(config.emailAddress()) || !StringUtils.hasText(config.password())
                    || !StringUtils.hasText(config.smtpHost()) || config.smtpPort() == null) {
                log.warn("平台邮件配置不完整，回退默认配置: purpose={}", purpose);
                return null;
            }
            return config;
        } catch (Exception e) {
            log.error("读取平台邮件配置失败，回退默认配置: purpose={}", purpose, e);
            return null;
        }
    }

    /**
     * 平台用途测试邮件：使用指定配置快照发送验证码测试邮件（固定验证码 888888），
     * 与业务链路（sendEmailWithResult(email, code, type)）同一模板解析策略
     */
    public EmailSendResult sendPlatformTestEmail(String testEmail, PlatformEmailConfig config) {
        return sendPlatformValidateCodeWithResult(testEmail, "888888", config);
    }

    /**
     * 使用平台绑定邮箱（EmailProviderEntity）动态构建 JavaMailSender 发送验证码邮件
     */
    EmailSendResult sendPlatformValidateCodeWithResult(String email, String code, PlatformEmailConfig config) {
        Assert.hasText(email, "邮箱地址不能为空");
        Assert.hasText(code, "验证码不能为空");

        log.info("sendPlatformValidateCode email={}, platform sender={}", email, config.emailAddress());
        // 优先使用平台绑定的验证码邮件模板，未绑定时回退默认 EMAIL_VERIFY_CODE 模板
        EmailTemplateEntity verifyCodeTemplate = findVerifyCodeTemplate(config.verifyCodeTemplateUid());
        String content = renderVerifyCodeContent(verifyCodeTemplate, code);
        String subject = resolveVerifyCodeSubject(verifyCodeTemplate);
        String displayName = StringUtils.hasText(config.displayName()) ? config.displayName() : "weiyuai";
        return sendMailWithResult(createPlatformMailSender(config), config.emailAddress(), displayName,
                email, subject, content);
    }

    /**
     * 读取默认验证码邮件模板（EMAIL_VERIFY_CODE）。
     */
    private EmailTemplateEntity findVerifyCodeTemplate() {
        return findVerifyCodeTemplate(null);
    }

    /**
     * 读取验证码邮件模板：优先使用平台绑定的模板UID，为空时回退默认 EMAIL_VERIFY_CODE。
     * 模板不存在、已停用或读取异常时返回 null，发送链路回退默认文案，保证验证码始终能发出。
     */
    private EmailTemplateEntity findVerifyCodeTemplate(String templateUid) {
        try {
            String effectiveUid = StringUtils.hasText(templateUid)
                    ? templateUid
                    : EmailTemplateInitData.EMAIL_VERIFY_CODE_UID;
            return emailTemplateRepository.findByUid(effectiveUid)
                    .filter(template -> Boolean.TRUE.equals(template.getEnabled()))
                    .orElse(null);
        } catch (Exception e) {
            log.warn("读取验证码邮件模板失败，回退默认文案: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 解析验证码邮件主题：优先使用模板主题，未配置时回退默认主题
     */
    private String resolveVerifyCodeSubject(EmailTemplateEntity template) {
        if (template != null && StringUtils.hasText(template.getSubject())) {
            return template.getSubject();
        }
        return DEFAULT_VERIFY_CODE_SUBJECT;
    }

    /**
     * 渲染验证码邮件内容：将模板中 #{code} / ${code} 占位符替换为验证码。
     * TEXT 类型模板优先纯文本内容；模板不可用或内容为空时回退默认文案。
     */
    private String renderVerifyCodeContent(EmailTemplateEntity template, String code) {
        String content = null;
        if (template != null) {
            if (EmailTemplateContentTypeEnum.TEXT.name().equals(template.getContentType())
                    && StringUtils.hasText(template.getPlainText())) {
                content = template.getPlainText();
            } else if (StringUtils.hasText(template.getContent())) {
                content = template.getContent();
            }
        }
        if (content == null || content.isBlank()) {
            return String.format(DEFAULT_VERIFY_CODE_CONTENT, code);
        }
        return content.replace("#{code}", code).replace("${code}", code);
    }

    /**
     * 基于平台配置快照动态创建 JavaMailSender（逻辑对齐 EmailPushSendService#createMailSender）
     */
    private JavaMailSenderImpl createPlatformMailSender(PlatformEmailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.smtpHost());
        mailSender.setPort(config.smtpPort());
        mailSender.setUsername(config.emailAddress());
        mailSender.setPassword(config.password());
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        if (Boolean.TRUE.equals(config.smtpSslEnabled())) {
            if (config.smtpPort() != null && config.smtpPort() == 465) {
                props.put("mail.smtp.ssl.enable", "true");
            } else {
                props.put("mail.smtp.starttls.enable", "true");
            }
        }
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        return mailSender;
    }

    /**
     * 通过阿里云邮件推送SDK发送
     *
     * @param email EmailProvider
     * @param code  验证码
     * @return 发送是否成功
     */
    public boolean sendAliyunValidateCode(String email, String code) {
        return sendAliyunValidateCodeWithResult(email, code).isSuccess();
    }

    EmailSendResult sendAliyunValidateCodeWithResult(String email, String code) {
        Assert.hasText(email, "邮箱地址不能为空");
        Assert.hasText(code, "验证码不能为空");
        
        log.info("sendValidateCode email={} ,code={}", email, code);

        // 如果是除杭州region外的其它region（如新加坡、澳洲Region），需要将下面的"cn-hangzhou"替换为"ap-southeast-1"、或"ap-southeast-2"。
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            request.setAccountName("notify@register.weiyuai.cn");
            request.setFromAlias("微语");
            request.setAddressType(1);
            request.setTagName("notify");
            request.setReplyToAddress(true);
            request.setToAddress(email);
            EmailTemplateEntity verifyCodeTemplate = findVerifyCodeTemplate();
            request.setSubject(resolveVerifyCodeSubject(verifyCodeTemplate));
            request.setHtmlBody(renderVerifyCodeContent(verifyCodeTemplate, code));
            client.getAcsResponse(request);
            return EmailSendResult.success();
        } catch (ServerException e) {
            log.error("阿里云邮件发送失败 - ServerException, ErrCode: {}", e.getErrCode(), e);
            return EmailSendResult.failure(EmailSendResult.SendCodeErrorType.SEND_FAILED,
                    resolveAliyunEmailErrorMessage(e.getErrCode(), e.getErrMsg()));
        } catch (ClientException e) {
            String errorCode = e.getErrCode();
            if (isAliyunCredentialOrPermissionError(errorCode)) {
                log.warn("阿里云邮件配置异常: code={}, message={}", errorCode, e.getErrMsg());
            } else {
                log.error("阿里云邮件发送失败 - ClientException, ErrCode: {}", errorCode, e);
            }
            return EmailSendResult.failure(EmailSendResult.SendCodeErrorType.SEND_FAILED,
                    resolveAliyunEmailErrorMessage(errorCode, e.getErrMsg()));
        }
    }

    /**
     * 发送验证码邮件
     * @param email 邮箱地址
     * @param code 验证码
     * @return 是否发送成功
     */
    public boolean sendJavaMailValidateCode(String email, String code) {
        return sendJavaMailValidateCodeWithResult(email, code).isSuccess();
    }

    EmailSendResult sendJavaMailValidateCodeWithResult(String email, String code) {
        Assert.hasText(email, "邮箱地址不能为空");
        Assert.hasText(code, "验证码不能为空");
        
        log.info("sendJavaMailValidateCode email={} ,code={}", email, code);
        EmailTemplateEntity verifyCodeTemplate = findVerifyCodeTemplate();
        String content = renderVerifyCodeContent(verifyCodeTemplate, code);
        String subject = resolveVerifyCodeSubject(verifyCodeTemplate);
        return sendJavaMailWithResult(email, subject, content);
    }

    /**
     * 通过JavaMail发送
     * https://springdoc.cn/spring-boot-email/
     * 
     * @param email 邮箱地址
     * @param subject 邮件主题
     * @param content 邮件内容
     * @return 发送是否成功
     */
    public boolean sendJavaMail(String email, String subject, String content) {
        return sendJavaMailWithResult(email, subject, content).isSuccess();
    }

    EmailSendResult sendJavaMailWithResult(String email, String subject, String content) {
        Assert.hasText(email, "邮箱地址不能为空");
        Assert.hasText(subject, "邮件主题不能为空");
        Assert.hasText(content, "邮件内容不能为空");
        
        return sendMailWithResult(javaMailSender, from, "weiyuai", email, subject, content);
    }

    /**
     * 通用 JavaMail 发送（平台动态 sender 与 spring.mail.* 静态 sender 共用）
     */
    EmailSendResult sendMailWithResult(JavaMailSender sender, String fromAddress, String fromPersonal,
            String email, String subject, String content) {
        Assert.hasText(email, "邮箱地址不能为空");
        Assert.hasText(subject, "邮件主题不能为空");
        Assert.hasText(content, "邮件内容不能为空");

        // 创建一个邮件消息
        MimeMessage message = sender.createMimeMessage();
        try {
            // 创建 MimeMessageHelper
            // 必须显式指定 UTF-8：否则正文/主题/发件人昵称使用系统默认 MIME 字符集，
            // 在默认字符集非 UTF-8 的环境（如容器 C locale）中文会变成 ?
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            // 发件人邮箱和邮件中显示的发件人名字
            helper.setFrom(fromAddress, fromPersonal);
            // 收件人邮箱
            helper.setTo(email);
            // 邮件标题
            helper.setSubject(subject);
            // 邮件正文，第二个参数表示是否是HTML正文
            helper.setText(content, true);
            
            // 发送
            sender.send(message);
            return EmailSendResult.success();
        } catch (MailAuthenticationException e) {
            log.warn("JavaMail邮件配置异常: {}", e.getMessage());
            return EmailSendResult.failure(EmailSendResult.SendCodeErrorType.SEND_FAILED,
                    I18Consts.I18N_EMAIL_SERVICE_CONFIG_ERROR);
        } catch (MailSendException | MailParseException e) {
            if (isJavaMailConfigError(e)) {
                log.warn("JavaMail邮件配置异常: {}", e.getMessage());
                return EmailSendResult.failure(EmailSendResult.SendCodeErrorType.SEND_FAILED,
                        I18Consts.I18N_EMAIL_SERVICE_CONFIG_ERROR);
            }
            log.error("JavaMail发送邮件失败", e);
            return EmailSendResult.failure(EmailSendResult.SendCodeErrorType.SEND_FAILED,
                    I18Consts.I18N_EMAIL_SERVICE_UNAVAILABLE);
        } catch (MailException e) {
            log.error("JavaMail发送邮件失败", e);
            return EmailSendResult.failure(EmailSendResult.SendCodeErrorType.SEND_FAILED,
                    I18Consts.I18N_EMAIL_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            if (isJavaMailConfigError(e)) {
                log.warn("JavaMail邮件配置异常: {}", e.getMessage());
                return EmailSendResult.failure(EmailSendResult.SendCodeErrorType.SEND_FAILED,
                        I18Consts.I18N_EMAIL_SERVICE_CONFIG_ERROR);
            }
            log.error("JavaMail发送邮件失败", e);
            return EmailSendResult.failure(EmailSendResult.SendCodeErrorType.SEND_FAILED,
                    I18Consts.I18N_EMAIL_SERVICE_UNAVAILABLE);
        }
    }

    String resolveEmailExceptionMessage(Exception e) {
        if (e instanceof MailAuthenticationException) {
            return I18Consts.I18N_EMAIL_SERVICE_CONFIG_ERROR;
        }
        if (e instanceof MailException) {
            return isJavaMailConfigError(e)
                    ? I18Consts.I18N_EMAIL_SERVICE_CONFIG_ERROR
                    : I18Consts.I18N_EMAIL_SERVICE_UNAVAILABLE;
        }
        return I18Consts.I18N_EMAIL_SERVICE_UNAVAILABLE;
    }

    String resolveAliyunEmailErrorMessage(String code, String originalMessage) {
        if (isAliyunCredentialOrPermissionError(code)) {
            return I18Consts.I18N_EMAIL_SERVICE_CONFIG_ERROR;
        }
        return I18Consts.I18N_EMAIL_SERVICE_UNAVAILABLE;
    }

    boolean isAliyunCredentialOrPermissionError(String code) {
        return "InvalidAccessKeyId.Inactive".equals(code)
                || "InvalidAccessKeyId.NotFound".equals(code)
                || "SignatureDoesNotMatch".equals(code)
                || "Forbidden.RAM".equals(code)
                || "InvalidSecurityToken.Expired".equals(code)
                || "InvalidSecurityToken.MismatchWithAccessKey".equals(code);
    }

    boolean isJavaMailConfigError(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            if (message != null) {
                String normalized = message.toLowerCase();
                if (normalized.contains("authentication failed")
                        || normalized.contains("auth fail")
                        || normalized.contains("could not connect to smtp host")
                        || normalized.contains("unknown smtp host")
                        || normalized.contains("connection refused")
                        || normalized.contains("no such provider")
                        || normalized.contains("mail server connection failed")
                        || normalized.contains("failed to connect")
                        || normalized.contains("from address must not be null")
                        || normalized.contains("illegal address")
                        || normalized.contains("could not convert socket to tls")
                        || normalized.contains("javax.mail.authenticationfailedexception")
                        || normalized.contains("jakarta.mail.authenticationfailedexception")) {
                    return true;
                }
            }
            current = current.getCause();
        }
        return false;
    }

}
