/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-24
 * @Description: Phone number lookup APIs
 */
package com.bytedesk.core.phone;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.annotation.ApiRateLimiter;
import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "手机号归属地查询", description = "对外手机号归属地/运营商查询接口")
@RestController
@AllArgsConstructor
@RequestMapping("/test/api/v1/phone")
public class PhoneLookupRestController {

    private final PhoneLookupService phoneLookupService;

    // http://127.0.0.1:9003/test/api/v1/phone/lookup?number=13800138000
    @ApiRateLimiter(value = 5, timeout = 1)
    @GetMapping("/lookup")
    @Operation(summary = "手机号归属地查询", description = "输入 11 位手机号或前 7 位号段，返回归属地与运营商信息")
    public ResponseEntity<?> lookup(@RequestParam("number") String number) {
        if (number == null || number.isBlank()) {
            return ResponseEntity.badRequest().body(JsonResult.error("number is required", 400));
        }
        String trimmed = number.trim();
        if (!trimmed.matches("\\d{7,11}")) {
            return ResponseEntity.badRequest().body(JsonResult.error("number must be 7-11 digits", 400));
        }

        Optional<PhoneNumberInfo> found = phoneLookupService.lookup(trimmed);
        if (found.isEmpty()) {
            return ResponseEntity.status(404).body(JsonResult.error("not found", 404));
        }

        return ResponseEntity.ok(JsonResult.success(PhoneLookupResponse.from(found.get())));
    }
}
