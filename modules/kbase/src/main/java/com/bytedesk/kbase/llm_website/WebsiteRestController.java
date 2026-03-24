/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-18 10:12:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_website;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.base.BaseRestController;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.utils.JsonResult;
import com.bytedesk.kbase.llm_website.crawl.WebsiteCrawlConfig;
import com.bytedesk.kbase.llm_website.crawl.WebsiteCrawlTask;
import com.bytedesk.core.annotation.ActionAnnotation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/llm/website")
@AllArgsConstructor
@Tag(name = "Website Management", description = "Website management APIs")
public class WebsiteRestController extends BaseRestController<WebsiteRequest, WebsiteRestService> {

    private final WebsiteRestService websiteRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "Query Websites by Organization", description = "Query the list of websites for the organization")
    @GetMapping("/query/org")
    @Override
    public ResponseEntity<?> queryByOrg(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @Operation(summary = "Query Websites by User", description = "Query the list of websites for the user")
    @GetMapping({ "/query", "/query/user" })
    @Override
    public ResponseEntity<?> queryByUser(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBSITE, action = I18Consts.I18N_ACTION_CREATE, description = "create website")
    @Operation(summary = "Create Website", description = "Create a new website")
    @PostMapping("/create")
    @Override
    public ResponseEntity<?> create(@RequestBody WebsiteRequest request) {
        
        WebsiteResponse website = websiteRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBSITE, action = I18Consts.I18N_ACTION_UPDATE, description = "update website")
    @Operation(summary = "Update Website", description = "Update the existing website")
    @PostMapping("/update")
    @Override
    public ResponseEntity<?> update(@RequestBody WebsiteRequest request) {
        
        WebsiteResponse website = websiteRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBSITE, action = I18Consts.I18N_ACTION_DELETE, description = "delete website")
    @Operation(summary = "Delete Website", description = "Delete the specified website")
    @PostMapping("/delete")
    @Override
    public ResponseEntity<?> delete(@RequestBody WebsiteRequest request) {
        
        websiteRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PostMapping("/deleteAll")
    @Operation(summary = "Delete All Websites", description = "Delete all websites")
    public ResponseEntity<?> deleteAll(@RequestBody WebsiteRequest request) {

        websiteRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable website
    @PostMapping("/enable")
    @Operation(summary = "Enable or Disable Website", description = "Enable or disable the website")
    public ResponseEntity<?> enable(@RequestBody WebsiteRequest request) {

        WebsiteResponse website = websiteRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = I18Consts.I18N_WEBSITE, action = I18Consts.I18N_ACTION_EXPORT, description = "export website")
    @Operation(summary = "Export Websites", description = "Export website data")
    @Override
    @GetMapping("/export")
    public Object export(WebsiteRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            websiteRestService,
            WebsiteExcel.class,
            "Knowledge Base Website",
            "website"
        );
    }

    @Operation(summary = "Query Website by UID", description = "Query the specific website by UID")
    @Override
    public ResponseEntity<?> queryByUid(WebsiteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
    // ==================== 网站抓取相关API ====================
    
    @PostMapping("/crawl/start")
    @Operation(summary = "Start Full-Site Crawl", description = "Start a full-site crawl using the specified configuration")
    public ResponseEntity<?> startCrawl(@RequestBody WebsiteCrawlRequest request) {
        try {
            WebsiteCrawlConfig config = request.getConfig() != null ? request.getConfig() : WebsiteCrawlConfig.getDefault();
            websiteRestService.startCrawl(request.getWebsiteUid(), config);
            return ResponseEntity.ok(JsonResult.success("抓取任务已启动"));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @PostMapping("/crawl/start/fast")
    @Operation(summary = "Start Fast Crawl", description = "Start crawling with a fast configuration using fewer pages and lower depth")
    public ResponseEntity<?> startFastCrawl(@RequestBody WebsiteCrawlRequest request) {
        try {
            websiteRestService.startFastCrawl(request.getWebsiteUid());
            return ResponseEntity.ok(JsonResult.success("快速抓取任务已启动"));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @PostMapping("/crawl/start/deep")
    @Operation(summary = "Start Deep Crawl", description = "Start crawling with a deep configuration using more pages and greater depth")
    public ResponseEntity<?> startDeepCrawl(@RequestBody WebsiteCrawlRequest request) {
        try {
            websiteRestService.startDeepCrawl(request.getWebsiteUid());
            return ResponseEntity.ok(JsonResult.success("深度抓取任务已启动"));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @PostMapping("/crawl/stop")
    @Operation(summary = "Stop Crawl", description = "Stop the running crawl task")
    public ResponseEntity<?> stopCrawl(@RequestBody WebsiteCrawlRequest request) {
        try {
            boolean stopped = websiteRestService.stopCrawl(request.getWebsiteUid());
            if (stopped) {
                return ResponseEntity.ok(JsonResult.success("抓取任务已停止"));
            } else {
                return ResponseEntity.ok(JsonResult.error("没有正在运行的抓取任务"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @GetMapping("/crawl/tasks/{websiteUid}")
    @Operation(summary = "Get Crawl Task List", description = "Get all crawl tasks for the specified website")
    public ResponseEntity<?> getCrawlTasks(@PathVariable String websiteUid) {
        try {
            var tasks = websiteRestService.getCrawlTasks(websiteUid);
            return ResponseEntity.ok(JsonResult.success(tasks));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @GetMapping("/crawl/task/status/{taskId}")
    @Operation(summary = "Get Crawl Task Status", description = "Get the real-time status of the specified task")
    public ResponseEntity<?> getCrawlTaskStatus(@PathVariable String taskId) {
        try {
            WebsiteCrawlTask task = websiteRestService.getCrawlTaskStatus(taskId);
            return ResponseEntity.ok(JsonResult.success(task));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @GetMapping("/crawl/sitemap/{websiteUid}")
    @Operation(summary = "Parse Sitemap", description = "Parse the website sitemap.xml and retrieve the URL list")
    public ResponseEntity<?> parseSitemap(@PathVariable String websiteUid) {
        try {
            var urls = websiteRestService.parseSitemap(websiteUid);
            return ResponseEntity.ok(JsonResult.success(urls));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @PostMapping("/crawl/config")
    @Operation(summary = "Update Crawl Configuration", description = "Update the crawl configuration for the website")
    public ResponseEntity<?> updateCrawlConfig(@RequestBody WebsiteCrawlConfigRequest request) {
        try {
            WebsiteResponse response = websiteRestService.updateCrawlConfig(
                request.getWebsiteUid(), request.getConfig());
            return ResponseEntity.ok(JsonResult.success(response));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @GetMapping("/crawl/config/{websiteUid}")
    @Operation(summary = "Get Crawl Configuration", description = "Get the crawl configuration for the website")
    public ResponseEntity<?> getCrawlConfig(@PathVariable String websiteUid) {
        try {
            WebsiteCrawlConfig config = websiteRestService.getCrawlConfig(websiteUid);
            return ResponseEntity.ok(JsonResult.success(config));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
}