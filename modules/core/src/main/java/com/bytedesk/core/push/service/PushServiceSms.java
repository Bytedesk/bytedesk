/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:29:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 15:15:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.bytedesk.core.utils.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
@Service
public class PushServiceSms {

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
    
    /**
     * 初始化时处理配置项编码问题
     */
    @Autowired
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
                    signName = new String(signName.getBytes("ISO-8859-1"), "UTF-8");
                    log.info("短信签名编码转换完成: {}", signName);
                }
            }
        } catch (Exception e) {
            log.error("短信签名编码转换失败", e);
        }
    }

    @Autowired
    private BytedeskProperties bytedeskProperties;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean sendSms(String mobile, String country, String content, HttpServletRequest request) {
        PushSendResult result = sendSmsWithResult(mobile, country, content, request);
        return result.isSuccess();
    }
    
    /**
     * 发送短信并返回详细结果
     * @param mobile 手机号
     * @param country 国家代码
     * @param content 短信内容
     * @param request HTTP请求
     * @return SendCodeResult 发送结果
     */
    public PushSendResult sendSmsWithResult(String mobile, String country, String content, HttpServletRequest request) {
        Assert.hasText(mobile, "手机号不能为空");
        Assert.hasText(content, "短信内容不能为空");
        
        log.info("send sms to {}, country: {}, content: {}", mobile, country, content);

        // 测试手机号不发送验证码
        if (Utils.isTestMobile(mobile)) {
            return PushSendResult.success(); // 测试手机号认为发送成功
        }

        // 白名单手机号使用固定验证码，无需真正发送验证码
        if (bytedeskProperties.isInWhitelist(mobile)) {
            return PushSendResult.success(); // 白名单手机号认为发送成功
        }

        // 测试环境不发送验证码
        // if (bytedeskProperties.getDebug()) {
        //     return SendCodeResult.success(); // 测试环境认为发送成功
        // }

        try {
            return sendValidateCode(mobile, country, content);
        } catch (Exception e) {
            log.error("发送短信失败", e);
            return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, "发送短信异常: " + e.getMessage());
        }
    }

    public PushSendResult sendValidateCode(String mobile, String country, String code) {
        Assert.hasText(mobile, "手机号不能为空");
        Assert.hasText(code, "验证码不能为空");
        
        log.info("sendValidateCode sms to {}, country: {}, code: {}", mobile, country, code);

        // 处理国家代码：只保留数字，中国86可以不添加前缀
        String phoneNumber = formatPhoneNumber(mobile, country);
        log.debug("格式化后的手机号: {}", phoneNumber);

        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
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
            return parseAliyunSmsResponse(response.getData());
        } catch (ServerException e) {
            log.error("阿里云短信发送失败 - ServerException", e);
            return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, "阿里云服务器错误: " + e.getErrMsg());
        } catch (ClientException e) {
            log.error("阿里云短信发送失败 - ClientException", e);
            return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, "阿里云客户端错误: " + e.getErrMsg());
        }
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
     * @return SendCodeResult
     */
    private PushSendResult parseAliyunSmsResponse(String responseData) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseData);
            String code = jsonNode.get("Code").asText();
            String message = jsonNode.get("Message").asText();
            
            // 判断是否发送成功
            if ("OK".equalsIgnoreCase(code)) {
                return PushSendResult.success();
            } else {
                // 根据错误代码返回中文错误信息
                String errorMessage = getChineseErrorMessage(code, message);
                return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, errorMessage);
            }
        } catch (Exception e) {
            log.error("解析阿里云短信响应失败", e);
            return PushSendResult.failure(PushSendResult.SendCodeErrorType.SEND_FAILED, "解析短信服务响应失败");
        }
    }    /**
     * 根据阿里云错误代码返回中文错误信息
     * @param code 错误代码
     * @param originalMessage 原始错误信息
     * @return 中文错误信息
     */
    private String getChineseErrorMessage(String code, String originalMessage) {
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
            case "isv.SMS_CONTENT_ILLEGAL":
                return "短信内容包含违禁词";
            case "isv.SMS_SIGN_ILLEGAL":
                return "短信签名不合规";
            default:
                return "短信发送失败: " + originalMessage;
        }
    }
    
}
