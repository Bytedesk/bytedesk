/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-25 17:30:49
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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;

import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.properties.BytedeskProperties;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.storage.UploadStorageException;
import com.bytedesk.core.upload.storage.UploadStorageFileNotFoundException;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.core.utils.ConvertUtils;

import com.bytedesk.core.upload.watermark.WatermarkConfig;
import com.bytedesk.core.upload.watermark.WatermarkService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// https://spring.io/guides/gs/uploading-files
@Slf4j
@Service
@AllArgsConstructor
public class UploadRestService extends BaseRestService<UploadEntity, UploadRequest, UploadResponse> {

	private final Path uploadDir;

	private final UidUtils uidUtils;

	private final ModelMapper modelMapper;

	private final UploadRepository uploadRepository;

	private final BytedeskProperties bytedeskProperties;

	private final AuthService authService;

	private final WatermarkService watermarkService;

	private final WatermarkConfig watermarkConfig;

	@Override
	public Page<UploadResponse> queryByOrg(UploadRequest request) {
		Pageable pageable = request.getPageable();
		Specification<UploadEntity> specification = UploadSpecification.search(request);
		Page<UploadEntity> page = uploadRepository.findAll(specification, pageable);
		return page.map(this::convertToResponse);
	}

	@Override
	public Page<UploadResponse> queryByUser(UploadRequest request) {
		UserEntity user = authService.getUser();
		if (user == null) {
			throw new RuntimeException("用户未登录");
		}
		request.setUserUid(user.getUid());
		// request.setOrgUid(user.getOrgUid());
		return queryByOrg(request);
	}

