# 统一上传服务

本项目提供了统一的上传服务，支持阿里云OSS和腾讯云COS两种云存储提供商。

## 功能特性

- 支持阿里云OSS和腾讯云COS
- 统一的API接口，便于切换云存储提供商
- 支持多种文件类型上传（头像、图片、视频、文件等）
- 支持微信相关文件上传
- 支持学校和教育相关文件上传
- 工厂模式，可根据配置动态选择提供商

## 配置说明

### 1. 阿里云OSS配置

```properties
# 阿里云OSS启用开关
bytedesk.aliyun.enabled=true
# 阿里云OSS配置
aliyun.region.id=cn-hangzhou
aliyun.access.key.id=your_access_key_id
aliyun.access.key.secret=your_access_key_secret
aliyun.oss.endpoint=https://oss-cn-shenzhen.aliyuncs.com
aliyun.oss.base.url=https://your-bucket.oss-cn-shenzhen.aliyuncs.com
aliyun.oss.bucket.name=your-bucket-name
aliyun.oss.img.domain=oss.yourdomain.com
```

### 2. 腾讯云COS配置

```properties
# 腾讯云COS启用开关
bytedesk.tencent.enabled=false
# 腾讯云COS配置
tencent.bucket.location=ap-shanghai
tencent.bucket.name=your-bucket-name
tencent.bucket.domain=https://your-bucket-name.cos.ap-shanghai.myqcloud.com
tencent.appid=your_app_id
tencent.secretid=your_secret_id
tencent.secretkey=your_secret_key
```

### 3. 统一上传配置

```properties
# 上传提供商: aliyun(阿里云OSS) 或 tencent(腾讯云COS)
bytedesk.upload.provider=aliyun
# 上传目录前缀
bytedesk.upload.dir.prefix=
```

## 使用方法

### 1. 注入上传服务

```java
@Autowired
private UploadServiceFactory uploadServiceFactory;

// 获取上传服务实例
UploadService uploadService = uploadServiceFactory.getUploadService();
```

### 2. 上传文件

```java
// 上传头像
String avatarUrl = uploadService.uploadAvatar(fileName, file);

// 上传图片
String imageUrl = uploadService.uploadImage(fileName, file);

// 上传文件
String fileUrl = uploadService.uploadFile(fileName, file);

// 上传附件
String attachmentUrl = uploadService.uploadAttachment(mediaType, fileName, width, height, username, file);
```

### 3. 微信相关上传

```java
// 保存微信图片URL
String wechatImageUrl = uploadService.saveWeChatImageUrl(fileName, url);

// 保存微信输入流
String wechatInputStreamUrl = uploadService.saveWeChatInputStream(fileName, inputStream);

// 保存微信头像URL
String wechatAvatarUrl = uploadService.saveWeChatAvatarUrl(fileName, url);
```

### 4. 学校相关上传

```java
// 上传身份证
String idcardUrl = uploadService.uploadIdcard(fileName, file);

// 上传营业执照
String businessLicenseUrl = uploadService.uploadBusinessLicense(fileName, file);

// 上传教育许可证
String eduLicenseUrl = uploadService.uploadEduLicense(fileName, file);

// 上传学校相册
String schoolAlbumUrl = uploadService.uploadSchoolAlbum(fileName, file);
```

### 5. 动态切换提供商

```java
// 切换为腾讯云COS
uploadServiceFactory.setUploadProvider("tencent");

// 切换为阿里云OSS
uploadServiceFactory.setUploadProvider("aliyun");

// 获取当前提供商
String currentProvider = uploadServiceFactory.getCurrentProvider();
```

## API接口

### 上传接口

- `POST /api/v1/upload/avatar` - 上传头像
- `POST /api/v1/upload/image` - 上传图片
- `POST /api/v1/upload/file` - 上传文件
- `POST /api/v1/upload/attachment` - 上传附件

### 管理接口

- `GET /api/v1/upload/provider` - 获取当前上传提供商
- `POST /api/v1/upload/provider/{provider}` - 切换上传提供商

## 文件结构

```
com.bytedesk.core.upload/
├── UploadService.java              # 统一上传服务接口
├── UploadServiceFactory.java       # 上传服务工厂类
├── UploadController.java           # 上传控制器示例
├── aliyun/
│   ├── AliyunConfig.java          # 阿里云配置类
│   ├── AliyunProperties.java      # 阿里云属性类
│   ├── AliyunOss.java             # 阿里云OSS工具类
│   └── AliyunUploadService.java   # 阿里云上传服务实现
└── tencent/
    ├── TencentConfig.java         # 腾讯云配置类
    ├── TencentProperties.java     # 腾讯云属性类
    ├── TencentCos.java            # 腾讯云COS工具类
    └── TencentUploadService.java  # 腾讯云上传服务实现
```

## 注意事项

1. 确保在配置文件中正确设置云存储的访问密钥和配置信息
2. 腾讯云COS需要在bucket下创建相应的文件夹结构
3. 阿里云OSS需要配置正确的endpoint和bucket信息
4. 上传的文件会自动生成唯一的文件名，避免文件名冲突
5. 临时文件会在上传完成后自动删除
6. 使用前需要启用相应的云存储服务（设置 `bytedesk.aliyun.enabled=true` 或 `bytedesk.tencent.enabled=true`）
7. 只能启用一个云存储服务，避免冲突

## 扩展说明

如需添加新的云存储提供商，只需：

1. 创建新的配置类和属性类
2. 实现具体的上传工具类
3. 实现UploadService接口
4. 在UploadServiceFactory中添加新的提供商支持

这样可以保持代码的一致性和可扩展性。 