/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-19 17:02:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 10:33:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * file preview and download
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/file")
@Tag(name = "File Preview and Download", description = "File preview and download APIs for displaying and downloading uploaded files")
public class UploadFilePreview {

	private final UploadRestService uploadRestService;

	/**
	 * 浏览器预览文件，或放到 <img src&gt; 标签中在线展示
	 * http://127.0.0.1:9003/file/2024/09/16/20240916144702_身份证-背面.jpg
	 * 
	 * @param filename
	 * @param year 年份
	 * @param month 月份
	 * @param day 日期
	 * @param filename 文件名
	 * @param response HTTP响应对象
	 * @throws IOException IO异常
	 */
	@Operation(summary = "Preview File", description = "Preview file in browser or display in img tag")
	@GetMapping("/{yyyy}/{MM}/{dd}/{filename:.+}")
	@ResponseBody
	public void preview(
			@PathVariable(name = "yyyy") String year,
			@PathVariable(name = "MM") String month,
			@PathVariable(name = "dd") String day,
			@PathVariable String filename,
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		log.info("year {}, month {}, day {}, filename: {}", year, month, day, filename);
		// 拼接完整路径
		String fullPath = year + "/" + month + "/" + day + "/" + filename;
		Resource fileResource = uploadRestService.loadAsResource(fullPath);

		File file = fileResource.getFile();
		long fileLength = file.length();
		String contentType = resolveContentType(file, request);
		String rangeHeader = request.getHeader(HttpHeaders.RANGE);

		response.reset();
		response.setCharacterEncoding("utf-8");
		response.setContentType(contentType);
		response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
		response.setHeader("X-Content-Type-Options", "nosniff");

		long start = 0L;
		long end = fileLength - 1;

		if (rangeHeader != null && !rangeHeader.isBlank()) {
			try {
				List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
				if (!ranges.isEmpty()) {
					HttpRange range = ranges.get(0);
					start = range.getRangeStart(fileLength);
					end = range.getRangeEnd(fileLength);
					if (start >= fileLength || end >= fileLength || start > end) {
						response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
						response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
						return;
					}
					response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
					response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileLength);
				}
			} catch (IllegalArgumentException ex) {
				log.warn("invalid range header for file preview filename={} range={}", filename, rangeHeader);
				response.setStatus(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
				response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + fileLength);
				return;
			}
		}

		long contentLength = end - start + 1;
		response.setContentLengthLong(contentLength);

		try (InputStream inputStream = new FileInputStream(file); OutputStream outputStream = response.getOutputStream()) {
			skipFully(inputStream, start);
			copyRange(inputStream, outputStream, contentLength);
			outputStream.flush();
		}
	}

	private String resolveContentType(File file, HttpServletRequest request) {
		return Optional.ofNullable(request.getServletContext().getMimeType(file.getName()))
			.or(() -> MediaTypeFactory.getMediaType(file.getName()).map(MediaType::toString))
			.orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
	}

	private void skipFully(InputStream inputStream, long bytesToSkip) throws IOException {
		long remaining = bytesToSkip;
		while (remaining > 0) {
			long skipped = inputStream.skip(remaining);
			if (skipped <= 0) {
				if (inputStream.read() == -1) {
					break;
				}
				skipped = 1;
			}
			remaining -= skipped;
		}
	}

	private void copyRange(InputStream inputStream, OutputStream outputStream, long bytesToCopy) throws IOException {
		byte[] buffer = new byte[1024 * 1024];
		long remaining = bytesToCopy;
		while (remaining > 0) {
			int bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, remaining));
			if (bytesRead == -1) {
				break;
			}
			outputStream.write(buffer, 0, bytesRead);
			remaining -= bytesRead;
		}
	}

	/**
	 * 浏览器下载文件
	 * http://127.0.0.1:9003/file/download/2024/09/16/20240319162820_img-service2.png
	 * 
	 * @param filename
	 * @return
	 */
	@Deprecated
	@Operation(summary = "Download File", description = "Download file from server (deprecated)")
	@GetMapping("/download/{yyyy}/{MM}/{dd}/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> download(
			@PathVariable(name = "yyyy") String year,
			@PathVariable(name = "MM") String month,
			@PathVariable(name = "dd") String day,
			@PathVariable String filename) throws UnsupportedEncodingException {
		log.info("year {}, month {}, day {}, filename: {}", year, month, day, filename);
		// 拼接完整路径
		String fullPath = year + "/" + month + "/" + day + "/" + filename;
		Resource fileResource = uploadRestService.loadAsResource(fullPath);
		if (fileResource == null) {
			return ResponseEntity.notFound().build();
		}
		// 对文件名进行URL编码，以确保中文字符能够正确传输
		String encodedFilename = URLEncoder.encode(fileResource.getFilename(), "UTF-8").replace("+", "%20");
		// 设置HTTP响应头，包含经过编码的文件名
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", encodedFilename);

		return ResponseEntity.ok().headers(headers).body(fileResource);
	}


}
