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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
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
import com.bytedesk.core.utils.Utils;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

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

    private final BytedeskProperties bytedeskProperties;

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
            request.setSubject("微语");
            request.setHtmlBody("您的验证码是" + code + ", 15分钟内有效。开源在线客服&企业IM系统, https://www.weiyuai.cn");
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
        String content = "您的验证码是" + code + ", 15分钟内有效。开源在线客服&企业IM系统, https://www.weiyuai.cn";
        return sendJavaMailWithResult(email, "微语验证码", content);
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
        
        // 创建一个邮件消息
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            // 创建 MimeMessageHelper
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            // 发件人邮箱和邮件中显示的发件人名字
            helper.setFrom(from, "weiyuai");
            // 收件人邮箱
            helper.setTo(email);
            // 邮件标题
            helper.setSubject(subject);
            // 邮件正文，第二个参数表示是否是HTML正文
            helper.setText(content, true);
            
            // 发送
            javaMailSender.send(message);
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
