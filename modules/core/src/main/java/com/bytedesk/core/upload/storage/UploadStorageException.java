/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-03-18 11:44:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.storage;

public class UploadStorageException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * 应用层错误码（用于 JsonResult.code），不等同于 HTTP 状态码。
	 * 约定：
	 * - 400 参数/请求错误
	 * - 404 资源不存在（如本地文件不存在）
	 * - 413 文件过大
	 * - 415 不支持的媒体类型
	 * - 422 内容校验失败（如伪造图片）
	 * - 503 存储服务不可用
	 * - 500 服务端内部错误
	 */
	private final Integer code;

	public UploadStorageException(String message) {
		this(message, 400, null);
	}

	public UploadStorageException(String message, Throwable cause) {
		this(message, 400, cause);
	}

	public UploadStorageException(String message, Integer code) {
		this(message, code, null);
	}

	public UploadStorageException(String message, Integer code, Throwable cause) {
		super(message, cause);
		this.code = code == null ? 400 : code;
	}

	public Integer getCode() {
		return code;
	}
}
