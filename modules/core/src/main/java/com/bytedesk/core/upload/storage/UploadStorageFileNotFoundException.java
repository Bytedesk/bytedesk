package com.bytedesk.core.upload.storage;

public class UploadStorageFileNotFoundException extends UploadStorageException {

	public UploadStorageFileNotFoundException(String message) {
		super(message);
	}

	public UploadStorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
