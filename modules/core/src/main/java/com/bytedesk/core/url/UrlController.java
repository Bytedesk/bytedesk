/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-08 09:48:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-08 10:10:36
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.url;

import java.io.IOException;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 短链控制器 */
@Slf4j
@Controller
@RequestMapping("/url")
@AllArgsConstructor
public class UrlController {

    private final UrlRestService urlRestService;
    
    @RequestMapping("/{shortUrl}")
    public void redirectUrl(HttpServletResponse response, @PathVariable("shortUrl") String shortUrl) {
        // 根据短链查询对应的长链
        Optional<UrlEntity> urlOptional = urlRestService.findByShortUrl(shortUrl);
        if (!urlOptional.isPresent()) {
            return;
        }
        // 重定向到长链
        String url = urlOptional.get().getUrl();
        log.info("url: {}", url);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            log.error("短链重定向失败 shortUrl: {}", shortUrl, e);
        }
    }
    
}
