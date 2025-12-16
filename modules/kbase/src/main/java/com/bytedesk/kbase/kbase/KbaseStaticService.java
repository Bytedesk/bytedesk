/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-30 07:04:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 09:22:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.kbase.article.ArticleResponse;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class KbaseStaticService {

    private final Configuration configuration;

    private final KbaseProperties kbaseProperties;

    private final KbaseRestService kbaseService;

    // 更新整个知识库
    public void updateKbase(KbaseEntity kbase) {
        // 静态化首页
        Page<CategoryResponse> categoriesPage = kbaseService.getCategories(kbase);
        Page<ArticleResponse> articlesPage = kbaseService.getArticles(kbase);
        //
        toHtmlKb(kbase, categoriesPage.getContent(), articlesPage, articlesPage, articlesPage);
        toHtmlSearch(kbase);
        // 遍历categoriesPage
        for (CategoryResponse category : categoriesPage.getContent()) {
            Page<ArticleResponse> articlesCategoryPage = kbaseService.getArticlesByCategory(kbase,
                    category.getUid());
            toHtmlCategory(kbase, category, categoriesPage.getContent(), articlesCategoryPage.getContent());
        }
        // 遍历articlesPage
        for (ArticleResponse article : articlesPage.getContent()) {
            toHtmlArticle(kbase, article, categoriesPage.getContent(), new ArrayList<>());
        }
    }

    // 生成知识库首页
    public void toHtmlKb(KbaseEntity kbase, List<CategoryResponse> categories,
        Page<ArticleResponse> articlesTop, Page<ArticleResponse> articlesHot, Page<ArticleResponse> articlesRecent) {
        //
        try {
            // 设置模板路径: classpath:/templates/ftl/kbase
            // configuration.setDirectoryForTemplateLoading(new
            // File(kbaseProperties.getTemplatePath()));
            // 加载模板
            Template template = configuration.getTemplate("/kbase/themes/" + kbase.getTheme() + "/index.ftl");
            // 数据模型
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);
            map.put("categories", categories);
            map.put("articlesTop", articlesTop);
            map.put("articlesHot", articlesHot);
            map.put("articlesRecent", articlesRecent);
            // 静态化页面内容
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, "UTF-8");
            //
            String saveHtmlPathWithKbUid = kbaseProperties.getHtmlPath() + "/" + kbase.getUid();
            log.info("toHtmlKb saveHtmlPathWithKbUid {}", saveHtmlPathWithKbUid);
            File file = new File(saveHtmlPathWithKbUid);
            if (!file.exists()) {
                file.mkdirs();
            }
            // 输出文件
            FileOutputStream fileOutputStream = new FileOutputStream(new File(saveHtmlPathWithKbUid + "/index.html"));
            IOUtils.copy(inputStream, fileOutputStream);
            // 关闭流
            inputStream.close();
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成知识库分类页
    public void toHtmlCategory(KbaseEntity kbase,
            CategoryResponse category,
            List<CategoryResponse> categories,
            List<ArticleResponse> articles) {
        //
        try {
            // 设置模板路径: classpath:/templates/ftl/kbase
            // configuration.setDirectoryForTemplateLoading(new
            // File(kbaseProperties.getTemplatePath()));
            // 加载模板
            Template template = configuration.getTemplate("/kbase/themes/" + kbase.getTheme() + "/category.ftl");
            // 数据模型
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);
            map.put("category", category);
            map.put("categories", categories);
            map.put("articles", articles);
            // 静态化页面内容
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, "UTF-8");
            //
            String saveHtmlPathWithKbUid = kbaseProperties.getHtmlPath() + "/" + kbase.getUid() + "/category";
            log.info("toHtmlCategory saveHtmlPathWithKbUid {}", saveHtmlPathWithKbUid);
            File file = new File(saveHtmlPathWithKbUid);
            if (!file.exists()) {
                file.mkdirs();
            }
            // 输出文件
            FileOutputStream fileOutputStream = new FileOutputStream(
                    new File(saveHtmlPathWithKbUid + "/" + category.getUid() + ".html"));
            IOUtils.copy(inputStream, fileOutputStream);
            // 关闭流
            inputStream.close();
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成知识库文章页
    public void toHtmlArticle(KbaseEntity kbase,
            ArticleResponse article,
            List<CategoryResponse> categories,
            List<ArticleResponse> related) {
        //
        try {
            // 设置模板路径: classpath:/templates/ftl/kbase
            // configuration.setDirectoryForTemplateLoading(new
            // File(kbaseProperties.getTemplatePath()));
            // 加载模板
            Template template = configuration.getTemplate("/kbase/themes/" + kbase.getTheme() + "/article.ftl");
            // 数据模型
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);
            map.put("article", article);
            map.put("categories", categories);
            map.put("related", related);
            // 静态化页面内容
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, "UTF-8");
            //
            String saveHtmlPathWithKbUid = kbaseProperties.getHtmlPath() + "/" + kbase.getUid() + "/article";
            log.info("toHtmlArticle saveHtmlPathWithKbUid {}", saveHtmlPathWithKbUid);
            File file = new File(saveHtmlPathWithKbUid);
            if (!file.exists()) {
                file.mkdirs();
            }
            // 输出文件
            FileOutputStream fileOutputStream = new FileOutputStream(
                    new File(saveHtmlPathWithKbUid + "/" + article.getUid() + ".html"));
            IOUtils.copy(inputStream, fileOutputStream);
            // 关闭流
            inputStream.close();
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 生成知识库搜索页
    public void toHtmlSearch(KbaseEntity kbase) {
        //
        try {
            // 设置模板路径: classpath:/templates/ftl/kbase
            // configuration.setDirectoryForTemplateLoading(new
            // File(kbaseProperties.getTemplatePath()));
            // 加载模板
            Template template = configuration.getTemplate("/kbase/themes/" + kbase.getTheme() + "/search.ftl");
            // 数据模型
            Map<String, Object> map = new HashMap<>();
            map.put("apiHost", kbaseProperties.getApiUrl());
            map.put("kbase", kbase);
            // 静态化页面内容
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, "UTF-8");
            //
            String saveHtmlPathWithKbUid = kbaseProperties.getHtmlPath() + "/" + kbase.getUid();
            log.info("toHtmlSearch saveHtmlPathWithKbUid {}", saveHtmlPathWithKbUid);
            File file = new File(saveHtmlPathWithKbUid);
            if (!file.exists()) {
                file.mkdirs();
            }
            // 输出文件
            FileOutputStream fileOutputStream = new FileOutputStream(new File(saveHtmlPathWithKbUid + "/search.html"));
            IOUtils.copy(inputStream, fileOutputStream);
            // 关闭流
            inputStream.close();
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
