/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-04
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-04
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.kbase.blog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryRepository;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseProperties;
import com.bytedesk.kbase.kbase.KbaseRepository;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class BlogStaticService {

    private final Configuration configuration;

    private final KbaseProperties kbaseProperties;

    private final KbaseRepository kbaseRepository;

    private final CategoryRepository categoryRepository;

    private final BlogRepository blogRepository;

    private final BlogRestService blogRestService;

    public void updateBlogKbase(String kbUid) {
        KbaseEntity kbase = kbaseRepository.findByUid(kbUid)
                .orElseThrow(() -> new RuntimeException("kbase not found: " + kbUid));
        List<CategoryEntity> categories = categoryRepository.findByKbUidAndDeletedFalse(kbUid);
        List<BlogResponse> blogs = blogRepository.findByKbUidAndDeletedFalse(kbUid)
                .stream()
                .map(blogRestService::convertToResponse)
                .collect(Collectors.toList());

        toHtmlIndex(kbase, categories, blogs);
        toHtmlSearch(kbase);
        for (CategoryEntity category : categories) {
            List<BlogResponse> categoryBlogs = blogRepository
                    .findByKbUidAndCategoryUidAndDeletedFalse(kbUid, category.getUid())
                    .stream()
                    .map(blogRestService::convertToResponse)
                    .collect(Collectors.toList());
            toHtmlCategory(kbase, category, categories, categoryBlogs);
        }
        for (BlogResponse blog : blogs) {
            toHtmlPost(kbase, blog, categories);
        }
    }

    public void updateBlogPost(String blogUid) {
        BlogEntity entity = blogRepository.findByUid(blogUid)
                .orElseThrow(() -> new RuntimeException("blog not found: " + blogUid));
        if (!entity.isDeleted()) {
            toHtmlPost(entity.getKbUid(), blogRestService.convertToResponse(entity));
            // 同步更新 index/category/search
            updateBlogIndex(entity.getKbUid());
        }
    }

    public void updateBlogIndex(String kbUid) {
        KbaseEntity kbase = kbaseRepository.findByUid(kbUid)
                .orElseThrow(() -> new RuntimeException("kbase not found: " + kbUid));
        List<CategoryEntity> categories = categoryRepository.findByKbUidAndDeletedFalse(kbUid);
        List<BlogResponse> blogs = blogRepository.findByKbUidAndDeletedFalse(kbUid)
                .stream()
                .map(blogRestService::convertToResponse)
                .collect(Collectors.toList());
        toHtmlIndex(kbase, categories, blogs);
        toHtmlSearch(kbase);

        // 同步生成分类页，避免访问 /category/{uid}.html 返回 404
        for (CategoryEntity category : categories) {
            List<BlogResponse> categoryBlogs = blogRepository
                .findByKbUidAndCategoryUidAndDeletedFalse(kbUid, category.getUid())
                .stream()
                .map(blogRestService::convertToResponse)
                .collect(Collectors.toList());
            toHtmlCategory(kbase, category, categories, categoryBlogs);
        }
    }

    public void deleteBlogPostStatic(String kbUid, String blogUid) {
        String root = getBlogHtmlRoot(kbUid);
        File file = new File(root + "/post/" + blogUid + ".html");
        if (file.exists() && file.isFile()) {
            boolean ok = file.delete();
            log.info("deleteBlogPostStatic {} => {}", file.getAbsolutePath(), ok);
        }
        // 删除后也更新首页与分类页（避免仍然展示）
        updateBlogIndex(kbUid);
    }

    public void toHtmlPost(String kbUid, BlogResponse blog) {
        KbaseEntity kbase = kbaseRepository.findByUid(kbUid)
                .orElseThrow(() -> new RuntimeException("kbase not found: " + kbUid));
        List<CategoryEntity> categories = categoryRepository.findByKbUidAndDeletedFalse(kbUid);
        toHtmlPost(kbase, blog, categories);
    }

    // blog 首页
    public void toHtmlIndex(KbaseEntity kbase, List<CategoryEntity> categories, List<BlogResponse> blogs) {
        try {
            Template template = configuration.getTemplate(getTemplatePath(kbase, "index.ftl"));
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);
            map.put("categories", categories);
            map.put("blogs", blogs);

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);

            String root = getBlogHtmlRoot(kbase.getUid());
            ensureDir(root);
            FileOutputStream out = new FileOutputStream(new File(root + "/index.html"));
            IOUtils.copy(inputStream, out);
            inputStream.close();
            out.close();

        } catch (Exception e) {
            log.error("toHtmlIndex failed", e);
        }
    }

    // blog 分类页
    public void toHtmlCategory(KbaseEntity kbase, CategoryEntity category, List<CategoryEntity> categories,
            List<BlogResponse> blogs) {
        try {
            Template template = configuration.getTemplate(getTemplatePath(kbase, "category.ftl"));
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);
            map.put("category", category);
            map.put("categories", categories);
            map.put("blogs", blogs);

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);

            String root = getBlogHtmlRoot(kbase.getUid()) + "/category";
            ensureDir(root);
            FileOutputStream out = new FileOutputStream(new File(root + "/" + category.getUid() + ".html"));
            IOUtils.copy(inputStream, out);
            inputStream.close();
            out.close();

        } catch (Exception e) {
            log.error("toHtmlCategory failed", e);
        }
    }

    // blog 文章页
    public void toHtmlPost(KbaseEntity kbase, BlogResponse blog, List<CategoryEntity> categories) {
        try {
            Template template = configuration.getTemplate(getTemplatePath(kbase, "post.ftl"));
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);
            map.put("categories", categories);
            map.put("blog", blog);

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);

            String root = getBlogHtmlRoot(kbase.getUid()) + "/post";
            ensureDir(root);
            FileOutputStream out = new FileOutputStream(new File(root + "/" + blog.getUid() + ".html"));
            IOUtils.copy(inputStream, out);
            inputStream.close();
            out.close();

        } catch (Exception e) {
            log.error("toHtmlPost failed", e);
        }
    }

    // blog 搜索页（静态模板 + 前端自行调用 API 或在模板中做简单搜索）
    public void toHtmlSearch(KbaseEntity kbase) {
        try {
            Template template = configuration.getTemplate(getTemplatePath(kbase, "search.ftl"));
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);

            String root = getBlogHtmlRoot(kbase.getUid());
            ensureDir(root);
            FileOutputStream out = new FileOutputStream(new File(root + "/search.html"));
            IOUtils.copy(inputStream, out);
            inputStream.close();
            out.close();

        } catch (Exception e) {
            log.error("toHtmlSearch failed", e);
        }
    }

    private String getTemplatePath(KbaseEntity kbase, String templateName) {
        String theme = (kbase != null && kbase.getTheme() != null && !kbase.getTheme().isBlank())
                ? kbase.getTheme()
                : (kbaseProperties.resolveBlogTheme() != null && !kbaseProperties.resolveBlogTheme().isBlank()
                        ? kbaseProperties.resolveBlogTheme()
                        : "default");
        return "/blog/themes/" + theme + "/" + templateName;
    }

    private String getBlogHtmlRoot(String kbUid) {
        return kbaseProperties.resolveBlogHtmlRootDir() + kbUid;
    }

    private void ensureDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            boolean ok = file.mkdirs();
            if (!ok) {
                log.warn("mkdirs failed: {}", dirPath);
            }
        }
    }
}
