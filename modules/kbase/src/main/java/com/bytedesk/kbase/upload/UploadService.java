/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-30 22:27:48
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

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

import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.upload.storage.UploadStorageException;
import com.bytedesk.kbase.upload.storage.UploadStorageFileNotFoundException;
import lombok.AllArgsConstructor;

// https://spring.io/guides/gs/uploading-files
@Service
@AllArgsConstructor
public class UploadService extends BaseService<Upload, UploadRequest, UploadResponse> {

	private final Path uploadDir;

	private final UidUtils uidUtils;

	private final ModelMapper modelMapper;

	private final UploadReposistory uploadReposistory;

	public UploadResponse create(UploadRequest request) {

		Upload upload = modelMapper.map(request, Upload.class);
		upload.setUid(uidUtils.getCacheSerialUid());
		upload.setClient(ClientEnum.fromValue(request.getClient()));
		upload.setType(UploadTypeEnum.fromValue(request.getType()));
		upload.setStatus(UploadStatusEnum.UPLOADED);
		//
		Upload savedUpload = save(upload);
		if (savedUpload == null) {
			throw new RuntimeException("Failed to store file " + upload.getFileName());
		}

		return convertToResponse(savedUpload);
	}

	public Upload save(Upload upload) {
		try {
			return uploadReposistory.save(upload);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String store(MultipartFile file, String fileName) {
		try {
			if (file.isEmpty()) {
				throw new UploadStorageException("Failed to store empty file.");
			}
			Path destinationFile = this.uploadDir.resolve(
					Paths.get(fileName))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.uploadDir.toAbsolutePath())) {
				// This is a security check
				throw new UploadStorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
						StandardCopyOption.REPLACE_EXISTING);
			}

			return destinationFile.getFileName().toString();

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

	public Path load(String filename) {
		return uploadDir.resolve(filename);
	}

	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
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
		Specification<Upload> specification = UploadSpecification.search(request);
		Page<Upload> page = uploadReposistory.findAll(specification, pageable);

		return page.map(this::convertToResponse);
	}

	@Override
	public Page<UploadResponse> queryByUser(UploadRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
	}

	@Override
	public Optional<Upload> findByUid(String uid) {
		return uploadReposistory.findByUid(uid);
	}

	@Override
	public UploadResponse update(UploadRequest request) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'update'");
	}

	@Override
	public void deleteByUid(String uid) {
		Optional<Upload> uploadOptional = findByUid(uid);
		if (uploadOptional.isPresent()) {
			uploadOptional.get().setDeleted(true);
			save(uploadOptional.get());
		} else {
			throw new RuntimeException("Upload with uid '" + uid + "' not found");
		}
	}

	@Override
	public void delete(Upload entity) {
		deleteByUid(entity.getUid());
	}

	@Override
	public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Upload entity) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
	}

	@Override
	public UploadResponse convertToResponse(Upload entity) {
		UploadResponse uploadResponse = this.modelMapper.map(entity, UploadResponse.class);
		// 上一行没有自动初始化isLlm字段，所以这里需要手动设置
		// uploadResponse.setIsLlm(entity.isLlm());
		return uploadResponse;
	}

}
