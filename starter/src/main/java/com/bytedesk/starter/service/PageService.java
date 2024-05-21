/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-13 12:09:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-05-16 16:55:20
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.starter.service;

import org.springframework.stereotype.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * only used for development
 * 页面服务，仅用于开发阶段
 */
@Slf4j
@Service
public class PageService {

    private static final String htmlSavePath = "/templates/";
    private static final String templatePath = "/templates/ftl/";
    
    @Autowired
    Configuration configuration;

    public void index() {
        toHtml("index");
    }

    public void download() {
        toHtml("download");
    }

    public void about() {
        toHtml("about");
    }

    public void contact() {
        toHtml("contact");
    }

    public void privacy() {
        toHtml("privacy");
    }

    public void protocal() {
        toHtml("protocal");
    }

    private void toHtml(String tempName) {

        try {
            // 设置模板路径
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + templatePath));
            // 加载模板
            Template template = configuration.getTemplate(tempName + ".ftl");
            // 数据模型
            Map<String, Object> map = new HashMap<>();
            // map.put("myTitle", "页面静态化(PageStatic)");
            // map.put("tableList", getList());
            // map.put("imgList", getImgList());
            // 静态化页面内容
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            // log.info("content:{}", content);
            InputStream inputStream = IOUtils.toInputStream(content, "UTF-8");
            // 输出文件
            String savePath = classpath + htmlSavePath + tempName + ".html";
            log.info("savePath {}", savePath);
            FileOutputStream fileOutputStream = new FileOutputStream(new File(savePath));
            IOUtils.copy(inputStream, fileOutputStream);
            // 关闭流
            inputStream.close();
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
