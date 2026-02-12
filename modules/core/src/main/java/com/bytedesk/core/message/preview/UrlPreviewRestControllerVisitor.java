package com.bytedesk.core.message.preview;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/visitor/api/v1/url")
@Tag(name = "URL预览", description = "解析网页 meta 信息用于链接预览")
@Description("URL Preview Controller (Visitor) - public endpoint for link preview")
public class UrlPreviewRestControllerVisitor {

    private final UrlPreviewService urlPreviewService;

    @Operation(summary = "解析URL预览信息", description = "抓取网页并解析 OpenGraph / title / description / image 等")
    @GetMapping("/preview")
    public ResponseEntity<?> preview(@RequestParam("url") String url) {
        try {
            UrlPreviewResponse resp = urlPreviewService.preview(url);
            return ResponseEntity.ok(JsonResult.success(resp));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }
    }
}
