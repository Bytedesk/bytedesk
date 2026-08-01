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
package com.bytedesk.marketing.portal;

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
public class PortalStaticService {

    private final Configuration configuration;

    private final KbaseProperties kbaseProperties;

    private final KbaseRepository kbaseRepository;

    private final CategoryRepository categoryRepository;

    private final PortalRepository portalRepository;

    private final PortalRestService portalRestService;

    public void updatePortalKbase(String kbUid) {
        KbaseEntity kbase = kbaseRepository.findByUid(kbUid)
                .orElseThrow(() -> new RuntimeException("kbase not found: " + kbUid));
        List<CategoryEntity> categories = categoryRepository.findByKbUidAndDeletedFalse(kbUid);
        List<PortalResponse> portals = portalRepository.findByKbUidAndDeletedFalse(kbUid)
                .stream()
                .map(portalRestService::convertToResponse)
                .collect(Collectors.toList());

        toHtmlIndex(kbase, categories, portals);
        toHtmlSearch(kbase);
        for (CategoryEntity category : categories) {
            List<PortalResponse> categoryPortals = portalRepository
                    .findByKbUidAndCategoryUidAndDeletedFalse(kbUid, category.getUid())
                    .stream()
                    .map(portalRestService::convertToResponse)
                    .collect(Collectors.toList());
            toHtmlCategory(kbase, category, categories, categoryPortals);
        }
        for (PortalResponse portal : portals) {
            toHtmlPost(kbase, portal, categories);
        }
    }

    public void updatePortalPost(String portalUid) {
        PortalEntity entity = portalRepository.findByUid(portalUid)
                .orElseThrow(() -> new RuntimeException("portal not found: " + portalUid));
        if (!entity.isDeleted()) {
            toHtmlPost(entity.getKbUid(), portalRestService.convertToResponse(entity));
            // 同步更新 index/category/search
            updatePortalIndex(entity.getKbUid());
        }
    }

    public void updatePortalIndex(String kbUid) {
        KbaseEntity kbase = kbaseRepository.findByUid(kbUid)
                .orElseThrow(() -> new RuntimeException("kbase not found: " + kbUid));
        List<CategoryEntity> categories = categoryRepository.findByKbUidAndDeletedFalse(kbUid);
        List<PortalResponse> portals = portalRepository.findByKbUidAndDeletedFalse(kbUid)
                .stream()
                .map(portalRestService::convertToResponse)
                .collect(Collectors.toList());
        toHtmlIndex(kbase, categories, portals);
        toHtmlSearch(kbase);

        // 同步生成分类页，避免访问 /category/{uid}.html 返回 404
        for (CategoryEntity category : categories) {
            List<PortalResponse> categoryPortals = portalRepository
                .findByKbUidAndCategoryUidAndDeletedFalse(kbUid, category.getUid())
                .stream()
                .map(portalRestService::convertToResponse)
                .collect(Collectors.toList());
            toHtmlCategory(kbase, category, categories, categoryPortals);
        }
    }

    public void deletePortalPostStatic(String kbUid, String portalUid) {
        String root = getPortalHtmlRoot(kbUid);
        File file = new File(root + "/post/" + portalUid + ".html");
        if (file.exists() && file.isFile()) {
            boolean ok = file.delete();
            log.info("deletePortalPostStatic {} => {}", file.getAbsolutePath(), ok);
        }
        // 删除后也更新首页与分类页（避免仍然展示）
        updatePortalIndex(kbUid);
    }

    public void toHtmlPost(String kbUid, PortalResponse portal) {
        KbaseEntity kbase = kbaseRepository.findByUid(kbUid)
                .orElseThrow(() -> new RuntimeException("kbase not found: " + kbUid));
        List<CategoryEntity> categories = categoryRepository.findByKbUidAndDeletedFalse(kbUid);
        toHtmlPost(kbase, portal, categories);
    }

    // portal 首页
    public void toHtmlIndex(KbaseEntity kbase, List<CategoryEntity> categories, List<PortalResponse> portals) {
        try {
            Template template = configuration.getTemplate(getTemplatePath(kbase, "index.ftl"));
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);
            map.put("categories", categories);
            map.put("portals", portals);

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);

            String root = getPortalHtmlRoot(kbase.getUid());
            ensureDir(root);
            FileOutputStream out = new FileOutputStream(new File(root + "/index.html"));
            IOUtils.copy(inputStream, out);
            inputStream.close();
            out.close();

        } catch (Exception e) {
            log.error("toHtmlIndex failed", e);
        }
    }

    // portal 分类页
    public void toHtmlCategory(KbaseEntity kbase, CategoryEntity category, List<CategoryEntity> categories,
            List<PortalResponse> portals) {
        try {
            Template template = configuration.getTemplate(getTemplatePath(kbase, "category.ftl"));
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);
            map.put("category", category);
            map.put("categories", categories);
            map.put("portals", portals);

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);

            String root = getPortalHtmlRoot(kbase.getUid()) + "/category";
            ensureDir(root);
            FileOutputStream out = new FileOutputStream(new File(root + "/" + category.getUid() + ".html"));
            IOUtils.copy(inputStream, out);
            inputStream.close();
            out.close();

        } catch (Exception e) {
            log.error("toHtmlCategory failed", e);
        }
    }

    // portal 文章页
    public void toHtmlPost(KbaseEntity kbase, PortalResponse portal, List<CategoryEntity> categories) {
        try {
            Template template = configuration.getTemplate(getTemplatePath(kbase, "post.ftl"));
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);
            map.put("categories", categories);
            map.put("portal", portal);

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);

            String root = getPortalHtmlRoot(kbase.getUid()) + "/post";
            ensureDir(root);
            FileOutputStream out = new FileOutputStream(new File(root + "/" + portal.getUid() + ".html"));
            IOUtils.copy(inputStream, out);
            inputStream.close();
            out.close();

        } catch (Exception e) {
            log.error("toHtmlPost failed", e);
        }
    }

    // portal 搜索页（静态模板 + 前端自行调用 API 或在模板中做简单搜索）
    public void toHtmlSearch(KbaseEntity kbase) {
        try {
            Template template = configuration.getTemplate(getTemplatePath(kbase, "search.ftl"));
            Map<String, Object> map = new HashMap<>();
            map.put("kbase", kbase);

            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, StandardCharsets.UTF_8);

            String root = getPortalHtmlRoot(kbase.getUid());
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
                : (kbaseProperties.resolvePortalTheme() != null && !kbaseProperties.resolvePortalTheme().isBlank()
                        ? kbaseProperties.resolvePortalTheme()
                        : "default");
        return "/portal/themes/" + theme + "/" + templateName;
    }

    private String getPortalHtmlRoot(String kbUid) {
        return kbaseProperties.resolvePortalHtmlRootDir() + kbUid;
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
