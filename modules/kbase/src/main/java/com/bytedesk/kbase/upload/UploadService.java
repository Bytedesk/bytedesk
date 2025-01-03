/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-03 15:14:50
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.upload;

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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.BytedeskProperties;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.upload.storage.UploadStorageException;
import com.bytedesk.kbase.upload.storage.UploadStorageFileNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// https://spring.io/guides/gs/uploading-files
@Slf4j
@Service
@AllArgsConstructor
public class UploadService extends BaseRestService<UploadEntity, UploadRequest, UploadResponse> {

	private final Path uploadDir;

	private final UidUtils uidUtils;

	private final ModelMapper modelMapper;

	private final UploadRepository uploadRepository;

	private final BytedeskProperties bytedeskProperties;

	public UploadResponse create(UploadRequest request) {

		UploadEntity upload = modelMapper.map(request, UploadEntity.class);
		upload.setUid(uidUtils.getCacheSerialUid());
		// upload.setClient(ClientEnum.fromValue(request.getClient()).name());
		upload.setClient(request.getClient());
		// upload.setType(UploadTypeEnum.fromValue(request.getType()).name());
		upload.setType(request.getType());
		if (upload.getType().equals(UploadTypeEnum.LLM.name())) {
			upload.setStatus(UploadStatusEnum.PARSING.name());
		} else {
			upload.setStatus(UploadStatusEnum.UPLOADED.name());
		}
		//
		UploadEntity savedUpload = save(upload);
		if (savedUpload == null) {
			throw new RuntimeException("Failed to store file " + upload.getFileName());
		}

		return convertToResponse(savedUpload);
	}

	public UploadEntity save(UploadEntity upload) {
		try {
			return uploadRepository.save(upload);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 给出图片URL地址，将其保存到本地
    public String storeFromUrl(String url, String fileName) {
        // 根据当前日期创建文件夹，格式如：2021/03/15
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String currentDateFolder = LocalDate.now().format(formatter);

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

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
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
	
	// TODO: 待删除
	@Deprecated
	public Resource loadAsResourceOld(String filename) {
		try {
			Path file = uploadDir.resolve(filename);
			//load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new UploadStorageFileNotFoundException(
						"Could not read file: " + filename);
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
	public Page<UploadResponse> queryByOrg(UploadRequest request) {

		Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
				"updatedAt");
		Specification<UploadEntity> specification = UploadSpecification.search(request);
		Page<UploadEntity> page = uploadRepository.findAll(specification, pageable);

		return page.map(this::convertToResponse);
	}

	@Override
	public Page<UploadResponse> queryByUser(UploadRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
	}

	@Override
	public Optional<UploadEntity> findByUid(String uid) {
		return uploadRepository.findByUid(uid);
	}

	@Override
	public UploadResponse update(UploadRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'update'");
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
	public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, UploadEntity entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
	}

	@Override
	public UploadResponse convertToResponse(UploadEntity entity) {
		UploadResponse uploadResponse = this.modelMapper.map(entity, UploadResponse.class);
		// 上一行没有自动初始化isLlm字段，所以这里需要手动设置
		// uploadResponse.setIsLlm(entity.isLlm());
		return uploadResponse;
	}

}
