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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.article.ArticleResponse;
import com.bytedesk.kbase.translation.KbaseTranslationEntity;
import com.bytedesk.kbase.translation.KbaseTranslationRepository;
import com.bytedesk.kbase.translation.KbaseTranslationSourceTypeEnum;
import com.bytedesk.kbase.translation.KbaseTranslationStatusEnum;
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

    private final KbaseRestService kbaseRestService;

    private final KbaseTranslationRepository kbaseTranslationRepository;

    // 更新整个知识库
    public void updateKbase(KbaseEntity kbase) {
        // 静态化首页
        Page<CategoryResponse> categoriesPage = kbaseRestService.getCategories(kbase);
        Page<ArticleResponse> articlesPage = kbaseRestService.getArticles(kbase);
        //
        toHtmlKb(kbase, categoriesPage.getContent(), articlesPage, articlesPage, articlesPage);
        toHtmlSearch(kbase);
        // 遍历categoriesPage
        for (CategoryResponse category : categoriesPage.getContent()) {
            Page<ArticleResponse> articlesCategoryPage = kbaseRestService.getArticlesByCategory(kbase,
                    category.getUid());
            toHtmlCategory(kbase, category, categoriesPage.getContent(), articlesCategoryPage.getContent());
        }
        // 遍历articlesPage
        for (ArticleResponse article : articlesPage.getContent()) {
            toHtmlArticle(kbase, article, categoriesPage.getContent(), new ArrayList<>());
            toHtmlTranslatedArticles(kbase, article, categoriesPage.getContent(), new ArrayList<>());
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
            String saveHtmlPathWithKbUid = kbaseProperties.resolveHelpcenterHtmlPath() + "/" + kbase.getUid();
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
            log.error("Unhandled exception", e);
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
            String saveHtmlPathWithKbUid = kbaseProperties.resolveHelpcenterHtmlPath() + "/" + kbase.getUid() + "/category";
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
            log.error("Unhandled exception", e);
        }
    }

    // 生成知识库文章页
    public void toHtmlArticle(KbaseEntity kbase,
            ArticleResponse article,
            List<CategoryResponse> categories,
            List<ArticleResponse> related) {
        toHtmlArticle(kbase, article, categories, related, null);
        }

        public void toHtmlArticle(KbaseEntity kbase,
            ArticleResponse article,
            List<CategoryResponse> categories,
            List<ArticleResponse> related,
            String languageDirectory) {
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
            String currentLanguage = resolveCurrentLanguage(kbase, languageDirectory);
            map.put("currentLanguage", currentLanguage);
            map.put("languageOptions", buildArticleLanguageOptions(kbase, article.getUid(), currentLanguage));
            // 静态化页面内容
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, "UTF-8");
            //
            String saveHtmlPathWithKbUid = resolveArticleHtmlDirectory(kbase.getUid(), languageDirectory);
            // log.info("toHtmlArticle saveHtmlPathWithKbUid {}", saveHtmlPathWithKbUid);
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
            log.error("Unhandled exception", e);
        }
    }

    private void toHtmlTranslatedArticles(KbaseEntity kbase,
            ArticleResponse article,
            List<CategoryResponse> categories,
            List<ArticleResponse> related) {
        List<KbaseTranslationEntity> translations = kbaseTranslationRepository
                .findByKbase_UidAndSourceUidAndSourceTypeAndDeletedFalse(
                        kbase.getUid(),
                        article.getUid(),
                        KbaseTranslationSourceTypeEnum.ARTICLE.name())
                .stream()
                .filter(translation -> Boolean.TRUE.equals(translation.getEnabled()))
                .filter(translation -> KbaseTranslationStatusEnum.SUCCESS.name().equals(translation.getTranslateStatus()))
                .filter(translation -> StringUtils.hasText(translation.getTargetLanguage()))
                .toList();

        for (KbaseTranslationEntity translation : translations) {
            ArticleResponse translatedArticle = ArticleResponse.builder()
                    .title(StringUtils.hasText(translation.getTitle()) ? translation.getTitle() : article.getTitle())
                    .summary(StringUtils.hasText(translation.getSummary()) ? translation.getSummary() : article.getSummary())
                    .contentHtml(StringUtils.hasText(translation.getContentHtml()) ? translation.getContentHtml() : article.getContentHtml())
                    .contentMarkdown(StringUtils.hasText(translation.getContentMarkdown()) ? translation.getContentMarkdown() : article.getContentMarkdown())
                    .coverImageUrl(article.getCoverImageUrl())
                    .type(article.getType())
                    .tagList(translation.getTagList() != null && !translation.getTagList().isEmpty() ? translation.getTagList() : article.getTagList())
                    .top(article.getTop())
                    .published(article.getPublished())
                    .readCount(article.getReadCount())
                    .likeCount(article.getLikeCount())
                    .editor(article.getEditor())
                    .needAudit(article.getNeedAudit())
                    .auditStatus(article.getAuditStatus())
                    .auditOpinion(article.getAuditOpinion())
                    .auditUser(article.getAuditUser())
                    .categoryUid(article.getCategoryUid())
                    .kbUid(article.getKbUid())
                    .user(article.getUser())
                    .elasticStatus(article.getElasticStatus())
                    .vectorStatus(article.getVectorStatus())
                    .build();
            translatedArticle.setUid(article.getUid());
                    translatedArticle.setCreatedAt(article.getCreatedAtRaw());
                    translatedArticle.setUpdatedAt(article.getUpdatedAtRaw());
            translatedArticle.setOrgUid(article.getOrgUid());
            translatedArticle.setUserUid(article.getUserUid());
            toHtmlArticle(kbase, translatedArticle, categories, related, translation.getTargetLanguage());
        }
    }

    private String resolveArticleHtmlDirectory(String kbUid, String languageDirectory) {
        String basePath = kbaseProperties.resolveHelpcenterHtmlPath() + "/" + kbUid;
        if (StringUtils.hasText(languageDirectory)) {
            return basePath + "/" + languageDirectory.trim().toUpperCase() + "/article";
        }
        return basePath + "/article";
    }

    private String resolveCurrentLanguage(KbaseEntity kbase, String languageDirectory) {
        if (StringUtils.hasText(languageDirectory)) {
            return normalizeLanguage(languageDirectory);
        }
        if (StringUtils.hasText(kbase.getSourceLanguage())) {
            return normalizeLanguage(kbase.getSourceLanguage());
        }
        return LanguageEnum.ZH_CN.name();
    }

    private List<Map<String, String>> buildArticleLanguageOptions(KbaseEntity kbase, String articleUid, String currentLanguage) {
        List<Map<String, String>> options = new ArrayList<>();
        String sourceLanguage = resolveCurrentLanguage(kbase, null);
        options.add(createLanguageOption(sourceLanguage, buildArticleLanguageUrl(kbase.getUid(), articleUid, null), currentLanguage));

        List<KbaseTranslationEntity> translations = kbaseTranslationRepository
                .findByKbase_UidAndSourceUidAndSourceTypeAndDeletedFalse(
                        kbase.getUid(),
                        articleUid,
                        KbaseTranslationSourceTypeEnum.ARTICLE.name())
                .stream()
                .filter(translation -> Boolean.TRUE.equals(translation.getEnabled()))
                .filter(translation -> KbaseTranslationStatusEnum.SUCCESS.name().equals(translation.getTranslateStatus()))
                .filter(translation -> StringUtils.hasText(translation.getTargetLanguage()))
                .toList();

        for (KbaseTranslationEntity translation : translations) {
            String targetLanguage = normalizeLanguage(translation.getTargetLanguage());
            boolean exists = options.stream().anyMatch(item -> targetLanguage.equals(item.get("code")));
            if (!exists) {
                options.add(createLanguageOption(
                        targetLanguage,
                        buildArticleLanguageUrl(kbase.getUid(), articleUid, targetLanguage),
                        currentLanguage));
            }
        }

        return options;
    }

    private Map<String, String> createLanguageOption(String code, String url, String currentLanguage) {
        Map<String, String> option = new LinkedHashMap<>();
        option.put("code", code);
        option.put("label", toLanguageLabel(code));
        option.put("url", url);
        option.put("active", String.valueOf(code.equals(currentLanguage)));
        return option;
    }

    private String normalizeLanguage(String language) {
        return LanguageEnum.fromValue(language).name();
    }

    private String buildArticleLanguageUrl(String kbUid, String articleUid, String language) {
        if (!StringUtils.hasText(language)) {
            return "/helpcenter/" + kbUid + "/article/" + articleUid + ".html";
        }
        return "/helpcenter/" + kbUid + "/" + normalizeLanguage(language) + "/article/" + articleUid + ".html";
    }

    private String toLanguageLabel(String code) {
        return switch (code) {
            case "ZH_CN" -> "简体中文";
            case "ZH_TW" -> "繁體中文";
            case "EN" -> "English";
            case "JA" -> "日本語";
            case "KO" -> "한국어";
            case "FR" -> "Français";
            case "DE" -> "Deutsch";
            case "ES" -> "Español";
            case "PT" -> "Português";
            case "RU" -> "Русский";
            default -> code;
        };
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
            map.put("apiHost", kbaseProperties.resolveHelpcenterApiUrl());
            map.put("kbase", kbase);
            // 静态化页面内容
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            InputStream inputStream = IOUtils.toInputStream(content, "UTF-8");
            //
            String saveHtmlPathWithKbUid = kbaseProperties.resolveHelpcenterHtmlPath() + "/" + kbase.getUid();
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
            log.error("Unhandled exception", e);
        }

    }

}
