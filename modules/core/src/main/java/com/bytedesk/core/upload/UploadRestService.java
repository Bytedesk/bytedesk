/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 14:47:30
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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
// import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.minio.UploadMinioService;
import com.bytedesk.core.upload.storage.UploadStorageException;
import com.bytedesk.core.upload.storage.UploadStorageFileNotFoundException;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// https://spring.io/guides/gs/uploading-files
@Slf4j
@Service
@RequiredArgsConstructor
public class UploadRestService extends BaseRestService<UploadEntity, UploadRequest, UploadResponse> {

	private final Path uploadDir;

	private final UidUtils uidUtils;

	private final ModelMapper modelMapper;

	private final UploadRepository uploadRepository;

	private final BytedeskProperties bytedeskProperties;

	private final AuthService authService;

	private final UploadWatermarkService uploadWatermarkService;

	@Autowired(required = false)
	private UploadMinioService uploadMinioService;

	@Override
	protected Specification<UploadEntity> createSpecification(UploadRequest request) {
		return UploadSpecification.search(request, authService);
	}

	@Override
	protected Page<UploadEntity> executePageQuery(Specification<UploadEntity> spec, Pageable pageable) {
		return uploadRepository.findAll(spec, pageable);
	}


	@Override
	public Optional<UploadEntity> findByUid(String uid) {
		return uploadRepository.findByUid(uid);
	}

	@Override
	public UploadResponse create(UploadRequest request) {
		UploadEntity upload = modelMapper.map(request, UploadEntity.class);
		upload.setUid(uidUtils.getUid());
		//
		UploadEntity savedUpload = save(upload);
		if (savedUpload == null) {
			throw new RuntimeException("Failed to store file " + upload.getFileName());
		}

		return convertToResponse(savedUpload);
	}

	@Override
	public UploadResponse update(UploadRequest request) {
		Optional<UploadEntity> uploadOptional = findByUid(request.getUid());
		if (uploadOptional.isPresent()) {
			UploadEntity upload = uploadOptional.get();
			modelMapper.map(request, upload);
			// 保存
			UploadEntity updatedUpload = save(upload);
			return convertToResponse(updatedUpload);
		} else {
			throw new RuntimeException("Upload with uid '" + request.getUid() + "' not found");
		}
	}

	@Override
	protected UploadEntity doSave(UploadEntity entity) {
		return uploadRepository.save(entity);
	}

