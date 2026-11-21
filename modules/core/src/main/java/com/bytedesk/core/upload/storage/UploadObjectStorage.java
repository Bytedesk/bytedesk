/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-11-19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *   selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *   contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.storage;

import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.upload.UploadRequest;

/**
 * 对象存储扩展点（企业版实现，社区版无实现时回落本地磁盘存储）。
 * 
 * 由 enterprise/core 的 UploadObjectStorageService 提供 S3/MinIO/OSS/COS 等
 * 第三方对象存储能力；社区版不注册任何实现，文件仅存储在本地磁盘。
 */
public interface UploadObjectStorage {

	/**
	 * 当前是否启用对象存储路由（如 bytedesk.storage.provider=s3）。
	 * 返回 false 时，上传走本地磁盘存储。
	 */
	boolean isEnabled();

	/**
	 * 上传 multipart 文件到对象存储
	 * 
	 * @param file     multipart 文件
	 * @param fileName 文件名（已过滤）
	 * @param request  上传请求（用于选择存储目录），可为 null
	 * @return 文件访问 URL
	 */
	String upload(MultipartFile file, String fileName, UploadRequest request);

	/**
	 * 尝试从对象存储加载文件资源：fileUrl 命中对象存储直链前缀时，
	 * 经客户端鉴权下载到临时文件后返回资源。
	 * 未启用、URL 不匹配或下载失败时返回 null，回落到本地存储查找。
	 */
	Resource tryLoad(String fileUrl);

	/**
	 * 下载对象到本地文件（供分片合并等需要本地文件的场景使用）
	 */
	void download(String objectPath, Path targetFile);
}
