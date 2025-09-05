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
@Tag(name = "网站管理", description = "网站管理相关接口")
public class WebsiteRestController extends BaseRestController<WebsiteRequest, WebsiteRestService> {

    private final WebsiteRestService websiteRestService;

    // @PreAuthorize(RolePermissions.ROLE_ADMIN)
    @Operation(summary = "根据组织查询网站", description = "查询组织的网站列表")
    @Override
    public ResponseEntity<?> queryByOrg(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteRestService.queryByOrg(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @Operation(summary = "根据用户查询网站", description = "查询用户的网站列表")
    @Override
    public ResponseEntity<?> queryByUser(WebsiteRequest request) {
        
        Page<WebsiteResponse> websites = websiteRestService.queryByUser(request);

        return ResponseEntity.ok(JsonResult.success(websites));
    }

    @ActionAnnotation(title = "知识库网站", action = "新建", description = "create website")
    @Operation(summary = "创建网站", description = "创建新的网站")
    @Override
    public ResponseEntity<?> create(WebsiteRequest request) {
        
        WebsiteResponse website = websiteRestService.create(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = "知识库网站", action = "更新", description = "update website")
    @Operation(summary = "更新网站", description = "更新现有的网站")
    @Override
    public ResponseEntity<?> update(WebsiteRequest request) {
        
        WebsiteResponse website = websiteRestService.update(request);

        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = "知识库网站", action = "删除", description = "delete website")
    @Operation(summary = "删除网站", description = "删除指定的网站")
    @Override
    public ResponseEntity<?> delete(WebsiteRequest request) {
        
        websiteRestService.delete(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // deleteAll
    @PostMapping("/deleteAll")
    @Operation(summary = "删除所有网站", description = "删除所有网站")
    public ResponseEntity<?> deleteAll(@RequestBody WebsiteRequest request) {

        websiteRestService.deleteAll(request);

        return ResponseEntity.ok(JsonResult.success());
    }

    // enable/disable website
    @PostMapping("/enable")
    @Operation(summary = "启用/禁用网站", description = "启用或禁用网站")
    public ResponseEntity<?> enable(@RequestBody WebsiteRequest request) {

        WebsiteResponse website = websiteRestService.enable(request);
        
        return ResponseEntity.ok(JsonResult.success(website));
    }

    @ActionAnnotation(title = "知识库网站", action = "导出", description = "export website")
    @Operation(summary = "导出网站", description = "导出网站数据")
    @Override
    public Object export(WebsiteRequest request, HttpServletResponse response) {
        return exportTemplate(
            request,
            response,
            websiteRestService,
            WebsiteExcel.class,
            "知识库网站",
            "website"
        );
    }

    @Operation(summary = "根据UID查询网站", description = "通过UID查询具体的网站")
    @Override
    public ResponseEntity<?> queryByUid(WebsiteRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUid'");
    }
    
    // ==================== 网站抓取相关API ====================
    
    @PostMapping("/crawl/start")
    @Operation(summary = "开始整站抓取", description = "使用指定配置开始整站抓取")
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
    @Operation(summary = "快速抓取", description = "使用快速配置开始抓取（较少页面和深度）")
    public ResponseEntity<?> startFastCrawl(@RequestBody WebsiteCrawlRequest request) {
        try {
            websiteRestService.startFastCrawl(request.getWebsiteUid());
            return ResponseEntity.ok(JsonResult.success("快速抓取任务已启动"));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @PostMapping("/crawl/start/deep")
    @Operation(summary = "深度抓取", description = "使用深度配置开始抓取（更多页面和深度）")
    public ResponseEntity<?> startDeepCrawl(@RequestBody WebsiteCrawlRequest request) {
        try {
            websiteRestService.startDeepCrawl(request.getWebsiteUid());
            return ResponseEntity.ok(JsonResult.success("深度抓取任务已启动"));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @PostMapping("/crawl/stop")
    @Operation(summary = "停止抓取", description = "停止正在运行的抓取任务")
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
    @Operation(summary = "获取抓取任务列表", description = "获取指定网站的所有抓取任务")
    public ResponseEntity<?> getCrawlTasks(@PathVariable String websiteUid) {
        try {
            var tasks = websiteRestService.getCrawlTasks(websiteUid);
            return ResponseEntity.ok(JsonResult.success(tasks));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @GetMapping("/crawl/task/status/{taskId}")
    @Operation(summary = "获取抓取任务状态", description = "获取指定任务的实时状态")
    public ResponseEntity<?> getCrawlTaskStatus(@PathVariable String taskId) {
        try {
            WebsiteCrawlTask task = websiteRestService.getCrawlTaskStatus(taskId);
            return ResponseEntity.ok(JsonResult.success(task));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @GetMapping("/crawl/sitemap/{websiteUid}")
    @Operation(summary = "解析站点地图", description = "解析网站的sitemap.xml获取URL列表")
    public ResponseEntity<?> parseSitemap(@PathVariable String websiteUid) {
        try {
            var urls = websiteRestService.parseSitemap(websiteUid);
            return ResponseEntity.ok(JsonResult.success(urls));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
    @PostMapping("/crawl/config")
    @Operation(summary = "更新抓取配置", description = "更新网站的抓取配置")
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
    @Operation(summary = "获取抓取配置", description = "获取网站的抓取配置")
    public ResponseEntity<?> getCrawlConfig(@PathVariable String websiteUid) {
        try {
            WebsiteCrawlConfig config = websiteRestService.getCrawlConfig(websiteUid);
            return ResponseEntity.ok(JsonResult.success(config));
        } catch (Exception e) {
            return ResponseEntity.ok(JsonResult.error(e.getMessage()));
        }
    }
    
}