/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-29 13:52:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-01 16:36:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bytedesk.core.annotation.ActionAnnotation;
import com.bytedesk.core.category.CategoryEntity;
import com.bytedesk.core.category.CategoryResponse;
import com.bytedesk.core.category.CategoryRestService;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.LanguageEnum;
import com.bytedesk.kbase.article.ArticleEntity;
import com.bytedesk.kbase.article.ArticleResponse;
import com.bytedesk.kbase.article.ArticleRestService;
import com.bytedesk.kbase.translation.KbaseTranslationEntity;
import com.bytedesk.kbase.translation.KbaseTranslationRepository;
import com.bytedesk.kbase.translation.KbaseTranslationSourceTypeEnum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/helpcenter")
@AllArgsConstructor
public class KbaseController {
    
    private final KbaseRestService kbaseRestService;

    private final CategoryRestService categoryRestService;

    private final ArticleRestService articleRestService;

    private final KbaseTranslationRepository kbaseTranslationRepository;

    private final KbaseProperties kbaseProperties;

	// kb/${currentKbase?.uid}
	// http://127.0.0.1:9003/helpcenter/${currentArticle?.uid}
    @ActionAnnotation(title = I18Consts.I18N_KBASE, action = I18Consts.I18N_ACTION_KB_INDEX, description = "show kbase")
	@GetMapping({"/{kbUid:[^\\.]*}", "/{kbUid:[^\\.]*}/"})
	public String kbIndex(@PathVariable String kbUid, Model model) {
		log.info("kbIndex path: {}", kbUid);

        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(kbUid);
        if (kbaseOptional.isPresent()) {
            log.info("kbase found: {}, {}", kbaseOptional.get().getName(), kbaseOptional.get().getHeadline());
            model.addAttribute("kbase", kbaseOptional.get());
            // 
            Page<CategoryResponse> categoriesPage = kbaseRestService.getCategories(kbaseOptional.get());
            model.addAttribute("categories", categoriesPage.getContent());
            // 
            Page<ArticleResponse> articlesPage = kbaseRestService.getArticles(kbaseOptional.get());
            // 
            model.addAttribute("articlesTop", articlesPage);
            model.addAttribute("articlesHot", articlesPage);
            model.addAttribute("articlesRecent", articlesPage);
            // 
            return "kbase/themes/" + kbaseOptional.get().getTheme() + "/index";
        }
        
        return "redirect:/404";
	}

    // http://127.0.0.1:9003/helpcenter/{kbUid}/category/${currentCategory?.uid}
    // http://127.0.0.1:9003/helpcenter/{kbUid}/category/${currentCategory?.uid}.html
    // kb/category/${currentCategory?.uid}
    @GetMapping("/{kbUid}/category/{categoryUid}")
	public String kbCategory(@PathVariable(value = "categoryUid") String categoryUid, Model model) {
        categoryUid = categoryUid.replaceAll(".html", "");
        log.info("kbCategory path: {}", categoryUid);
        return routeCategory(categoryUid, model);
	}

    // http://127.0.0.1:9003/helpcenter/{kbUid}/article/${currentArticle?.uid}
    // http://127.0.0.1:9003/helpcenter/{kbUid}/article/${currentArticle?.uid}.html
	// kb/article/${currentArticle?.uid}
	@GetMapping("/{kbUid}/article/{articleUid}")
    public String kbArticle(@PathVariable(value = "kbUid") String kbUid,
            @RequestParam(value = "lang", required = false) String lang,
            @PathVariable(value = "articleUid") String articleUid, Model model) {
        articleUid = articleUid.replaceAll(".html", "");
		log.info("kbArticle path: {}", articleUid);
        return routeArticle(kbUid, articleUid, lang, model);
	}

    @GetMapping("/{kbUid}/{lang}/article/{articleUid}")
    public String kbArticleWithLanguageDirectory(@PathVariable("kbUid") String kbUid,
            @PathVariable("lang") String lang,
            @PathVariable("articleUid") String articleUid,
            Model model) {
        articleUid = articleUid.replaceAll(".html", "");
        log.info("kbArticleWithLanguageDirectory path: {}, lang={}", articleUid, lang);
        return routeArticle(kbUid, articleUid, lang, model);
    }

    @GetMapping("/{kbUid}/search.html")
    public String kbSearch(@RequestParam("kbUid") String kbUid, @RequestParam("content") String content, Model model) {
        log.info("kbSearch path: {}", kbUid, content);
        //
        // model.addAttribute("kbUid", kbUid);
        // model.addAttribute("content", content);
        model.addAttribute("apiHost", kbaseProperties.resolveHelpcenterApiUrl());
        // 
        Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(kbUid);
        if (kbaseOptional.isPresent()) {
            model.addAttribute("kbase", kbaseOptional.get());
        }
        return "kbase/themes/" + kbaseOptional.get().getTheme() + "/search";
    }

