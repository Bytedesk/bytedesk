# 图片水印功能使用说明

## 功能概述

本模块为上传的图片自动添加水印功能，支持文字水印和图片水印，可以灵活配置水印的位置、颜色、大小等参数。

## 主要特性

- ✅ 支持文字水印和图片水印
- ✅ 支持多种水印位置（左上角、右上角、左下角、右下角、中心）
- ✅ 支持自定义水印颜色和透明度
- ✅ 支持自定义字体和字体大小
- ✅ 支持图片尺寸过滤（只对特定尺寸的图片添加水印）
- ✅ 支持客户端控制是否添加水印
- ✅ 支持自定义水印参数
- ✅ 自动错误处理（水印添加失败时保存原图）

## 配置说明

### 1. 启用水印功能

在 `application.yml` 中添加以下配置：

```yaml
bytedesk:
  watermark:
    # 是否启用水印功能
    enabled: true
    
    # 水印文字
    text: "bytedesk.com"
    
    # 水印位置: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER
    position: BOTTOM_RIGHT
    
    # 字体大小
    fontSize: 24
    
    # 字体名称
    fontName: "Arial"
    
    # 水印颜色 (RGBA格式: R,G,B,A)
    color: "255,255,255,128"
    
    # 透明度 (0.0-1.0)
    opacity: 0.5
    
    # 边距
    margin: 20
    
    # 是否只对图片文件添加水印
    imageOnly: true
    
    # 最小图片尺寸（像素），小于此尺寸的图片不添加水印
    minImageSize: 100
    
    # 最大图片尺寸（像素），大于此尺寸的图片不添加水印
    maxImageSize: 5000
```

### 2. 水印位置说明

- `TOP_LEFT`: 左上角
- `TOP_RIGHT`: 右上角
- `BOTTOM_LEFT`: 左下角
- `BOTTOM_RIGHT`: 右下角
- `CENTER`: 中心

### 3. 颜色格式说明

颜色使用RGBA格式，例如：
- `"255,255,255,128"`: 白色半透明
- `"0,0,0,255"`: 黑色不透明
- `"255,0,0,100"`: 红色半透明

## API 使用说明

### 1. 基本文件上传（使用默认水印配置）

```http
POST /visitor/api/v1/upload/file
Content-Type: multipart/form-data

file: [图片文件]
fileName: "example.jpg"
fileType: "image/jpeg"
```

### 2. 带水印控制的文件上传

```http
POST /visitor/api/v1/upload/file/watermark
Content-Type: multipart/form-data

file: [图片文件]
fileName: "example.jpg"
fileType: "image/jpeg"
addWatermark: true
watermarkText: "自定义水印文字"
watermarkPosition: "BOTTOM_RIGHT"
```

### 3. 请求参数说明

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | 是 | 要上传的文件 |
| fileName | String | 是 | 文件名 |
| fileType | String | 是 | 文件类型 |
| addWatermark | Boolean | 否 | 是否添加水印，默认true |
| watermarkText | String | 否 | 自定义水印文字 |
| watermarkPosition | String | 否 | 水印位置 |

## 代码示例

### 1. 使用 WatermarkService 直接添加水印

```java
@Autowired
private WatermarkService watermarkService;

public void addWatermarkExample() {
    // 读取原始图片
    BufferedImage originalImage = ImageIO.read(new File("original.jpg"));
    
    // 添加文字水印
    byte[] watermarkedImageBytes = watermarkService.addTextWatermark(
        originalImage, 
        "bytedesk.com", 
        WatermarkService.WatermarkPosition.BOTTOM_RIGHT
    );
    
    // 保存水印图片
    try (InputStream inputStream = new ByteArrayInputStream(watermarkedImageBytes)) {
        Files.copy(inputStream, Paths.get("watermarked.png"), StandardCopyOption.REPLACE_EXISTING);
    }
}
```

### 2. 使用 UploadRestService 上传带水印的图片

```java
@Autowired
private UploadRestService uploadService;

public void uploadWithWatermark() {
    UploadRequest request = UploadRequest.builder()
        .fileName("example.jpg")
        .fileType("image/jpeg")
        .addWatermark(true)
        .watermarkText("自定义水印")
        .watermarkPosition("BOTTOM_RIGHT")
        .build();
    
    String fileUrl = uploadService.store(multipartFile, "example.jpg", request);
}
```

## 注意事项

1. **性能考虑**: 水印处理会增加图片上传的时间，建议只对需要保护的图片启用水印。

2. **图片格式**: 水印功能支持常见的图片格式（JPG、PNG、GIF等）。

3. **错误处理**: 如果水印添加失败，系统会自动保存原图，确保上传功能不受影响。

4. **内存使用**: 大图片处理时会占用较多内存，建议设置合理的图片尺寸限制。

5. **字体支持**: 确保系统安装了配置的字体，否则会使用默认字体。

## 扩展功能

### 1. 添加图片水印

```java
// 读取水印图片
BufferedImage watermarkImage = ImageIO.read(new File("watermark.png"));

// 添加图片水印
byte[] watermarkedImageBytes = watermarkService.addImageWatermark(
    originalImage, 
    watermarkImage, 
    WatermarkService.WatermarkPosition.BOTTOM_RIGHT, 
    0.5f
);
```

### 2. 自定义水印样式

```java
// 自定义颜色和字体大小
byte[] watermarkedImageBytes = watermarkService.addTextWatermark(
    originalImage, 
    "自定义水印", 
    WatermarkService.WatermarkPosition.CENTER,
    32,  // 字体大小
    new Color(255, 0, 0, 128)  // 红色半透明
);
```

## 故障排除

### 1. 水印不显示
- 检查水印功能是否启用（`enabled: true`）
- 检查图片尺寸是否在允许范围内
- 检查水印颜色是否与背景色相近

### 2. 水印位置不正确
- 检查 `position` 配置是否正确
- 检查 `margin` 设置是否合理

### 3. 水印文字模糊
- 检查字体是否安装
- 尝试调整字体大小
- 检查抗锯齿设置

### 4. 上传失败
- 检查文件格式是否支持
- 检查文件大小是否超限
- 查看日志中的错误信息 