	@Override
	public UploadEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, UploadEntity entity) {
		try {
			Optional<UploadEntity> latest = uploadRepository.findByUid(entity.getUid());
			if (latest.isPresent()) {
				UploadEntity latestEntity = latest.get();
				// 合并需要保留的数据
				latestEntity.setStatus(entity.getStatus());
				latestEntity.setFileName(entity.getFileName());
				latestEntity.setFileUrl(entity.getFileUrl());
				// 
				return uploadRepository.save(latestEntity);
			}
		} catch (Exception ex) {
			log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
			throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
		}
		return null;
	}

	// 给出图片URL地址，将其保存到本地
    public String storeFromUrl(String url, String fileName) {
		// fileName添加前缀: 20240916144702_, // 例如：20240916144702_身份证-背面.jpg
		fileName = BdDateUtils.formatDatetimeUid() + "_" + fileName;
		// storeFromUrl url: https://api.telegram.org/file/bot7968513107:AAHuHmMSQM_4CeO46bAr0FmLH5ryvzzJ2dQ/photos/file_2.jpg, fileName: 20250617172223_file_2.jpg
		log.info("storeFromUrl url: {}, fileName: {}", url, fileName);
        // 根据当前日期创建文件夹，格式如：2021/03/15
        String currentDateFolder = BdDateUtils.formatDateSlashNow();

        try {
            // 构建包含日期文件夹的文件路径
            Path dateFolderPath = this.uploadDir.resolve(currentDateFolder);
            Files.createDirectories(dateFolderPath); // 创建日期文件夹（如果不存在）

            Path destinationFile = dateFolderPath.resolve(fileName).normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(dateFolderPath.toAbsolutePath())) {
                // 这是一个安全检查
                throw new UploadStorageException("Cannot store file outside current directory.");
            }

            // 下载并保存图片
            URL imageUrl = new URL(url);
            try (InputStream inputStream = imageUrl.openStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // return destinationFile.toString();
			String uploadPath = currentDateFolder + "/" + fileName;
			String fileUrl = String.format("%s/file/%s", bytedeskProperties.getUploadUrl(), uploadPath);
			return fileUrl;
        } catch (IOException e) {
            throw new UploadStorageException("Failed to store file from URL.", e);
        }
    }

	public String store(MultipartFile file, String fileName) {
		return store(file, fileName, null);
	}

	public String store(MultipartFile file, String fileName, UploadRequest request) {
		// 根据当前日期创建文件夹，格式如：2021/03/15
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		String currentDateFolder = LocalDate.now().format(formatter);

		try {
			if (file.isEmpty()) {
				throw new UploadStorageException("Failed to store empty file.");
			}

			// 构建包含日期文件夹的文件路径
			Path dateFolderPath = this.uploadDir.resolve(currentDateFolder);
			Files.createDirectories(dateFolderPath); // 创建日期文件夹（如果不存在）

			Path destinationFile = dateFolderPath.resolve(fileName).normalize().toAbsolutePath();

			if (!destinationFile.getParent().equals(dateFolderPath.toAbsolutePath())) {
				// 这是一个安全检查
				throw new UploadStorageException("Cannot store file outside current directory.");
			}

			// 检查是否需要添加水印
			if (uploadWatermarkService.shouldAddWatermark(file, request)) {
				log.info("为图片添加水印: {}", fileName);
				uploadWatermarkService.addWatermarkToFile(file, destinationFile, request);
			} else {
				// 直接保存原文件
				try (InputStream inputStream = file.getInputStream()) {
					Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
				}
			}

			// 返回包含日期文件夹的文件名路径
			String uploadPath = currentDateFolder + "/" + fileName;
			String fileUrl = String.format("%s/file/%s", bytedeskProperties.getUploadUrl(), uploadPath);
			return fileUrl;

		} catch (IOException e) {
			throw new UploadStorageException("Failed to store file.", e);
		}
	}

	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.uploadDir, 1)
					.filter(path -> !path.equals(this.uploadDir))
					.map(this.uploadDir::relativize);
		} catch (IOException e) {
			throw new UploadStorageException("Failed to read stored files", e);
		}
	}

	public Resource loadAsResource(String filename) {
		// filename格式为：20240916144702_身份证-背面.jpg
		// 提取日期部分
		String dateString = filename.substring(0, 8);
		// 将日期字符串转换为路径格式
		String folderDatePart = dateString.substring(0, 4) + "/" + dateString.substring(4, 6) + "/"
				+ dateString.substring(6, 8);
		// 构建文件夹路径
		Path dateFolderPath = this.uploadDir.resolve(folderDatePart);

		// 创建日期文件夹（如果不存在）
		try {
			Files.createDirectories(dateFolderPath);
		} catch (IOException e) {
			// 处理异常
			e.printStackTrace();
		}

		// 构建完整的文件路径
		Path filePath = dateFolderPath.resolve(filename);

		try {
			if (Files.exists(filePath)) {
				Resource resource = new UrlResource(filePath.toUri());
				if (resource.exists() || resource.isReadable()) {
					return resource;
				} else {
					throw new UploadStorageFileNotFoundException(
							"Could not read file: " + filename);
				}
			} else {
				throw new UploadStorageFileNotFoundException(
						"File not found: " + filename);
			}
		} catch (MalformedURLException e) {
			throw new UploadStorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	public void initUploadDir() {
		try {
			Files.createDirectories(uploadDir);
		} catch (IOException e) {
			throw new UploadStorageException("Could not initialize storage", e);
		}
	}

	public void deleteFile(String filename) {
		Path filePath = uploadDir.resolve(filename);
		if (Files.exists(filePath)) {
			try {
				Files.delete(filePath);
			} catch (IOException e) {
				throw new RuntimeException("Failed to delete file: " + filename, e);
			}
		} else {
			throw new RuntimeException("File not found: " + filename);
		}
	}

	// 删除整个上传文件夹，危险操作，暂时注释掉
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(uploadDir.toFile());
	}

	@Override
	public void deleteByUid(String uid) {
		Optional<UploadEntity> uploadOptional = findByUid(uid);
		if (uploadOptional.isPresent()) {
			uploadOptional.get().setDeleted(true);
			save(uploadOptional.get());
		} else {
			throw new RuntimeException("Upload with uid '" + uid + "' not found");
		}
	}

	@Override
	public void delete(UploadRequest entity) {
		deleteByUid(entity.getUid());
	}

	@Override
	public UploadResponse convertToResponse(UploadEntity entity) {
		return ConvertUtils.convertToUploadResponse(entity);
	}

	public UploadResponse handleFileUpload(MultipartFile file, UploadRequest request) {
		log.info("handleFileUpload fileName: {}, fileType: {}, kbType {}, extra {}", 
		request.getFileName(), request.getFileType(), request.getKbType(), request.getExtra());

		UserEntity user = authService.getUser();
		UserProtobuf userProtobuf = null;
		if (user == null) {
			userProtobuf = UserProtobuf.builder()
				.uid(request.getVisitorUid())
                .nickname(request.getVisitorNickname())
                .avatar(request.getVisitorAvatar())
                .build();
		} else {
			userProtobuf = ConvertUtils.convertToUserProtobuf(user);
			request.setUserUid(user.getUid());
			request.setOrgUid(user.getOrgUid());
		}
		
		// 根据配置选择存储方式：优先使用 MinIO，否则使用本地文件系统
		String fileUrl;
		if (bytedeskProperties.getMinioEnabled()) {
			log.info("MinIO 已启用，使用 MinIO 存储文件");
			fileUrl = storeToMinio(file, request.getFileName(), request);
		} else {
			log.info("MinIO 未启用，使用本地文件系统存储文件");
			fileUrl = store(file, request.getFileName(), request);
		}
		
		request.setFileUrl(fileUrl);
		request.setType(request.getKbType());
		request.setUser(userProtobuf.toJson());
		// 
		return create(request);
	}

	// ==================== MinIO 存储方法 ====================

	/**
	 * 将文件存储到 MinIO
	 * 
	 * @param file MultipartFile 文件
	 * @param fileName 文件名
	 * @param request 上传请求
	 * @return 文件访问URL
	 */
	public String storeToMinio(MultipartFile file, String fileName, UploadRequest request) {
		// 检查 MinIO 是否启用
		if (!bytedeskProperties.getMinioEnabled()) {
			throw new RuntimeException("MinIO 存储未启用，请在配置中启用 bytedesk.minio.enabled=true");
		}

		// 检查 MinIO 服务是否可用
		if (uploadMinioService == null) {
			throw new RuntimeException("MinIO 服务未初始化，请检查配置");
		}

		try {
			if (file.isEmpty()) {
				throw new UploadStorageException("Failed to store empty file.");
			}

			// 根据文件类型选择存储文件夹
			String folder = getMinioFolderByFileType(file, request);
			
			// 上传到 MinIO
			String fileUrl = uploadMinioService.uploadFile(file, fileName, folder);
			
			log.info("文件已成功上传到 MinIO: {}", fileUrl);
			return fileUrl;

		} catch (Exception e) {
			log.error("上传文件到 MinIO 失败: {}", e.getMessage(), e);
			throw new UploadStorageException("Failed to store file to MinIO.", e);
		}
	}

	/**
	 * 将本地文件存储到 MinIO
	 * 
	 * @param localFilePath 本地文件路径
	 * @param fileName 文件名
	 * @param request 上传请求
	 * @return 文件访问URL
	 */
	public String storeLocalFileToMinio(String localFilePath, String fileName, UploadRequest request) {
		// 检查 MinIO 是否启用
		if (!bytedeskProperties.getMinioEnabled()) {
			throw new RuntimeException("MinIO 存储未启用，请在配置中启用 bytedesk.minio.enabled=true");
		}

		// 检查 MinIO 服务是否可用
		if (uploadMinioService == null) {
			throw new RuntimeException("MinIO 服务未初始化，请检查配置");
		}

		try {
			File localFile = new File(localFilePath);
			if (!localFile.exists()) {
				throw new UploadStorageException("Local file not found: " + localFilePath);
			}

			// 根据文件类型选择存储文件夹
			String folder = getMinioFolderByFileName(fileName, request);
			
			// 上传到 MinIO
			String fileUrl = uploadMinioService.uploadFile(localFile, fileName, folder);
			
			log.info("本地文件已成功上传到 MinIO: {}", fileUrl);
			return fileUrl;

		} catch (Exception e) {
			log.error("上传本地文件到 MinIO 失败: {}", e.getMessage(), e);
			throw new UploadStorageException("Failed to store local file to MinIO.", e);
		}
	}

	/**
	 * 从 URL 下载并存储到 MinIO
	 * 
	 * @param url 文件URL
	 * @param fileName 文件名
	 * @param request 上传请求
	 * @return 文件访问URL
	 */
	public String storeUrlToMinio(String url, String fileName, UploadRequest request) {
		// 检查 MinIO 是否启用
		if (!bytedeskProperties.getMinioEnabled()) {
			throw new RuntimeException("MinIO 存储未启用，请在配置中启用 bytedesk.minio.enabled=true");
		}

		// 检查 MinIO 服务是否可用
		if (uploadMinioService == null) {
			throw new RuntimeException("MinIO 服务未初始化，请检查配置");
		}

		try {
			// 根据文件类型选择存储文件夹
			String folder = getMinioFolderByFileName(fileName, request);
			
			// 从 URL 下载并上传到 MinIO
			String fileUrl = uploadMinioService.uploadFromUrl(url, fileName, folder);
			
			log.info("URL 文件已成功上传到 MinIO: {}", fileUrl);
			return fileUrl;

		} catch (Exception e) {
			log.error("从 URL 上传文件到 MinIO 失败: {}", e.getMessage(), e);
			throw new UploadStorageException("Failed to store URL file to MinIO.", e);
		}
	}

	/**
	 * 根据文件类型和请求信息确定 MinIO 存储文件夹
	 * 
	 * @param file MultipartFile 文件
	 * @param request 上传请求
	 * @return 存储文件夹路径
	 */
	private String getMinioFolderByFileType(MultipartFile file, UploadRequest request) {
		String contentType = file.getContentType();
		String fileName = file.getOriginalFilename();
		
		if (contentType != null) {
			if (contentType.startsWith("image/")) {
				return "images";
			} else if (contentType.startsWith("audio/")) {
				return "audios";
			} else if (contentType.startsWith("video/")) {
				return "videos";
			} else if (contentType.equals("application/pdf") || 
					   contentType.contains("document") || 
					   contentType.contains("word") || 
					   contentType.contains("excel") || 
					   contentType.contains("powerpoint")) {
				return "documents";
			}
		}
		
		// 根据文件名扩展名判断
		return getMinioFolderByFileName(fileName, request);
	}

	/**
	 * 根据文件名确定 MinIO 存储文件夹
	 * 
	 * @param fileName 文件名
	 * @param request 上传请求
	 * @return 存储文件夹路径
	 */
	private String getMinioFolderByFileName(String fileName, UploadRequest request) {
		if (fileName == null) {
			return "others";
		}
		
		String lowerFileName = fileName.toLowerCase();
		
		// 图片文件
		if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg") || 
			lowerFileName.endsWith(".png") || lowerFileName.endsWith(".gif") || 
			lowerFileName.endsWith(".webp") || lowerFileName.endsWith(".bmp") || 
			lowerFileName.endsWith(".svg")) {
			return "images";
		}
		
		// 音频文件
		if (lowerFileName.endsWith(".mp3") || lowerFileName.endsWith(".wav") || 
			lowerFileName.endsWith(".aac") || lowerFileName.endsWith(".ogg") || 
			lowerFileName.endsWith(".flac") || lowerFileName.endsWith(".m4a")) {
			return "audios";
		}
		
		// 视频文件
		if (lowerFileName.endsWith(".mp4") || lowerFileName.endsWith(".avi") || 
			lowerFileName.endsWith(".mov") || lowerFileName.endsWith(".wmv") || 
			lowerFileName.endsWith(".flv") || lowerFileName.endsWith(".mkv") || 
			lowerFileName.endsWith(".webm")) {
			return "videos";
		}
		
		// 文档文件
		if (lowerFileName.endsWith(".pdf") || lowerFileName.endsWith(".doc") || 
			lowerFileName.endsWith(".docx") || lowerFileName.endsWith(".xls") || 
			lowerFileName.endsWith(".xlsx") || lowerFileName.endsWith(".ppt") || 
			lowerFileName.endsWith(".pptx") || lowerFileName.endsWith(".txt")) {
			return "documents";
		}
		
		// 压缩文件
		if (lowerFileName.endsWith(".zip") || lowerFileName.endsWith(".rar") || 
			lowerFileName.endsWith(".7z") || lowerFileName.endsWith(".tar") || 
			lowerFileName.endsWith(".gz")) {
			return "archives";
		}
		
		// 根据请求类型判断
		if (request != null && request.getKbType() != null) {
			switch (request.getKbType().toLowerCase()) {
				case "avatar":
					return "avatars";
				case "attachment":
					return "attachments";
				case "image":
					return "images";
				case "audio":
					return "audios";
				case "video":
					return "videos";
				case "document":
					return "documents";
				default:
					break;
			}
		}
		
		return "others";
	}

	/**
	 * 删除 MinIO 中的文件
	 * 
	 * @param objectPath 对象路径
	 */
	public void deleteFromMinio(String objectPath) {
		if (!bytedeskProperties.getMinioEnabled()) {
			throw new RuntimeException("MinIO 存储未启用");
		}

		// 检查 MinIO 服务是否可用
		if (uploadMinioService == null) {
			throw new RuntimeException("MinIO 服务未初始化，请检查配置");
		}
		
		try {
			uploadMinioService.deleteFile(objectPath);
			log.info("成功删除 MinIO 文件: {}", objectPath);
		} catch (Exception e) {
			log.error("删除 MinIO 文件失败: {}", e.getMessage(), e);
			throw new RuntimeException("删除 MinIO 文件失败: " + e.getMessage(), e);
		}
	}

	/**
	 * 检查 MinIO 中文件是否存在
	 * 
	 * @param objectPath 对象路径
	 * @return 是否存在
	 */
	public boolean fileExistsInMinio(String objectPath) {
		if (!bytedeskProperties.getMinioEnabled()) {
			return false;
		}

		// 检查 MinIO 服务是否可用
		if (uploadMinioService == null) {
			return false;
		}
		
		return uploadMinioService.fileExists(objectPath);
	}

	/**
	 * 获取 MinIO 文件下载URL（预签名URL）
	 * 
	 * @param objectPath 对象路径
	 * @param expiry 过期时间（秒）
	 * @return 预签名URL
	 */
	public String getMinioDownloadUrl(String objectPath, int expiry) {
		if (!bytedeskProperties.getMinioEnabled()) {
			throw new RuntimeException("MinIO 存储未启用");
		}

		// 检查 MinIO 服务是否可用
		if (uploadMinioService == null) {
			throw new RuntimeException("MinIO 服务未初始化，请检查配置");
		}
		
		return uploadMinioService.getDownloadUrl(objectPath, expiry);
	}

	/**
	 * 获取 MinIO 文件上传URL（预签名URL）
	 * 
	 * @param objectPath 对象路径
	 * @param expiry 过期时间（秒）
	 * @return 预签名URL
	 */
	public String getMinioUploadUrl(String objectPath, int expiry) {
		if (!bytedeskProperties.getMinioEnabled()) {
			throw new RuntimeException("MinIO 存储未启用");
		}

		// 检查 MinIO 服务是否可用
		if (uploadMinioService == null) {
			throw new RuntimeException("MinIO 服务未初始化，请检查配置");
		}
		
		return uploadMinioService.getUploadUrl(objectPath, expiry);
	}

	
	

}
