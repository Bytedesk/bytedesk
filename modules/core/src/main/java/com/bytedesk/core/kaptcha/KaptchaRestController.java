/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 19:17:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-12 15:47:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.kaptcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.imageio.ImageIO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.annotation.ApiRateLimiter;
import com.bytedesk.core.rbac.user.UserRequest;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.core.utils.Utils;
import com.google.code.kaptcha.Producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/kaptcha/api/v1")
public class KaptchaRestController {
    
    private final Producer kaptchaProducer;

    private final KaptchaRedisService kaptchaRedisService;

    // 获取验证码
    // http://127.0.0.1:9003/kaptcha/api/v1/get
    @ApiRateLimiter(value = 1, timeout = 1)
    @GetMapping("/get")
    public ResponseEntity<?> getKaptcha() {
        //
        String captchaUid = Utils.uuid();
        String code = kaptchaProducer.createText();
        log.info("getKaptcha captchaUid {} code {}", captchaUid, code);
        BufferedImage image = kaptchaProducer.createImage(code);
        // 转换流信息写出x
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", bos);
            byte[] byteArray = Base64.getEncoder().encode(bos.toByteArray());
            String imageString = "data:image/jpeg;base64," + new String(byteArray, StandardCharsets.UTF_8);
            //
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("captchaUid", captchaUid);
            jsonObject.put("captchaImage", imageString);
            // 缓存，用于做匹配
            kaptchaRedisService.putKaptcha(captchaUid, code);
            //
            return ResponseEntity.ok(JsonResult.success("kaptcha success", jsonObject));
        } catch (Exception e) {
            log.error("getKaptcha 生成验证码失败", e);
        }
        //
        return ResponseEntity.ok(JsonResult.error("kaptcha get failed"));
    }

    // 验证验证码
    @ApiRateLimiter(value = 1, timeout = 1)
    @PostMapping("/check")
    public ResponseEntity<?> checkKaptcha(@RequestBody UserRequest userRequest) {
        //
        String captchaUid = userRequest.getCaptchaUid();
        String captchaCode = userRequest.getCaptchaCode();
        String channel = userRequest.getChannel();
        log.info("checkKaptcha captchaUid {} captchaCode {}", captchaUid, captchaCode);
        if (kaptchaRedisService.checkKaptcha(captchaUid, captchaCode, channel)) {
            return ResponseEntity.ok(JsonResult.success("kaptcha success"));
        } else {
            return ResponseEntity.ok(JsonResult.error("kaptcha check failed", -1));
        }
    }

}
