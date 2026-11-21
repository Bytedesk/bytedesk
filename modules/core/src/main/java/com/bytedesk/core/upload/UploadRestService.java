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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.MalformedURLException;
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
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.constant.I18Consts;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserConvertUtils;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.storage.UploadObjectStorage;
import com.bytedesk.core.upload.storage.UploadStorageException;
import com.bytedesk.core.upload.storage.UploadStorageFileNotFoundException;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.BdUploadUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.extern.slf4j.Slf4j;

// https://spring.io/guides/gs/uploading-files
@Slf4j
@Service
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
	private final ObjectProvider<UploadWatermarkService> uploadWatermarkServiceProvider;

	// 可选依赖：对象存储（企业版实现，社区版无实现时仅本地磁盘存储）。
	// 持有 ObjectProvider 而非构造期解析实例，避免与 UploadObjectStorageService
	// （其注入 UploadRestService）形成构造期循环依赖
	private final ObjectProvider<UploadObjectStorage> uploadObjectStorageProvider;

	public UploadRestService(Path uploadDir,
			UidUtils uidUtils,
			ModelMapper modelMapper,
			UploadRepository uploadRepository,
			BytedeskProperties bytedeskProperties,
			AuthService authService,
			UploadSecurityConfig uploadSecurityConfig,
			UploadSecurityLogger uploadSecurityLogger,
			ObjectProvider<UploadWatermarkService> uploadWatermarkServiceProvider,
			ObjectProvider<UploadObjectStorage> uploadObjectStorageProvider) {
		this.uploadDir = uploadDir;
		this.uidUtils = uidUtils;
		this.modelMapper = modelMapper;
		this.uploadRepository = uploadRepository;
		this.bytedeskProperties = bytedeskProperties;
		this.authService = authService;
		this.uploadSecurityConfig = uploadSecurityConfig;
		this.uploadSecurityLogger = uploadSecurityLogger;
		this.uploadWatermarkServiceProvider = uploadWatermarkServiceProvider;
		this.uploadObjectStorageProvider = uploadObjectStorageProvider;
	}

	/**
	 * 懒解析对象存储实现；出现循环依赖异常时按无实现处理，回落本地磁盘存储
	 */
	private UploadObjectStorage resolveObjectStorage() {
		try {
			return uploadObjectStorageProvider.getIfAvailable();
		} catch (BeanCreationException e) {
			log.warn("对象存储实现加载失败，回落本地磁盘存储: {}", e.getMessage());
			return null;
		}
	}

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
			throw new UploadStorageException("保存上传记录失败: " + upload.getFileName(), 500);
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
				throw new UploadStorageException("非法文件路径（疑似路径穿越攻击）", 400);
            }

            // 下载并保存图片
			try (InputStream inputStream = URI.create(url).toURL().openStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // return destinationFile.toString();
			String uploadPath = currentDateFolder + "/" + fileName;
			String fileUrl = String.format("%s/file/%s", bytedeskProperties.getUploadUrl(), uploadPath);
			return fileUrl;
        } catch (IOException e) {
			throw new UploadStorageException(
				"从URL下载并保存文件失败: " + (e.getMessage() == null ? "未知错误" : e.getMessage()),
				400,
				e
			);
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
			if (file == null || file.isEmpty()) {
				throw new UploadStorageException("空文件，无法上传", 400);
			}

			// 文件类型白名单校验
			String contentType = file.getContentType();
			if (!isAllowedFileType(fileName, contentType)) {
				String ext = getFileExt(fileName);
				String reason = (ext == null || ext.isEmpty()) ? "不支持的文件类型" : ("不支持的文件类型: ." + ext);
				throw new UploadStorageException(reason, 415);
			}
			// 文件大小限制
			if (!isFileSizeValid(file.getSize())) {
				throw new UploadStorageException("文件过大，最大支持" + getFileSizeDescription(), 413);
			}
			// 图片内容校验
			if (uploadSecurityConfig.isEnableImageValidation() && isImageFile(fileName, contentType)) {
				try {
					ImageIO.read(file.getInputStream());
				} catch (Exception e) {
					throw new UploadStorageException("图片内容校验失败（文件可能已损坏或伪造扩展名）", 422);
				}
			}
			// 构建包含日期文件夹的文件路径
			Path dateFolderPath = this.uploadDir.resolve(currentDateFolder);
			Files.createDirectories(dateFolderPath); // 创建日期文件夹（如果不存在）

			Path destinationFile = dateFolderPath.resolve(fileName).normalize().toAbsolutePath();

			if (!destinationFile.getParent().equals(dateFolderPath.toAbsolutePath())) {
				// 这是一个安全检查
				throw new UploadStorageException("非法文件路径（疑似路径穿越攻击）", 400);
			}

			// 检查是否需要添加水印（水印服务可能未启用）
			UploadWatermarkService uploadWatermarkService = uploadWatermarkServiceProvider.getIfAvailable();
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
			throw new UploadStorageException(
				"保存文件失败: " + (e.getMessage() == null ? "未知错误" : e.getMessage()),
				503,
				e
			);
		}
	}

    // ========== 安全辅助方法 ========== 
    // 文件类型白名单校验 - 使用配置化的安全策略（public 供企业版对象存储实现复用同一套校验规则）
    public boolean isAllowedFileType(String fileName, String contentType) {
        String ext = getFileExt(fileName);

		// 主动内容文件（尤其是 SVG）存在脚本执行风险，禁止通过上传链路直接落盘
		if ("svg".equalsIgnoreCase(ext) || "svgz".equalsIgnoreCase(ext)) {
			return false;
		}
		if (contentType != null && contentType.toLowerCase().contains("image/svg")) {
			return false;
		}
        
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

    public boolean isImageFile(String fileName, String contentType) {
        return BdUploadUtils.isImageFile(fileName, contentType);
    }

    public String getFileExt(String fileName) {
        return BdUploadUtils.getFileExtension(fileName);
    }

    // 文件名过滤与重命名 - 使用配置化的安全策略
    public String filterAndRenameFileName(String fileName) {
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
    public boolean isFileSizeValid(long fileSize) {
        return uploadSecurityConfig.isFileSizeValid(fileSize);
    }

    // 获取文件大小限制描述
    public String getFileSizeDescription() {
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
			// 对象存储直链（{endpoint}/{bucket}/{objectPath}）：经对象存储客户端下载到临时文件后返回资源，
			// 否则本地存储逻辑无法识别该 URL，会回落到原始文件名导致 Invalid filename format
			UploadObjectStorage objectStorage = resolveObjectStorage();
			if (objectStorage != null) {
				Resource objectResource = objectStorage.tryLoad(upload.getFileUrl());
				if (objectResource != null) {
					return objectResource;
				}
			}
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
		if (filenameOrPath == null || filenameOrPath.trim().isEmpty()) {
			throw new UploadStorageFileNotFoundException("File path is empty");
		}

		String normalizedInput = filenameOrPath.trim();
		// Accept full upload URL input and normalize it to relative storage path.
		if (normalizedInput.startsWith("http://") || normalizedInput.startsWith("https://")) {
			String relativePath = extractRelativePathFromUrl(normalizedInput);
			if (relativePath != null && !relativePath.isEmpty()) {
				normalizedInput = relativePath;
				log.info("Normalized file URL to relative path: {} -> {}", filenameOrPath, normalizedInput);
			}
		}

		String datePath;
		String filename;
		
		// 判断是否包含路径分隔符
		if (normalizedInput.contains("/")) {
			// 包含路径分隔符，说明是完整路径格式：2025/09/05/filename.ext
			int lastSlashIndex = normalizedInput.lastIndexOf("/");
			datePath = normalizedInput.substring(0, lastSlashIndex);
			filename = normalizedInput.substring(lastSlashIndex + 1);
			log.info("Loading file from full path: {}, datePath: {}, filename: {}", normalizedInput, datePath, filename);
		} else {
			// 不包含路径分隔符，说明是文件名格式：20240916144702_身份证-背面.jpg
			// 使用工具类提取日期路径
			datePath = BdUploadUtils.extractDatePathFromTimestampFileName(normalizedInput);
			if (datePath == null) {
				throw new UploadStorageFileNotFoundException("Invalid filename format: " + normalizedInput);
			}
			filename = normalizedInput;
			log.info("Loading file from filename: {}, extracted datePath: {}", normalizedInput, datePath);
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
			throw new UploadStorageException(
				"初始化上传目录失败（请检查磁盘权限/路径配置）: " + (e.getMessage() == null ? "未知错误" : e.getMessage()),
				503,
				e
			);
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
			throw new RuntimeException(I18Consts.withArgs(I18Consts.I18N_FILE_NOT_FOUND, filename));
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
			if (file == null || file.isEmpty()) {
				uploadSecurityLogger.logUploadFailure(file, request, "空文件", httpRequest);
				throw new UploadStorageException("空文件，无法上传", 400);
			}

			// 1. 文件类型白名单校验
			String originalFileName = file.getOriginalFilename();
			if (originalFileName == null || originalFileName.trim().isEmpty()) {
				uploadSecurityLogger.logUploadFailure(file, request, "文件名为空", httpRequest);
				throw new UploadStorageException("文件名为空，无法上传", 400);
			}
			String safeFileName = filterAndRenameFileName(originalFileName);
			String contentType = file.getContentType();
			if (!isAllowedFileType(safeFileName, contentType)) {
				String ext = getFileExt(safeFileName);
				uploadSecurityLogger.logSecurityThreat(file, request, "ILLEGAL_FILE_TYPE", 
					(ext == null || ext.isEmpty()) ? "不支持的文件类型" : ("不支持的文件类型: ." + ext), httpRequest);
				String reason = (ext == null || ext.isEmpty()) ? "不支持的文件类型" : ("不支持的文件类型: ." + ext);
				throw new UploadStorageException(reason, 415);
			}

			// 2. 文件大小限制
			if (!isFileSizeValid(file.getSize())) {
				uploadSecurityLogger.logUploadFailure(file, request, 
					"文件过大，最大支持" + getFileSizeDescription(), httpRequest);
				throw new UploadStorageException("文件过大，最大支持" + getFileSizeDescription(), 413);
			}

			// 3. 图片内容校验（防止伪造扩展名）
			if (uploadSecurityConfig.isEnableImageValidation() && isImageFile(safeFileName, contentType)) {
				try {
					ImageIO.read(file.getInputStream());
				} catch (Exception e) {
					uploadSecurityLogger.logSecurityThreat(file, request, "FAKE_IMAGE", 
						"图片内容校验失败", httpRequest);
					throw new UploadStorageException("图片内容校验失败（文件可能已损坏或伪造扩展名）", 422);
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
				userProtobuf = UserConvertUtils.convertToUserProtobuf(user);
				request.setUserUid(user.getUid());
				request.setOrgUid(user.getOrgUid());
			}

			// 4. 存储文件并获取实际的文件URL（对象存储为可选依赖：社区版无实现时回落本地磁盘）
			UploadObjectStorage uploadObjectStorage = resolveObjectStorage();
			String fileUrl;
			if (uploadObjectStorage != null && uploadObjectStorage.isEnabled()) {
				log.info("对象存储已启用，使用对象存储保存文件");
				fileUrl = uploadObjectStorage.upload(file, safeFileName, request);
			} else {
				if (uploadObjectStorage == null) {
					log.info("对象存储未启用（社区版仅本地磁盘存储），使用本地文件系统存储文件");
				} else {
					log.info("对象存储未启用，使用本地文件系统存储文件");
				}
				fileUrl = store(file, safeFileName, request);
			}
			if (fileUrl == null || fileUrl.trim().isEmpty()) {
				throw new UploadStorageException("文件上传失败：未获取到文件地址", 503);
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
			throw new UploadStorageException("文件上传失败: " + (e.getMessage() == null ? "系统内部错误" : e.getMessage()), 500, e);
		}
	}

}