    private String routeCategory(String categoryUid, Model model) {
        Optional<CategoryEntity> categoryOptional = categoryRestService.findByUid(categoryUid);
        if (categoryOptional.isPresent()) {
            model.addAttribute("category", categoryOptional.get());
            // 
            Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(categoryOptional.get().getKbUid());
            if (kbaseOptional.isPresent()) {
                model.addAttribute("kbase", kbaseOptional.get());
                // 
                Page<CategoryResponse> categoriesPage = kbaseRestService.getCategories(kbaseOptional.get());
                model.addAttribute("categories", categoriesPage.getContent());
                //
                Page<ArticleResponse> articlesPage = kbaseRestService.getArticlesByCategory(kbaseOptional.get(), categoryOptional.get().getUid());
                model.addAttribute("articles", articlesPage.getContent());

                return "kbase/themes/" + kbaseOptional.get().getTheme() + "/category";
            }
        }
        // error
		return "redirect:/404";
    }

    private String routeArticle(String kbUid, String articleUid, String lang, Model model) {
        Optional<ArticleEntity> articleOptional = articleRestService.findByUid(articleUid);
        if (articleOptional.isPresent()) {
            ArticleResponse articleResponse = articleRestService.convertToResponse(articleOptional.get());
            String resolvedKbUid = articleOptional.get().getKbase() != null
                    ? articleOptional.get().getKbase().getUid()
                    : kbUid;
            if (!StringUtils.hasText(resolvedKbUid)) {
                log.warn("routeArticle missing kbase relation and kbUid fallback, articleUid={}", articleUid);
                return "redirect:/404";
            }
            Optional<KbaseEntity> kbaseOptional = kbaseRestService.findByUid(resolvedKbUid);
            if (kbaseOptional.isPresent()) {
                String currentLanguage = resolveCurrentLanguage(kbaseOptional.get(), lang);
                model.addAttribute("currentLanguage", currentLanguage);
                model.addAttribute("languageOptions", buildArticleLanguageOptions(kbaseOptional.get(), articleUid, currentLanguage));
                if (StringUtils.hasText(lang)) {
                    Optional<KbaseTranslationEntity> translationOptional = kbaseTranslationRepository
                            .findByKbase_UidAndSourceUidAndSourceTypeAndTargetLanguageAndEnabledTrueAndDeletedFalse(
                                    resolvedKbUid,
                                    articleUid,
                                    KbaseTranslationSourceTypeEnum.ARTICLE.name(),
                                    lang.trim().toUpperCase());
                    if (translationOptional.isPresent()) {
                        KbaseTranslationEntity translation = translationOptional.get();
                        articleResponse.setTitle(StringUtils.hasText(translation.getTitle()) ? translation.getTitle() : articleResponse.getTitle());
                        articleResponse.setSummary(StringUtils.hasText(translation.getSummary()) ? translation.getSummary() : articleResponse.getSummary());
                        articleResponse.setContentHtml(StringUtils.hasText(translation.getContentHtml()) ? translation.getContentHtml() : articleResponse.getContentHtml());
                        articleResponse.setContentMarkdown(StringUtils.hasText(translation.getContentMarkdown()) ? translation.getContentMarkdown() : articleResponse.getContentMarkdown());
                        if (translation.getTagList() != null && !translation.getTagList().isEmpty()) {
                            articleResponse.setTagList(translation.getTagList());
                        }
                    }
                }
                model.addAttribute("article", articleResponse);
                model.addAttribute("kbase", kbaseOptional.get());
                // 
                Page<CategoryResponse> categoriesPage = kbaseRestService.getCategories(kbaseOptional.get());
                model.addAttribute("categories", categoriesPage.getContent());
                model.addAttribute("related", new ArrayList<>());
                // 
                return "kbase/themes/" + kbaseOptional.get().getTheme() + "/article";
            }
            log.warn("routeArticle unable to resolve kbase, articleUid={}, kbUid={}", articleUid, resolvedKbUid);
        }
        // error
		return "redirect:/404";
    }

    private String resolveCurrentLanguage(KbaseEntity kbase, String lang) {
        if (StringUtils.hasText(lang)) {
            return normalizeLanguage(lang);
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

    //////////////////////////////////////////////////////////////////

	// http://127.0.0.1:9003/helpcenter/edu
	@GetMapping("/edu")
	public String kbEduIndex() {
		return "kbase/themes/eduport/index";
	}

	// http://127.0.0.1:9003/helpcenter/edu/detail
	public String kbEduDetail() {
		return "kbase/themes/eduport/detail";
	}

	// http://127.0.0.1:9003/helpcenter/social
	@GetMapping("/social")
	public String kbSocialIndexS() {
		return "kbase/themes/social/index";
	}

	// http://127.0.0.1:9003/helpcenter/social/detail
	public String kbSocialDetail() {
		return "kbase/themes/social/detail";
	}

	// 
	// http://127.0.0.1:9003/helpcenter/default
	@GetMapping("/default")
	public String kbZdIndex() {
		return "kbase/themes/default/index";
	}

	// http://127.0.0.1:9003/helpcenter/default/article
	@GetMapping("/default/{path:[^\\.]*}")
	public String kbZdRedirect(@PathVariable String path) {
		log.info("kbZdRedirect path: {}", path);
		return "forward:/kbase/themes/default/" + path; // 默认路径
	}
}
