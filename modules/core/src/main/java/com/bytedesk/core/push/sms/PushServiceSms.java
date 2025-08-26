/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:29:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 16:39:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

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

    @Async
    public void notify(MessageEntity e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notify'");
    }

    @Async
    public void send(String mobile, String country, String content, HttpServletRequest request) {
        log.info("send sms to {}, country: {}, content: {}", mobile, country, content);

        // 测试手机号不发送验证码
        if (Utils.isTestMobile(mobile)) {
            return;
        }

        // 白名单手机号使用固定验证码，无需真正发送验证码
        if (bytedeskProperties.isInWhitelist(mobile)) {
            return;
        }

        // 测试环境不发送验证码
        // if (bytedeskProperties.getDebug()) {
        //     return;
        // }

        sendValidateCode(mobile, country, content);
    }

    public void sendValidateCode(String mobile, String country, String code) {
        log.info("sendValidateCode sms to {}, country: {}, code: {}", mobile, country, code);

        DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", regionId);
        request.putQueryParameter("PhoneNumbers", mobile);
        // 已在init方法中处理了编码问题，此处直接使用
        log.debug("配置文件签名：{}", signName);
        request.putQueryParameter("SignName", signName);
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info("sendValidateCode sms response: {}", response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
    
    
}
