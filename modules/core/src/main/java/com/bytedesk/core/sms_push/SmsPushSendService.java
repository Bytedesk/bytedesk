/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:29:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-05 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.sms_push;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.push.PushStatusEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * 短信发送服务
 */
@Slf4j
@Service
public class SmsPushSendService {

    public SmsPushSendService(
            ObjectProvider<SmsPushExternalSender> smsPushExternalSenderProvider,
            BytedeskProperties bytedeskProperties,
            SmsPushRepository smsPushRepository) {
        this.bytedeskProperties = bytedeskProperties;
        this.smsPushExternalSenderProvider = smsPushExternalSenderProvider;
        this.smsPushRepository = smsPushRepository;
    }


    @Value("${aliyun.region.id:cn-hangzhou}")
    private String regionId;

    @Value("${aliyun.access.key.id:}")
    private String accessKeyId;

    @Value("${aliyun.access.key.secret:}")
    private String accessKeySecret;

    @Value("${aliyun.sms.signname:}")
    private String signName;

    @Value("${aliyun.sms.templatecode:}")
    private String templateCode;

    @Value("${aliyun.sms.domain:dysmsapi.aliyuncs.com}")
    private String smsDomain;

    @Value("${aliyun.sms.version:2017-05-25}")
    private String smsVersion;

    @Value("${aliyun.sms.action:SendSms}")
    private String smsAction;

    /**
     * 默认关闭，开启后优先使用外部短信发送器。
     */
    @Value("${bytedesk.sms.external-enabled:false}")
    private boolean externalSmsPushEnabled;

    /**
     * 外部短信发送器 key，空值表示使用第一个可用发送器。
     */
    @Value("${bytedesk.sms.external-sender-key:}")
    private String externalSenderKey;
    
    /**
     * 初始化时处理配置项编码问题
     */
    @PostConstruct
    public void init() {
        try {
            // 检查签名是否为乱码，如果是则进行转换
            if (signName != null && !signName.isEmpty()) {
                boolean needsConversion = false;
                for (char c : signName.toCharArray()) {
                    if (c > 0x7F) { // 非ASCII字符
                        needsConversion = true;
                        break;
                    }
                }
                
                if (needsConversion) {
                    signName = new String(signName.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    log.info("短信签名编码转换完成: {}", signName);
                }
            }
        } catch (Exception e) {
            log.error("短信签名编码转换失败", e);
        }
    }

    private final BytedeskProperties bytedeskProperties;

    private final ObjectProvider<SmsPushExternalSender> smsPushExternalSenderProvider;

    private final SmsPushRepository smsPushRepository;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 发送短信
     * @param mobile 手机号
     * @param country 国家代码
     * @param content 短信内容
     * @param request HTTP请求
     * @return 是否发送成功
     */
    public boolean sendSms(String mobile, String country, String content, HttpServletRequest request) {
        SmsSendResult result = sendSmsWithResult(mobile, country, content, request);
        return result.isSuccess();
    }
    
    /**
     * 发送短信并返回详细结果
     * @param mobile 手机号
     * @param country 国家代码
     * @param content 短信内容
     * @param request HTTP请求
     * @return SmsPushSendResult 发送结果
     */
    public SmsSendResult sendSmsWithResult(String mobile, String country, String content, HttpServletRequest request) {
        Assert.hasText(content, "短信内容不能为空");
        
        String normalizedMobile = normalizeAndValidateMobile(mobile);
        log.info("send sms to {}, country: {}, content: {}", normalizedMobile, country, content);

        // 白名单手机号使用固定验证码，无需真正发送验证码。超级管理员手机号也认为发送成功，无论是否在白名单中，方便测试和管理员使用。
        if (bytedeskProperties.isInWhitelist(normalizedMobile) || bytedeskProperties.isAdminIdentifier(normalizedMobile)) {
            return SmsSendResult.success(); // 白名单手机号认为发送成功
        }

        if (externalSmsPushEnabled) {
            SmsSendResult externalResult = sendByExternalSenderIfAvailable(normalizedMobile, country, content, request);
            if (externalResult != null) {
                return externalResult;
            }
            log.warn("bytedesk.sms.external-enabled=true, but no external SMS sender is available, fallback to aliyun sender");
        }

        try {
            return sendValidateCode(normalizedMobile, country, content);
        } catch (Exception e) {
            log.error("发送短信失败", e);
            return SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED, "发送短信异常: " + e.getMessage());
        }
    }

