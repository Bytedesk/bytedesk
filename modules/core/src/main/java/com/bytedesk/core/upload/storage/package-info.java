/**
 * 上传存储基础抽象：本地磁盘存储异常与对象存储扩展点。
 *
 * <p>对象存储（S3/MinIO/阿里云 OSS/腾讯云 COS 等）为企业版功能，实现在
 * enterprise/core 的 com.bytedesk.enterprise.core.upload 包；社区版无
 * {@link com.bytedesk.core.upload.storage.UploadObjectStorage} 实现，
 * 文件仅存储在本地磁盘。
 */
@NullMarked
package com.bytedesk.core.upload.storage;

import org.jspecify.annotations.NullMarked;
