# upload

该包负责上传记录管理、多后端文件存储、预览与水印处理，以及上传生命周期接口。

## 实现要点

- 核心模型包括 UploadEntity、UploadRequest、UploadResponse、UploadExcel、UploadFilePreview、UploadStatusEnum、UploadTypeEnum。
- UploadRepository、UploadSpecification、UploadRestController、UploadRestControllerVisitor、UploadRestService 提供持久化、条件过滤、管理端接口、访客上传接口与上传编排能力。
- UploadConfig、UploadSecurityConfig、UploadSecurityLogger 定义运行配置和上传安全行为。
- UploadWatermarkService、UploadInitializer、UploadPermissions、UploadEntityListener、UploadEventListener 与 event 子包负责水印处理、初始化数据、权限元数据以及创建、处理、更新生命周期事件。
- aliyun、minio、tencent、storage、watermark 子包封装了具体存储和处理集成。
