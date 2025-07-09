/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-19 17:02:05
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-09 23:01:40
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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/file")
@Tag(name = "File Preview and Download", description = "File preview and download APIs for displaying and downloading uploaded files")
public class UploadFilePreview {

	private final UploadRestService uploadService;

	/**
	 * 浏览器预览文件，或放到 <img src> 标签中在线展示
	 * http://127.0.0.1:9003/file/2024/09/16/20240916144702_身份证-背面.jpg
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	@Operation(summary = "Preview File", description = "Preview file in browser or display in img tag")
	@GetMapping("/{yyyy}/{MM}/{dd}/{filename:.+}")
	@ResponseBody
	public void preview(
			@PathVariable(name = "yyyy") String year,
			@PathVariable(name = "MM") String month,
			@PathVariable(name = "dd") String day,
			@PathVariable String filename,
			HttpServletResponse response) throws IOException {
		log.info("year {}, month {}, day {}, filename: {}", year, month, day, filename);
		Resource fileResource = uploadService.loadAsResource(filename);

		// 文件预览
		File file = fileResource.getFile();
		FileInputStream fileInputStream = new FileInputStream(file);
		// 清空response
		response.reset();
		// 2、设置文件下载方式
		response.setCharacterEncoding("utf-8");
		// response.setContentType("application/pdf");
		OutputStream outputStream = response.getOutputStream();
		int count = 0;
		byte[] buffer = new byte[1024 * 1024];
		while ((count = fileInputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, count);
		}
		outputStream.flush();
		outputStream.close();
		fileInputStream.close();
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
		Resource fileResource = uploadService.loadAsResource(filename);
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

	/////////////// 旧版本

	// http://127.0.0.1:9003/file/download/20240319162820_img-service2.png
	// @Deprecated
	// @GetMapping("/download/{filename:.+}")
	// @ResponseBody
	// public ResponseEntity<Resource> downloadOld(@PathVariable String filename) throws UnsupportedEncodingException {
	// 	Resource fileResource = uploadService.loadAsResource(filename);

	// 	if (fileResource == null) {
	// 		return ResponseEntity.notFound().build();
	// 	}

	// 	// 对文件名进行URL编码，以确保中文字符能够正确传输
	// 	String encodedFilename = URLEncoder.encode(fileResource.getFilename(), "UTF-8").replace("+", "%20");

	// 	// 设置HTTP响应头，包含经过编码的文件名
	// 	HttpHeaders headers = new HttpHeaders();
	// 	headers.setContentDispositionFormData("attachment", encodedFilename);

	// 	return ResponseEntity.ok().headers(headers).body(fileResource);
	// }

	// // http://127.0.0.1:9003/file/20240319162820_img-service2.png
	// @Deprecated
	// @GetMapping("/{filename:.+}")
	// @ResponseBody
	// public void previewOld(
	// 		@PathVariable String filename,
	// 		HttpServletResponse response) throws IOException {
	// 	log.info("filename: {}", filename);

	// 	Resource fileResource = uploadService.loadAsResourceOld(filename);

	// 	// 文件预览
	// 	File file = fileResource.getFile();
	// 	FileInputStream fileInputStream = new FileInputStream(file);
	// 	// 清空response
	// 	response.reset();
	// 	// 2、设置文件下载方式
	// 	response.setCharacterEncoding("utf-8");
	// 	// response.setContentType("application/pdf");
	// 	OutputStream outputStream = response.getOutputStream();
	// 	int count = 0;
	// 	byte[] buffer = new byte[1024 * 1024];
	// 	while ((count = fileInputStream.read(buffer)) != -1) {
	// 		outputStream.write(buffer, 0, count);
	// 	}
	// 	outputStream.flush();
	// 	outputStream.close();
	// 	fileInputStream.close();
	// }

}
