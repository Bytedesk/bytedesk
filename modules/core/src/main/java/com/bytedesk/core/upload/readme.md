# upload

This package manages upload records, multi-backend file storage, preview and watermark processing, and upload lifecycle APIs.

## Implementation Notes

- Core models include UploadEntity, UploadRequest, UploadResponse, UploadExcel, UploadFilePreview, UploadStatusEnum, and UploadTypeEnum.
- UploadRepository, UploadSpecification, UploadRestController, UploadRestControllerVisitor, and UploadRestService provide persistence, filtering, admin endpoints, visitor upload endpoints, and upload orchestration.
- UploadConfig, UploadSecurityConfig, and UploadSecurityLogger define runtime configuration and upload security behavior.
- UploadWatermarkService, UploadInitializer, UploadPermissions, UploadEntityListener, UploadEventListener, and the event subpackage cover watermark processing, bootstrap data, permission metadata, and create, process, update lifecycle events.
- The aliyun, minio, tencent, storage, and watermark subpackages encapsulate concrete storage and processing integrations.
