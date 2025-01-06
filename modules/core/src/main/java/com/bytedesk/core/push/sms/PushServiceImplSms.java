/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-31 15:29:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-19 10:58:58
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.push.PushNotifier;
// import com.bytedesk.core.utils.Utils;
import com.bytedesk.core.utils.Utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PushServiceImplSms extends PushNotifier {

    // @Value("${bytedesk.debug}")
    // private Boolean debug;

    @Value("${aliyun.access.key.id:}")
    private String accessKeyId;

    @Value("${aliyun.access.key.secret:}")
    private String accessKeySecret;

    @Value("${aliyun.sms.signname:}")
    private String signName;

    @Value("${aliyun.sms.templatecode:}")
    private String templateCode;

    @Autowired
    private BytedeskProperties bytedeskProperties;

    @Async
    @Override
    public void notify(MessageEntity e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notify'");
    }

    @Async
    @Override
    public void send(String mobile, String content, HttpServletRequest request) {
        log.info("send sms to {}, content: {}", mobile, content);

        // 测试手机号不发送验证码
        if (Utils.isTestMobile(mobile)) {
            return;
        }

        // 白名单手机号使用固定验证码，无需真正发送验证码
        if (bytedeskProperties.isInWhitelist(mobile)) {
            return;
        }

        // 测试环境不发送验证码
        if (bytedeskProperties.getDebug()) {
            return;
        }

        sendValidateCode(mobile, content);
    }

    public void sendValidateCode(String phone, String code) {

        // if (debug) {
        // return;
        // }

        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        // request.setMethod(MethodType.POST);
        // request.setDomain("dysmsapi.aliyuncs.com");
        // request.setVersion("2017-05-25");
        // request.setAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", phone);
        // FIXME:替换为配置文件方式，会发送失败
        request.putQueryParameter("SignName", "微语");
        request.putQueryParameter("TemplateCode", templateCode);
        request.putQueryParameter("TemplateParam", "{\"code\":\"" + code + "\"}");
        try {
            client.getCommonResponse(request);
            // CommonResponse response = client.getCommonResponse(request);
            // System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
