/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-18 19:17:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-29 10:57:57
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
import java.util.Map;
import java.util.Base64;

import javax.imageio.ImageIO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson2.JSONObject;
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
    
    private final Producer captchaProducer;

    private final KaptchaRedisService kaptchaCacheService;

    // 获取验证码
    // http://127.0.0.1:9003/kaptcha/api/v1/get
    @GetMapping("/get")
    public ResponseEntity<?> getKaptcha() {
        //
        String captchaUid = Utils.uuid();
        String code = captchaProducer.createText();
        log.info("getKaptcha captchaUid {} code {}", captchaUid, code);
        BufferedImage image = captchaProducer.createImage(code);
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
            kaptchaCacheService.putKaptcha(captchaUid, code);
            //
            return ResponseEntity.ok(JsonResult.success("kaptcha success", jsonObject));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //
        return ResponseEntity.ok(JsonResult.error("kaptcha get failed"));
    }

    // 验证验证码
    @PostMapping("/check")
    public ResponseEntity<?> checkKaptcha(@RequestBody Map<String, String> map) {
        //
        String captchaUid = map.get("captchaUid");
        String captchaCode = map.get("captchaCode");
        String client = map.get("client");
        log.info("checkKaptcha captchaUid {} captchaCode {}", captchaUid, captchaCode);
        if (kaptchaCacheService.checkKaptcha(captchaUid, captchaCode, client)) {
            return ResponseEntity.ok(JsonResult.success("kaptcha success"));
        } else {
            return ResponseEntity.ok(JsonResult.error("kaptcha check failed", -1));
        }
    }

}
