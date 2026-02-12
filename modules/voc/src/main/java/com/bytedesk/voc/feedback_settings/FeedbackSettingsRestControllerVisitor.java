/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-02-10
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-02-10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 */
package com.bytedesk.voc.feedback_settings;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/feedback/settings")
@AllArgsConstructor
public class FeedbackSettingsRestControllerVisitor {

    private final FeedbackSettingsRestService feedbackSettingsRestService;

    /**
     * Fetch widget settings by uid.
     *
     * Note: this endpoint is intentionally lightweight for visitor widget usage.
     */
    @GetMapping
    public ResponseEntity<?> getByUid(
            @RequestParam(value = "uid", required = false) String uid,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "orgUid", required = false) String orgUid,
            @RequestParam(value = "type", required = false) String type) {

        // Preferred lookup: uid
        if (StringUtils.hasText(uid)) {
            Optional<FeedbackSettingsEntity> optional = feedbackSettingsRestService.findByUid(uid);
            if (optional.isPresent()) {
                return ResponseEntity.ok(JsonResult.success(feedbackSettingsRestService.convertToResponse(optional.get())));
            }
        }

        // Fallback: name + orgUid + type
        if (StringUtils.hasText(name) && StringUtils.hasText(orgUid) && StringUtils.hasText(type)) {
            Optional<FeedbackSettingsEntity> optional = feedbackSettingsRestService.findByNameAndOrgUidAndType(name, orgUid, type);
            if (optional.isPresent()) {
                return ResponseEntity.ok(JsonResult.success(feedbackSettingsRestService.convertToResponse(optional.get())));
            }
        }

        return ResponseEntity.ok(JsonResult.success(null));
    }
}