	@Override
	public UploadResponse queryByUid(UploadRequest request) {
		Optional<UploadEntity> uploadOptional = findByUid(request.getUid());
		if (uploadOptional.isPresent()) {
			return convertToResponse(uploadOptional.get());
		} else {
			throw new RuntimeException("Upload with uid '" + request.getUid() + "' not found");
		}
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
			if (shouldAddWatermark(file, request)) {
				log.info("为图片添加水印: {}", fileName);
				addWatermarkToFile(file, destinationFile, request);
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

	/**
	 * 判断是否需要添加水印（支持客户端控制）
	 */
	private boolean shouldAddWatermark(MultipartFile file, UploadRequest request) {
		// 如果客户端明确指定不添加水印
		if (request != null && request.getAddWatermark() != null && !request.getAddWatermark()) {
			return false;
		}

		// 检查水印功能是否启用
		if (!watermarkConfig.isEnabled()) {
			return false;
		}

		// 检查是否只对图片文件添加水印
		if (watermarkConfig.isImageOnly() && !watermarkService.isImageFile(file)) {
			return false;
		}

		// 检查图片尺寸
		try {
			BufferedImage image = ImageIO.read(file.getInputStream());
			if (image != null) {
				int width = image.getWidth();
				int height = image.getHeight();
				
				// 检查最小尺寸
				if (width < watermarkConfig.getMinImageSize() || height < watermarkConfig.getMinImageSize()) {
					log.debug("图片尺寸太小，不添加水印: {}x{}", width, height);
					return false;
				}
				
				// 检查最大尺寸
				if (width > watermarkConfig.getMaxImageSize() || height > watermarkConfig.getMaxImageSize()) {
					log.debug("图片尺寸太大，不添加水印: {}x{}", width, height);
					return false;
				}
			}
		} catch (IOException e) {
			log.warn("无法读取图片尺寸，跳过水印检查: {}", file.getOriginalFilename(), e);
			return false;
		}

		return true;
	}

	/**
	 * 为文件添加水印（支持自定义参数）
	 */
	private void addWatermarkToFile(MultipartFile file, Path destinationPath, UploadRequest request) throws IOException {
		try {
			// 读取原始图片
			BufferedImage originalImage = ImageIO.read(file.getInputStream());
			if (originalImage == null) {
				log.error("无法读取图片文件: {}", file.getOriginalFilename());
				// 如果无法读取图片，直接保存原文件
				try (InputStream inputStream = file.getInputStream()) {
					Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
				}
				return;
			}

			// 获取水印参数
			String watermarkText = getWatermarkText(request);
			WatermarkService.WatermarkPosition position = getWatermarkPosition(request);
			int fontSize = getWatermarkFontSize(request);
			Color watermarkColor = getWatermarkColor(request);

			// 添加水印
			byte[] watermarkedImageBytes = watermarkService.addTextWatermark(
				originalImage, 
				watermarkText, 
				position,
				fontSize,
				watermarkColor
			);

			// 保存到文件
			try (InputStream inputStream = new ByteArrayInputStream(watermarkedImageBytes)) {
				Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			}

			log.info("成功为图片添加水印: {}", file.getOriginalFilename());

		} catch (Exception e) {
			log.error("添加水印失败，保存原文件: {}", file.getOriginalFilename(), e);
			// 如果添加水印失败，保存原文件
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
			}
		}
	}

	/**
	 * 获取水印文字
	 */
	private String getWatermarkText(UploadRequest request) {
		if (request != null && request.getWatermarkText() != null && !request.getWatermarkText().trim().isEmpty()) {
			return request.getWatermarkText();
		}
		return watermarkConfig.getText();
	}

	/**
	 * 获取水印位置
	 */
	private WatermarkService.WatermarkPosition getWatermarkPosition(UploadRequest request) {
		if (request != null && request.getWatermarkPosition() != null && !request.getWatermarkPosition().trim().isEmpty()) {
			try {
				return WatermarkService.WatermarkPosition.valueOf(request.getWatermarkPosition().toUpperCase());
			} catch (IllegalArgumentException e) {
				log.warn("无效的水印位置: {}, 使用默认位置", request.getWatermarkPosition());
			}
		}
		return watermarkConfig.getPosition();
	}

	/**
	 * 获取水印字体大小
	 */
	private int getWatermarkFontSize(UploadRequest request) {
		if (request != null && request.getWatermarkFontSize() != null && request.getWatermarkFontSize() > 0) {
			return request.getWatermarkFontSize();
		}
		return watermarkConfig.getFontSize();
	}

	/**
	 * 获取水印颜色
	 */
	private Color getWatermarkColor(UploadRequest request) {
		if (request != null && request.getWatermarkColor() != null && !request.getWatermarkColor().trim().isEmpty()) {
			return parseColor(request.getWatermarkColor());
		}
		return parseColor(watermarkConfig.getColor());
	}

	/**
	 * 解析颜色字符串
	 */
	private Color parseColor(String colorStr) {
		try {
			String[] parts = colorStr.split(",");
			if (parts.length == 4) {
				int r = Integer.parseInt(parts[0].trim());
				int g = Integer.parseInt(parts[1].trim());
				int b = Integer.parseInt(parts[2].trim());
				int a = Integer.parseInt(parts[3].trim());
				return new Color(r, g, b, a);
			} else if (parts.length == 3) {
				int r = Integer.parseInt(parts[0].trim());
				int g = Integer.parseInt(parts[1].trim());
				int b = Integer.parseInt(parts[2].trim());
				return new Color(r, g, b, 128); // 默认透明度
			}
		} catch (Exception e) {
			log.warn("无法解析颜色配置: {}, 使用默认颜色", colorStr, e);
		}
		return new Color(255, 255, 255, 128); // 默认白色半透明
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
	// public void deleteAll() {
	// FileSystemUtils.deleteRecursively(uploadDir.toFile());
	// }

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
		String fileUrl = store(file, request.getFileName(), request);
		request.setFileUrl(fileUrl);
		request.setType(request.getKbType());
		request.setUser(userProtobuf.toJson());
		// 
		return create(request);
	}

	

}
