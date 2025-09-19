/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:30:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 09:49:16
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.bytedesk.core.utils.Utils;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * https://springdoc.cn/spring-boot-email/
 * https://springdoc.cn/spring/integration.html#mail
 * https://mailtrap.io/blog/spring-send-email/
 * https://www.thymeleaf.org/doc/articles/springmail.html
 * http://blog.didispace.com/springbootmailsender/
 */
@Slf4j
@Service
public class PushServiceEmail {

    @Autowired
    private BytedeskProperties bytedeskProperties;

    @Value("${aliyun.access.key.id:}")
    private String accessKeyId;

    @Value("${aliyun.access.key.secret:}")
    private String accessKeySecret;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String from;

    public boolean sendEmail(String email, String content, HttpServletRequest request) {
        // 为了保持向后兼容，调用新的方法并返回结果
        return sendEmailWithResult(email, content, request).isSuccess();
    }
    
    /**
     * 发送邮件并返回详细结果
     * @param email 邮箱地址
     * @param content 邮件内容
     * @param request HTTP请求
     * @return SendCodeResult 发送结果
     */
    public PushSendResult sendEmailWithResult(String email, String content, HttpServletRequest request) {
        Assert.hasText(email, "邮箱地址不能为空");
        Assert.hasText(content, "邮件内容不能为空");
        
        // log.info("send email to {}, content {}", email, content);

        // 测试邮箱不发送邮件
        if (Utils.isTestEmail(email)) {
            return PushSendResult.success(); // 测试邮箱认为发送成功
        }

        // 白名单邮箱使用固定验证码，无需真正发送验证码
        if (bytedeskProperties.isInWhitelist(email)) {
            return PushSendResult.success(); // 白名单邮箱认为发送成功
        }

        // 调试模式不发送邮件
        // if (bytedeskProperties.getDebug()) {
        //     return SendCodeResult.success(); // 调试模式认为发送成功
        // }

        try {
            boolean success;
            if (bytedeskProperties.getEmailType().equals("aliyun")) {
                success = sendAliyunValidateCode(email, content);
            } else {
                success = sendJavaMailValidateCode(email, content);
            }
            
            if (success) {
                return PushSendResult.success();
            } else {
                return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, "邮件发送失败");
            }
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, "邮件发送异常: " + e.getMessage());
        }
    }

    /**
     * 通过阿里云邮件推送SDK发送
     *
     * @param email Email
     * @param code  验证码
     * @return 发送是否成功
     */
    public boolean sendAliyunValidateCode(String email, String code) {
        Assert.hasText(email, "邮箱地址不能为空");
        Assert.hasText(code, "验证码不能为空");
        
        log.info("sendValidateCode email={} ,code={}", email, code);

        // TODO: 检测同一个ip是否短时间内有发送过验证码，如果短时间内发送过，则不发送

        // 如果是除杭州region外的其它region（如新加坡、澳洲Region），需要将下面的"cn-hangzhou"替换为"ap-southeast-1"、或"ap-southeast-2"。
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        // 如果是除杭州region外的其它region（如新加坡region）， 需要做如下处理
        // try {
        // DefaultProfile.addEndpoint("dm.ap-southeast-1.aliyuncs.com",
        // "ap-southeast-1", "Dm", "dm.ap-southeast-1.aliyuncs.com");
        // } catch (ClientException e) {
        // e.printStackTrace();
        // }
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {
            // request.setVersion("2017-06-22");//
            // 如果是除杭州region外的其它region（如新加坡region）,必须指定为2017-06-22
            request.setAccountName("notify@register.weiyuai.cn");
            request.setFromAlias("微语");
            request.setAddressType(1);
            request.setTagName("notify");
            request.setReplyToAddress(true);
            request.setToAddress(email);
            // 可以给多个收件人发送邮件，收件人之间用逗号分开，批量发信建议使用BatchSendMailRequest方式
            // request.setToAddress("邮箱1,邮箱2");
            request.setSubject("微语");
            request.setHtmlBody("您的验证码是" + code + ", 15分钟内有效。开源在线客服&企业IM系统, https://www.weiyuai.cn");
            // 开启需要备案，0关闭，1开启
            // request.setClickTrace("0");
            // 如果调用成功，正常返回httpResponse；如果调用失败则抛出异常，需要在异常中捕获错误异常码；错误异常码请参考对应的API文档;
            // SingleSendMailResponse httpResponse =
            client.getAcsResponse(request);
            return true; // 发送成功
        } catch (ServerException e) {
            // 捕获错误异常码
            log.error("阿里云邮件发送失败 - ServerException, ErrCode: {}", e.getErrCode(), e);
            return false;
        } catch (ClientException e) {
            // 捕获错误异常码
            log.error("阿里云邮件发送失败 - ClientException, ErrCode: {}", e.getErrCode(), e);
            return false;
        }
    }


    public boolean sendJavaMailValidateCode(String email, String code) {
        Assert.hasText(email, "邮箱地址不能为空");
        Assert.hasText(code, "验证码不能为空");
        
        log.info("sendJavaMailValidateCode email={} ,code={}", email, code);
        // 
        String content = "您的验证码是" + code + ", 15分钟内有效。开源在线客服&企业IM系统, https://www.weiyuai.cn";
        return sendJavaMail(email, "微语验证码", content);
    }

    /**
     * 通过JavaMail发送
     * https://springdoc.cn/spring-boot-email/
     * 
     * @param email
     * @param content
     * @return 发送是否成功
     */
    public boolean sendJavaMail(String email, String subject, String content) {
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
            return true; // 发送成功
        } catch (Exception e) {
            log.error("JavaMail发送邮件失败", e);
            return false;
        }
    }

}
