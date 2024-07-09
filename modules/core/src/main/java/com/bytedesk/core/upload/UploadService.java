/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 11:35:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-07-08 17:32:30
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.core.upload.storage.UploadStorageException;
import com.bytedesk.core.upload.storage.UploadStorageFileNotFoundException;
import com.bytedesk.core.upload.storage.UploadStorageProperties;

// https://spring.io/guides/gs/uploading-files
@Service
public class UploadService {

	private final Path rootLocation;

	// private UploadReposistory uploadReposistory;

	@Autowired
	public UploadService(UploadStorageProperties properties) {
		if (properties.getUploadDir().trim().length() == 0) {
			throw new UploadStorageException("File upload location can not be Empty.");
		}
		this.rootLocation = Paths.get(properties.getUploadDir());
	}

	public String store(MultipartFile file, String fileName) {
		try {
			if (file.isEmpty()) {
				throw new UploadStorageException("Failed to store empty file.");
			}
			Path destinationFile = this.rootLocation.resolve(
					Paths.get(
							// file.getOriginalFilename()
							fileName))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
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

		// TODO:
		// uploadReposistory.save(new Upload(file.getOriginalFilename()));
	}

	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
					.filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new UploadStorageException("Failed to read stored files", e);
		}
	}

	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	// @SuppressWarnings("null")
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

	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	public void initUploadDir() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new UploadStorageException("Could not initialize storage", e);
		}
	}
}
