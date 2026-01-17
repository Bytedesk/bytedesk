package com.bytedesk.service.form_result;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bytedesk.core.utils.JsonResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/visitor/api/v1/form/result")
@AllArgsConstructor
public class FormResultRestControllerVisitor {

	private final FormResultRestService formResultRestService;

	@Operation(summary = "访客提交表单结果", description = "未登录访客提交表单结果")
	@ApiResponse(responseCode = "200", description = "提交成功",
		content = @Content(mediaType = "application/json",
		schema = @Schema(implementation = FormResultResponse.class)))
	@PostMapping("/create")
	public ResponseEntity<?> create(@RequestBody FormResultRequest request) {
		if (!StringUtils.hasText(request.getOrgUid())) {
			throw new IllegalArgumentException("orgUid is required");
		}
		if (!StringUtils.hasText(request.getFormUid())) {
			throw new IllegalArgumentException("formUid is required");
		}
		FormResultResponse result = formResultRestService.create(request);
		return ResponseEntity.ok(JsonResult.success(result));
	}
}
