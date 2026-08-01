/**
 * upload package for upload record persistence, multi-backend storage integration, watermark processing, and upload access control.
 * upload 包，负责上传记录持久化、多后端存储集成、水印处理与上传访问控制。
 *
 * <p>The package combines upload entities, request and response models, repository and specification queries,
 * admin and visitor upload endpoints, security configuration, preview and watermark services, lifecycle listeners,
 * and specialized subpackages for aliyun, minio, tencent, storage, and watermark integrations.
 * 该包组合了上传实体、请求响应模型、仓库与 Specification 查询、管理端与访客端上传接口、安全配置、预览与水印服务、生命周期监听器，
 * 以及 aliyun、minio、tencent、storage、watermark 等专门子包。
 *
 * @author bytedesk.com
 */
@NonNullApi
package com.bytedesk.core.upload;

import org.springframework.lang.NonNullApi;