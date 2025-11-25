/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 10:48:03
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
import javax.imageio.ImageIO;
import jakarta.servlet.http.HttpServletRequest;

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
import com.bytedesk.core.utils.BdUploadUtils;
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

	private final UploadSecurityConfig uploadSecurityConfig;

	private final UploadSecurityLogger uploadSecurityLogger;

	// 可选依赖：水印服务（当 bytedesk.watermark.enabled=false 时不加载）
	@Autowired(required = false)
	private UploadWatermarkService uploadWatermarkService;

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
		// 文件名再次过滤，防止绕过
		fileName = filterAndRenameFileName(fileName);
		// 根据当前日期创建文件夹，格式如：2021/03/15
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		String currentDateFolder = LocalDate.now().format(formatter);

		try {
			if (file.isEmpty()) {
				throw new UploadStorageException("Failed to store empty file.");
			}

			// 文件类型白名单校验
			String contentType = file.getContentType();
			if (!isAllowedFileType(fileName, contentType)) {
				throw new UploadStorageException("不支持的文件类型");
			}
		// 文件大小限制
		if (!isFileSizeValid(file.getSize())) {
			throw new UploadStorageException("文件过大，最大支持" + getFileSizeDescription());
		}
		// 图片内容校验
		if (uploadSecurityConfig.isEnableImageValidation() && isImageFile(fileName, contentType)) {
			try {
				ImageIO.read(file.getInputStream());
			} catch (Exception e) {
				throw new UploadStorageException("图片内容校验失败");
			}
		}			// 构建包含日期文件夹的文件路径
			Path dateFolderPath = this.uploadDir.resolve(currentDateFolder);
			Files.createDirectories(dateFolderPath); // 创建日期文件夹（如果不存在）

			Path destinationFile = dateFolderPath.resolve(fileName).normalize().toAbsolutePath();

			if (!destinationFile.getParent().equals(dateFolderPath.toAbsolutePath())) {
				// 这是一个安全检查
				throw new UploadStorageException("Cannot store file outside current directory.");
			}

			// 检查是否需要添加水印（水印服务可能未启用）
			if (uploadWatermarkService != null && uploadWatermarkService.shouldAddWatermark(file, request)) {
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

    // ========== 安全辅助方法 ========== 
    // 文件类型白名单校验 - 使用配置化的安全策略
    private boolean isAllowedFileType(String fileName, String contentType) {
        String ext = getFileExt(fileName);
        
        // 检查扩展名
        if (!uploadSecurityConfig.isExtensionAllowed(ext)) {
            return false;
        }
        
        // 检查MIME类型
        if (contentType != null && !uploadSecurityConfig.isMimeTypeAllowed(contentType)) {
            return false;
        }
        
        return true;
    }

    private boolean isImageFile(String fileName, String contentType) {
        return BdUploadUtils.isImageFile(fileName, contentType);
    }

    private String getFileExt(String fileName) {
        return BdUploadUtils.getFileExtension(fileName);
    }

    // 文件名过滤与重命名 - 使用配置化的安全策略
    private String filterAndRenameFileName(String fileName) {
        if (fileName == null) fileName = "file";
        
        // 检查文件名长度
        if (fileName.length() > uploadSecurityConfig.getMaxFileNameLength()) {
            fileName = BdUploadUtils.truncateFileName(fileName, uploadSecurityConfig.getMaxFileNameLength());
        }
        
        if (uploadSecurityConfig.isEnableFileNameFilter()) {
            fileName = BdUploadUtils.sanitizeFileName(fileName);
        }
        
        if (uploadSecurityConfig.isForceRename()) {
            fileName = BdUploadUtils.generateSafeFileName(fileName);
        }
        
        return fileName;
    }

    // 文件大小校验
    private boolean isFileSizeValid(long fileSize) {
        return uploadSecurityConfig.isFileSizeValid(fileSize);
    }

    // 获取文件大小限制描述
    private String getFileSizeDescription() {
        return uploadSecurityConfig.getMaxFileSizeDescription();
    }	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.uploadDir, 1)
					.filter(path -> !path.equals(this.uploadDir))
					.map(this.uploadDir::relativize);
		} catch (IOException e) {
			throw new UploadStorageException("Failed to read stored files", e);
		}
	}

	/**
	 * 从 UploadEntity 加载文件资源
	 * 优先使用 fileUrl，如果 fileUrl 为空则使用 fileName
	 * 
	 * @param upload UploadEntity 对象
	 * @return Resource
	 */
	public Resource loadAsResource(UploadEntity upload) {
		if (upload.getFileUrl() != null && !upload.getFileUrl().isEmpty()) {
			// 从 fileUrl 中提取相对路径
			String relativePath = extractRelativePathFromUrl(upload.getFileUrl());
			if (relativePath != null) {
				return loadAsResource(relativePath);
			}
		}
		// 如果 fileUrl 为空或提取失败，则使用 fileName
		return loadAsResource(upload.getFileName());
	}

	/**
	 * 从文件URL中提取相对路径
	 * 例如：http://127.0.0.1:9003/file/2025/09/05/1757039824330_8577.pdf 
	 * 提取为：2025/09/05/1757039824330_8577.pdf
	 * 
	 * @param fileUrl 完整的文件URL
	 * @return 相对路径，如果提取失败返回null
	 */
	private String extractRelativePathFromUrl(String fileUrl) {
		return BdUploadUtils.extractRelativePathFromUrl(fileUrl);
	}

	/**
	 * 加载文件资源
	 * Resource resource2 = uploadRestService.loadAsResource("2025/09/05/1757039824330_8577.pdf");
	 * 
	 * @param filenameOrPath 文件名（如：20240916144702_身份证-背面.jpg）或完整路径（如：2025/09/05/1757039824330_8577.pdf）
	 * @return Resource
	 */
	public Resource loadAsResource(String filenameOrPath) {
		String datePath;
		String filename;
		
		// 判断是否包含路径分隔符
		if (filenameOrPath.contains("/")) {
			// 包含路径分隔符，说明是完整路径格式：2025/09/05/filename.ext
			int lastSlashIndex = filenameOrPath.lastIndexOf("/");
			datePath = filenameOrPath.substring(0, lastSlashIndex);
			filename = filenameOrPath.substring(lastSlashIndex + 1);
			log.info("Loading file from full path: {}, datePath: {}, filename: {}", filenameOrPath, datePath, filename);
		} else {
			// 不包含路径分隔符，说明是文件名格式：20240916144702_身份证-背面.jpg
			// 使用工具类提取日期路径
			datePath = BdUploadUtils.extractDatePathFromTimestampFileName(filenameOrPath);
			if (datePath == null) {
				throw new UploadStorageFileNotFoundException("Invalid filename format: " + filenameOrPath);
			}
			filename = filenameOrPath;
			log.info("Loading file from filename: {}, extracted datePath: {}", filenameOrPath, datePath);
		}
		
		// 构建文件夹路径
		Path dateFolderPath = this.uploadDir.resolve(datePath);

		// 创建日期文件夹（如果不存在）
		try {
			Files.createDirectories(dateFolderPath);
		} catch (IOException e) {
			log.error("Failed to create directory: {}", dateFolderPath, e);
		}

		// 构建完整的文件路径
		Path filePath = dateFolderPath.resolve(filename);
		log.info("Full file path: {}", filePath);

		try {
			if (Files.exists(filePath)) {
				Resource resource = new UrlResource(filePath.toUri());
				if (resource.exists() || resource.isReadable()) {
					log.info("Successfully loaded resource: {}", filePath);
					return resource;
				} else {
					throw new UploadStorageFileNotFoundException(
							"Could not read file: " + filename);
				}
			} else {
				log.error("File does not exist: {}", filePath);
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
		return handleFileUpload(file, request, null);
	}

	public UploadResponse handleFileUpload(MultipartFile file, UploadRequest request, HttpServletRequest httpRequest) {
		log.info("handleFileUpload fileName: {}, fileType: {}, kbType {}, extra {}", 
			request.getFileName(), request.getFileType(), request.getKbType(), request.getExtra());

		try {
			// 1. 文件类型白名单校验
			String originalFileName = file.getOriginalFilename();
			String safeFileName = filterAndRenameFileName(originalFileName);
			String contentType = file.getContentType();
			if (!isAllowedFileType(safeFileName, contentType)) {
				uploadSecurityLogger.logSecurityThreat(file, request, "ILLEGAL_FILE_TYPE", 
					"不支持的文件类型: " + contentType, httpRequest);
				throw new UploadStorageException("不支持的文件类型");
			}

			// 2. 文件大小限制
			if (!isFileSizeValid(file.getSize())) {
				uploadSecurityLogger.logUploadFailure(file, request, 
					"文件过大，最大支持" + getFileSizeDescription(), httpRequest);
				throw new UploadStorageException("文件过大，最大支持" + getFileSizeDescription());
			}

			// 3. 图片内容校验（防止伪造扩展名）
			if (uploadSecurityConfig.isEnableImageValidation() && isImageFile(safeFileName, contentType)) {
				try {
					ImageIO.read(file.getInputStream());
				} catch (Exception e) {
					uploadSecurityLogger.logSecurityThreat(file, request, "FAKE_IMAGE", 
						"图片内容校验失败", httpRequest);
					throw new UploadStorageException("图片内容校验失败");
				}
			}

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

			// 4. 存储文件并获取实际的文件URL
			String fileUrl;
			if (bytedeskProperties.getMinioEnabled()) {
				log.info("MinIO 已启用，使用 MinIO 存储文件");
				fileUrl = storeToMinio(file, safeFileName, request);
			} else {
				log.info("MinIO 未启用，使用本地文件系统存储文件");
				fileUrl = store(file, safeFileName, request);
			}

			// 5. 从fileUrl中提取实际存储的文件名（用于日志记录）
			String actualStoredFileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
			log.info("实际存储的文件名: {}, 原始文件名: {}", actualStoredFileName, originalFileName);
			
			// 6. 设置到request中：fileName保留原始文件名，fileUrl包含安全处理后的路径
			request.setFileName(originalFileName);  // 保留原始文件名供前端显示
			request.setFileUrl(fileUrl);            // 实际存储路径（可能包含安全重命名）
			request.setType(request.getKbType());
			request.setUser(userProtobuf.toJson());
			
			UploadResponse response = create(request);
			
			// 记录成功日志
			uploadSecurityLogger.logUploadSuccess(file, request, fileUrl, httpRequest);
			
			return response;
			
		} catch (UploadStorageException e) {
			// 记录失败日志
			uploadSecurityLogger.logUploadFailure(file, request, e.getMessage(), httpRequest);
			throw e;
		} catch (Exception e) {
			// 记录失败日志
			uploadSecurityLogger.logUploadFailure(file, request, "系统内部错误: " + e.getMessage(), httpRequest);
			throw new UploadStorageException("文件上传失败: " + e.getMessage(), e);
		}
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

			// 文件名再次过滤，防止绕过
			fileName = filterAndRenameFileName(fileName);
			
			// 文件类型白名单校验
			String contentType = file.getContentType();
			if (!isAllowedFileType(fileName, contentType)) {
				throw new UploadStorageException("不支持的文件类型");
			}
			
			// 文件大小限制
			if (!isFileSizeValid(file.getSize())) {
				throw new UploadStorageException("文件过大，最大支持" + getFileSizeDescription());
			}
			
			// 图片内容校验
			if (uploadSecurityConfig.isEnableImageValidation() && isImageFile(fileName, contentType)) {
				try {
					ImageIO.read(file.getInputStream());
				} catch (Exception e) {
					throw new UploadStorageException("图片内容校验失败");
				}
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
		
		// 使用工具类根据文件类型获取文件夹
		String folder = BdUploadUtils.getFileFolderByType(fileName, contentType);
		
		// 根据请求类型进行特殊处理
		if (request != null && request.getKbType() != null) {
			switch (request.getKbType().toLowerCase()) {
				case "avatar":
					return "avatars";
				case "attachment":
					return "attachments";
				default:
					break;
			}
		}
		
		return folder;
	}

	/**
	 * 根据文件名确定 MinIO 存储文件夹
	 * 
	 * @param fileName 文件名
	 * @param request 上传请求
	 * @return 存储文件夹路径
	 */
	private String getMinioFolderByFileName(String fileName, UploadRequest request) {
		// 使用工具类根据文件类型获取文件夹
		String folder = BdUploadUtils.getFileFolderByType(fileName, null);
		
		// 根据请求类型进行特殊处理
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
		
		return folder;
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