    private SmsSendResult sendByExternalSenderIfAvailable(String mobile, String country, String code, HttpServletRequest request) {
        if (smsPushExternalSenderProvider == null) {
            return null;
        }

        SmsPushExternalSender sender;
        if (externalSenderKey == null || externalSenderKey.isBlank()) {
            sender = smsPushExternalSenderProvider.orderedStream().findFirst().orElse(null);
        } else {
            sender = smsPushExternalSenderProvider.orderedStream()
                    .filter(item -> externalSenderKey.equalsIgnoreCase(item.getSenderKey()))
                    .findFirst()
                    .orElse(null);
        }

        if (sender == null) {
            return null;
        }

        try {
            return sender.sendValidateCode(mobile, country, code, request);
        } catch (Exception exception) {
            log.error("外部短信发送失败", exception);
            return SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED, I18Consts.I18N_SMS_PUSH_SERVICE_UNAVAILABLE);
        }
    }

    /**
     * 发送验证码（原有流程，不做任何改动）
     * @param mobile 手机号
     * @param country 国家代码
     * @param code 验证码
     * @return SmsPushSendResult 发送结果
     */
    public SmsSendResult sendValidateCode(String mobile, String country, String code) {
        Assert.hasText(code, "验证码不能为空");

        String normalizedMobile = normalizeAndValidateMobile(mobile);
        log.info("sendValidateCode sms to {}, country: {}, code: {}", normalizedMobile, country, code);

        // 处理国家代码：只保留数字，中国86可以不添加前缀
        String phoneNumber = formatPhoneNumber(normalizedMobile, country);
        log.debug("格式化后的手机号: {}", phoneNumber);

        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(smsDomain);
        request.setSysVersion(smsVersion);
        request.setSysAction(smsAction);
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        // 已在init方法中处理了编码问题，此处直接使用
        log.debug("配置文件签名：{}", signName);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            // 发送失败提示：{"Message":"手机号码格式错误","RequestId":"42DC3C7D-DABE-5E13-AB10-873060508C47","Code":"isv.MOBILE_NUMBER_ILLEGAL"}
            // 发送成功提示：{"Message":"OK","RequestId":"1EA51590-4DBF-51EC-9FEC-812E7193C74D","Code":"OK","BizId":"458315458265098373^0"}
            log.info("sendValidateCode sms response: {}", response.getData());
            
            // 解析响应结果
            return parseAliyunSmsPushResponse(response.getData());
        } catch (ServerException e) {
            log.error("阿里云短信发送失败 - ServerException", e);
            return SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED,
                    resolveAliyunErrorMessage(e.getErrCode(), e.getErrMsg()));
        } catch (ClientException e) {
            String errorCode = e.getErrCode();
            String errorMessage = resolveAliyunErrorMessage(errorCode, e.getErrMsg());
            if (isAliyunCredentialOrPermissionError(errorCode)) {
                log.warn("阿里云短信配置异常: code={}, message={}", errorCode, e.getErrMsg());
            } else {
                log.error("阿里云短信发送失败 - ClientException: code={}, message={}", errorCode, e.getErrMsg(), e);
            }
            return SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED, errorMessage);
        }
    }

    /**
     * 使用指定签名和模板发送通用通知短信（非验证码场景）。
     * 模板参数中至少需包含 "content" 键，作为短信内容。
     *
     * @param mobile       手机号
     * @param country      国家代码
     * @param signName     短信签名（如：微语）
     * @param templateCode 阿里云短信模板编码（如：SMS_xxx）
     * @param templateParams 模板参数键值对
     * @return 发送结果
     */
    public SmsSendResult sendSmsWithTemplate(String mobile, String country, String signName,
            String templateCode, Map<String, String> templateParams, String orgUid) {
        Assert.hasText(mobile, "手机号不能为空");
        Assert.hasText(signName, "短信签名不能为空");
        Assert.hasText(templateCode, "短信模板编码不能为空");
        Assert.notEmpty(templateParams, "模板参数不能为空");

        String normalizedMobile = normalizeAndValidateMobile(mobile);
        String phoneNumber = formatPhoneNumber(normalizedMobile, country);

        String templateParamJson;
        try {
            templateParamJson = objectMapper.writeValueAsString(templateParams);
        } catch (JsonProcessingException e) {
            log.error("序列化模板参数失败", e);
            return SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED, "模板参数序列化失败");
        }

        log.info("sendSmsWithTemplate to {}, signName: {}, templateCode: {}, params: {}",
                normalizedMobile, signName, templateCode, templateParamJson);

        SmsSendResult result = doSendAliyunSms(phoneNumber, signName, templateCode, templateParamJson);

        // 记录短信发送历史
        String contentSummary = templateParams.containsKey("content")
                ? templateParams.get("content")
                : templateParamJson;
        saveSmsPushRecord(normalizedMobile, country, contentSummary, result, orgUid);
        return result;
    }

    /**
     * 执行阿里云短信 API 调用
     */
    private SmsSendResult doSendAliyunSms(String phoneNumber, String signName, String templateCode,
            String templateParamJson) {
        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain(smsDomain);
        request.setSysVersion(smsVersion);
        request.setSysAction(smsAction);
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", phoneNumber);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", templateParamJson);
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("aliyun sms response: {}", response.getData());
            return parseAliyunSmsPushResponse(response.getData());
        } catch (ServerException e) {
            log.error("阿里云短信发送失败 - ServerException", e);
            return SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED,
                    resolveAliyunErrorMessage(e.getErrCode(), e.getErrMsg()));
        } catch (ClientException e) {
            String errorCode = e.getErrCode();
            String errorMessage = resolveAliyunErrorMessage(errorCode, e.getErrMsg());
            if (isAliyunCredentialOrPermissionError(errorCode)) {
                log.warn("阿里云短信配置异常: code={}, message={}", errorCode, e.getErrMsg());
            } else {
                log.error("阿里云短信发送失败 - ClientException: code={}, message={}", errorCode, e.getErrMsg(), e);
            }
            return SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED, errorMessage);
        }
    }

    /**
     * 构建工单通知短信模板变量。
     * 仅输出 {@code variableNames} 中声明的键，值从上下文填充。
     * <p>支持的变量：name（客户称呼）、ticketNumber（工单编号）、status（状态）、title（工单标题）。</p>
     */
    public static Map<String, String> buildTicketSmsVariables(List<String> variableNames,
            String ticketNumber, String currentStatusLabel, String eventType, String reporterName) {
        Map<String, String> vars = new HashMap<>();
        Set<String> allowed = new HashSet<>(variableNames);
        if (allowed.contains("name") && reporterName != null) vars.put("name", reporterName);
        if (allowed.contains("ticketNumber")) vars.put("ticketNumber", ticketNumber);
        if (allowed.contains("status")) {
            vars.put("status", "TICKET_CREATED".equals(eventType) ? "已创建" : currentStatusLabel);
        }
        return vars;
    }

    /**
     * 保存短信发送记录到 SmsPushEntity
     */
    private void saveSmsPushRecord(String mobile, String country, String content, SmsSendResult result, String orgUid) {
        try {
            SmsPushEntity record = SmsPushEntity.builder()
                    .uid(UidUtils.getInstance().getUid())
                    .type("TICKET_NOTIFICATION")
                    .sender("SYSTEM")
                    .content(content)
                    .country(country)
                    .receiver(mobile)
                    .status(result.isSuccess() ? PushStatusEnum.SUCCESS.name() : PushStatusEnum.ERROR.name())
                    .sendSuccess(result.isSuccess())
                    .sendMessage(result.isSuccess() ? null : result.getErrorMessage())
                    .build();
            record.setOrgUid(orgUid);
            record.setCreatedAt(BdDateUtils.now());
            record.setUpdatedAt(BdDateUtils.now());
            smsPushRepository.save(record);
            log.debug("SmsPushEntity record saved: uid={}, mobile={}, orgUid={}, success={}", record.getUid(), mobile, orgUid, result.isSuccess());
        } catch (Exception e) {
            log.warn("Failed to save SmsPushEntity record for mobile {}: {}", mobile, e.getMessage());
        }
    }

    private String normalizeAndValidateMobile(String mobile) {
        String normalized = mobile == null ? "" : mobile.trim();
        Assert.hasText(normalized, "手机号不能为空");
        if (!normalized.matches("^\\d+$")) {
            throw new IllegalArgumentException("手机号必须为数字");
        }
        return normalized;
    }
    
    /**
     * 格式化手机号码，处理国家代码
     * @param mobile 手机号
     * @param country 国家代码
     * @return 格式化后的手机号
     */
    private String formatPhoneNumber(String mobile, String country) {
        // 处理国家代码：只保留数字
        String cleanCountry = "";
        if (country != null && !country.isEmpty()) {
            cleanCountry = country.replaceAll("[^0-9]", "");
        }
        
        // 中国86区号可以不添加前缀
        if ("86".equals(cleanCountry)) {
            return mobile;
        }
        
        // 其他国家需要添加国家代码前缀
        if (!cleanCountry.isEmpty()) {
            return cleanCountry + mobile;
        }
        
        // 如果没有国家代码，默认返回手机号（中国手机号）
        return mobile;
    }
    
    /**
     * 解析阿里云短信服务响应
     * @param responseData 响应JSON数据
     * @return SmsPushSendResult
     */
    private SmsSendResult parseAliyunSmsPushResponse(String responseData) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseData);
            String code = jsonNode.get("Code").asText();
            String message = jsonNode.get("Message").asText();
            
            // 判断是否发送成功
            if ("OK".equalsIgnoreCase(code)) {
                return SmsSendResult.success();
            } else {
                // 根据错误代码返回中文错误信息
                String errorMessage = resolveAliyunErrorMessage(code, message);
                return SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED, errorMessage);
            }
        } catch (Exception e) {
            log.error("解析阿里云短信响应失败", e);
            return SmsSendResult.failure(SmsSendResult.SendCodeErrorType.SEND_FAILED, I18Consts.I18N_SMS_PUSH_SERVICE_UNAVAILABLE);
        }
    }

    String resolveAliyunErrorMessage(String code, String originalMessage) {
        // if (isAliyunCredentialOrPermissionError(code)) {
        //     return I18Consts.I18N_SMS_PUSH_SERVICE_CONFIG_ERROR;
        // }
        String resolvedMessage = getChineseErrorMessage(code, originalMessage);
        if (resolvedMessage.startsWith("短信发送失败:")) {
            return I18Consts.I18N_SMS_PUSH_SERVICE_UNAVAILABLE;
        }
        return resolvedMessage;
    }

    boolean isAliyunCredentialOrPermissionError(String code) {
        return "InvalidAccessKeyId.Inactive".equals(code)
                || "InvalidAccessKeyId.NotFound".equals(code)
                || "SignatureDoesNotMatch".equals(code)
                || "Forbidden.RAM".equals(code)
                || "InvalidSecurityToken.Expired".equals(code)
                || "InvalidSecurityToken.MismatchWithAccessKey".equals(code);
    }

    /**
     * 根据阿里云错误代码返回中文错误信息
     * @param code 错误代码
     * @param originalMessage 原始错误信息
     * @return 中文错误信息
     */
    String getChineseErrorMessage(String code, String originalMessage) {
        switch (code) {
            case "isv.MOBILE_NUMBER_ILLEGAL":
                return "手机号码格式错误";
            case "isv.MOBILE_COUNT_OVER_LIMIT":
                return "手机号码数量超过限制";
            case "isv.TEMPLATE_MISSING_PARAMETERS":
                return "短信模板参数缺失";
            case "isv.BUSINESS_LIMIT_CONTROL":
                return "业务限流";
            case "isv.INVALID_PARAMETERS":
                return "参数错误";
            case "isv.SYSTEM_ERROR":
                return "系统错误";
            case "isv.OUT_OF_SERVICE":
                return "服务不可用";
            case "SignatureNonce.Duplicate":
                return "重复请求";
            case "InvalidTimeStamp.Expired":
                return "时间戳过期";
            case "SignatureDoesNotMatch":
                return "签名验证失败";
            case "InvalidAccessKeyId.NotFound":
                return "AccessKey不存在";
            case "Forbidden.RAM":
                return "RAM权限不足";
            case "isv.DAY_LIMIT_CONTROL":
                return "日发送量超限";
            case "isv.SMS_PUSH_CONTENT_ILLEGAL":
                return "短信内容包含违禁词";
            case "isv.SMS_PUSH_SIGN_ILLEGAL":
                return "短信签名不合规";
            default:
                return "短信发送失败: " + originalMessage;
        }
    }
    
}
