package com.bytedesk.core.message.preview;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRepository;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.message.content.TextContent;
import com.bytedesk.core.message.content.UrlPreview;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/url")
@Tag(name = "URL预览", description = "URL 预览写回缓存（仅客服端）")
@Description("URL Preview Controller (Agent) - authenticated write-back cache")
public class UrlPreviewRestControllerAgent {

    private final UrlPreviewService urlPreviewService;
    private final MessageRepository messageRepository;

    @Operation(summary = "写回URL预览到消息内容", description = "仅客服端调用；若已存在则不重复写入")
    @PostMapping("/preview/cache")
    public ResponseEntity<?> cachePreview(@RequestBody UrlPreviewWritebackRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(JsonResult.error("request required"));
        }
        if (!StringUtils.hasText(request.getMessageUid())) {
            return ResponseEntity.badRequest().body(JsonResult.error("messageUid required"));
        }
        if (!StringUtils.hasText(request.getUrl())) {
            return ResponseEntity.badRequest().body(JsonResult.error("url required"));
        }

        Optional<MessageEntity> optional = messageRepository.findByUid(request.getMessageUid().trim());
        if (optional.isEmpty()) {
            return ResponseEntity.ok(JsonResult.error("message not found"));
        }

        MessageEntity message = optional.get();
        if (!MessageTypeEnum.TEXT.name().equalsIgnoreCase(message.getType())) {
            return ResponseEntity.ok(JsonResult.error("only TEXT message supported"));
        }

        final String normalizedUrl;
        try {
            normalizedUrl = urlPreviewService.normalizeUrl(request.getUrl());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(JsonResult.error(e.getMessage()));
        }

        TextContent textContent = TextContent.fromJson(message.getContent());
        if (textContent == null) {
            textContent = TextContent.builder().text("").build();
        }

        List<UrlPreview> previews = textContent.getUrlPreviews();
        if (previews == null) {
            previews = new ArrayList<>();
        }

        if (containsUrl(previews, normalizedUrl)) {
            return ResponseEntity.ok(JsonResult.success(textContent));
        }

        UrlPreviewResponse previewResponse = null;
        boolean hasAnyPreviewField = StringUtils.hasText(request.getTitle())
                || StringUtils.hasText(request.getDescription())
                || StringUtils.hasText(request.getImageUrl())
                || StringUtils.hasText(request.getSiteName());
        if (!hasAnyPreviewField) {
            previewResponse = urlPreviewService.preview(normalizedUrl);
        }

        UrlPreview urlPreview = UrlPreview.builder()
                .url(normalizedUrl)
                .title(cap(hasAnyPreviewField ? request.getTitle() : previewResponse.getTitle(), 200))
                .description(cap(hasAnyPreviewField ? request.getDescription() : previewResponse.getDescription(), 500))
                .imageUrl(cap(hasAnyPreviewField ? request.getImageUrl() : previewResponse.getImageUrl(), 500))
                .siteName(cap(hasAnyPreviewField ? request.getSiteName() : previewResponse.getSiteName(), 100))
                .fetchedAt(ZonedDateTime.now().toString())
                .build();

        previews.add(urlPreview);
        textContent.setUrlPreviews(previews);
        message.setContent(textContent.toJson());
        messageRepository.save(message);

        return ResponseEntity.ok(JsonResult.success(textContent));
    }

    private boolean containsUrl(List<UrlPreview> previews, String normalizedUrl) {
        if (previews == null || !StringUtils.hasText(normalizedUrl)) {
            return false;
        }
        String needle = normalizedUrl.trim().toLowerCase(Locale.ROOT);
        return previews.stream().anyMatch(p -> {
            if (p == null || !StringUtils.hasText(p.getUrl())) {
                return false;
            }
            return needle.equals(p.getUrl().trim().toLowerCase(Locale.ROOT));
        });
    }

    private String cap(String s, int max) {
        if (!StringUtils.hasText(s)) {
            return null;
        }
        String t = s.replaceAll("\\s+", " ").trim();
        if (t.length() <= max) {
            return t;
        }
        return t.substring(0, max);
    }
}
