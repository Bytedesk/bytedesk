# 收藏功能设计文档

## 概述

收藏功能参考微信收藏功能的实现，支持收藏聊天记录消息、会话和客户信息。该功能提供了完整的CRUD操作，支持分类、标签、置顶等特性。

## 功能特性

### 1. 收藏类型
- **THREAD**: 会话收藏 - 收藏整个会话
- **CUSTOMER**: 客户收藏 - 收藏客户信息
- **MESSAGE**: 消息收藏 - 收藏单条消息

### 2. 支持的消息类型
- **TEXT**: 文本消息
- **IMAGE**: 图片消息
- **FILE**: 文件消息
- **AUDIO/VOICE**: 语音消息
- **VIDEO**: 视频消息
- **MUSIC**: 音乐消息
- **LOCATION**: 位置消息
- **LINK**: 链接消息
- **CARD**: 卡片消息

### 3. 核心功能
- ✅ 创建收藏
- ✅ 查询收藏列表
- ✅ 更新收藏信息
- ✅ 删除收藏
- ✅ 置顶/取消置顶
- ✅ 分类管理
- ✅ 标签管理
- ✅ 搜索功能
- ✅ 按类型筛选

## 数据模型

### FavoriteEntity
```java
@Entity
@Table(name = "bytedesk_core_favorite")
public class FavoriteEntity extends BaseEntity {
    private String name;                    // 收藏名称/标题
    private String type;                    // 收藏类型
    private String content;                 // 收藏的消息内容
    private String messageType;             // 消息类型
    private String messageStatus;           // 消息状态
    private String messageSender;           // 消息发送人信息（JSON）
    private String messageReceiver;         // 消息接收人信息（JSON）
    private String threadInfo;              // 会话信息（JSON）
    private String messageChannel;          // 消息来源渠道
    private String messageExtra;            // 消息额外信息（JSON）
    private List<String> tagList;           // 收藏的标签列表
    private String description;             // 收藏描述/备注
    private String category;                // 收藏分类
    private Boolean isPinned;               // 是否置顶
    private String favoriteSource;          // 收藏来源
    private String originalMessageUid;      // 原始消息ID
    private String originalThreadUid;       // 原始会话ID
}
```

### FavoriteRequest
```java
public class FavoriteRequest extends BaseRequest {
    private String name;                    // 收藏名称
    private String favoriteType;            // 收藏类型
    private String favoriteContent;         // 收藏内容
    private String messageType;             // 消息类型
    private String messageStatus;           // 消息状态
    private String messageSender;           // 消息发送人
    private String messageReceiver;         // 消息接收人
    private String threadInfo;              // 会话信息
    private String messageChannel;          // 消息渠道
    private String messageExtra;            // 消息额外信息
    private List<String> tagList;           // 标签列表
    private String description;             // 描述
    private String category;                // 分类
    private Boolean isPinned;               // 是否置顶
    private String favoriteSource;          // 收藏来源
    private String originalMessageUid;      // 原始消息ID
    private String originalThreadUid;       // 原始会话ID
}
```

### FavoriteResponse
```java
public class FavoriteResponse extends BaseResponse {
    private String name;                    // 收藏名称
    private String type;                    // 收藏类型
    private String content;                 // 收藏内容
    private String messageType;             // 消息类型
    private String messageStatus;           // 消息状态
    private String messageSender;           // 消息发送人
    private String messageReceiver;         // 消息接收人
    private String threadInfo;              // 会话信息
    private String messageChannel;          // 消息渠道
    private String messageExtra;            // 消息额外信息
    private List<String> tagList;           // 标签列表
    private String description;             // 描述
    private String category;                // 分类
    private Boolean isPinned;               // 是否置顶
    private String favoriteSource;          // 收藏来源
    private String originalMessageUid;      // 原始消息ID
    private String originalThreadUid;       // 原始会话ID
}
```

## 使用示例

### 1. 创建消息收藏
```java
FavoriteRequest request = FavoriteRequest.builder()
    .name("重要消息")
    .favoriteType(FavoriteTypeEnum.MESSAGE.name())
    .favoriteContent("这是一条重要的消息内容")
    .messageType(MessageTypeEnum.TEXT.name())
    .messageSender("{\"uid\":\"user123\",\"name\":\"张三\"}")
    .messageReceiver("{\"uid\":\"user456\",\"name\":\"李四\"}")
    .threadInfo("{\"uid\":\"thread123\",\"name\":\"客服会话\"}")
    .messageChannel(ChannelEnum.WEB.name())
    .tagList(Arrays.asList("重要", "客户"))
    .description("客户反馈的重要信息")
    .category("客户反馈")
    .isPinned(true)
    .favoriteSource(FavoriteSourceEnum.MANUAL.name())
    .originalMessageUid("msg_123")
    .originalThreadUid("thread_123")
    .userUid("current_user_uid")
    .orgUid("current_org_uid")
    .build();

FavoriteResponse response = favoriteService.createFavorite(request);
```

### 2. 查询收藏列表
```java
// 查询用户的所有收藏
Pageable pageable = PageRequest.of(0, 20, Sort.by("isPinned").descending().and(Sort.by("createdAt").descending()));
Page<FavoriteResponse> favorites = favoriteService.getFavoritesByUser("user_uid", pageable);

// 查询指定类型的收藏
Page<FavoriteResponse> messageFavorites = favoriteService.getFavoritesByType("user_uid", FavoriteTypeEnum.MESSAGE.name(), pageable);
```

### 3. 搜索收藏
```java
Page<FavoriteResponse> searchResults = favoriteService.searchFavorites("user_uid", "重要", pageable);
```

### 4. 更新收藏
```java
FavoriteRequest updateRequest = FavoriteRequest.builder()
    .uid("favorite_uid")
    .name("更新后的名称")
    .description("更新后的描述")
    .category("新分类")
    .tagList(Arrays.asList("重要", "紧急"))
    .isPinned(true)
    .build();

FavoriteResponse updatedResponse = favoriteService.updateFavorite(updateRequest);
```

### 5. 置顶/取消置顶
```java
FavoriteResponse pinnedResponse = favoriteService.togglePinFavorite("favorite_uid", true);
```

### 6. 删除收藏
```java
favoriteService.deleteFavorite("favorite_uid");
```

## 工具类

### FavoriteUtils
提供了以下工具方法：
- `generateFavoriteName()`: 根据消息类型和内容生成收藏名称
- `isMessageTypeSupported()`: 验证消息类型是否支持收藏
- `getMessageTypeDisplayName()`: 获取消息类型的显示名称
- `getFavoriteTypeDisplayName()`: 获取收藏类型的显示名称
- `getFavoriteSourceDisplayName()`: 获取收藏来源的显示名称
- `validateFavoriteRequest()`: 验证收藏请求参数

## 枚举类型

### FavoriteTypeEnum
```java
public enum FavoriteTypeEnum {
    THREAD,    // 会话收藏
    CUSTOMER,  // 客户收藏
    MESSAGE    // 消息收藏
}
```

### FavoriteSourceEnum
```java
public enum FavoriteSourceEnum {
    MANUAL,    // 手动收藏
    AUTO,      // 自动收藏
    IMPORT,    // 导入收藏
    SHARE,     // 分享收藏
    FORWARD    // 转发收藏
}
```

## 数据库设计

### 表结构
```sql
CREATE TABLE bytedesk_core_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(255) UNIQUE NOT NULL,
    version INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE,
    org_uid VARCHAR(255),
    user_uid VARCHAR(255),
    level VARCHAR(50) DEFAULT 'ORGANIZATION',
    platform VARCHAR(50) DEFAULT 'BYTEDESK',
    
    name VARCHAR(500),
    favorite_type VARCHAR(50) DEFAULT 'MESSAGE',
    content TEXT,
    message_type VARCHAR(50) DEFAULT 'TEXT',
    message_status VARCHAR(50) DEFAULT 'SUCCESS',
    message_sender TEXT,
    message_receiver TEXT,
    thread_info TEXT,
    message_channel VARCHAR(50) DEFAULT 'WEB',
    message_extra TEXT,
    tag_list TEXT,
    description TEXT,
    category VARCHAR(100),
    is_pinned BOOLEAN DEFAULT FALSE,
    favorite_source VARCHAR(50) DEFAULT 'MANUAL',
    original_message_uid VARCHAR(255),
    original_thread_uid VARCHAR(255),
    
    INDEX idx_user_uid (user_uid),
    INDEX idx_org_uid (org_uid),
    INDEX idx_favorite_type (favorite_type),
    INDEX idx_message_type (message_type),
    INDEX idx_is_pinned (is_pinned),
    INDEX idx_original_message_uid (original_message_uid),
    INDEX idx_original_thread_uid (original_thread_uid)
);
```

## 注意事项

1. **字段冲突**: `FavoriteRequest` 中的 `type` 和 `content` 字段与 `BaseRequest` 冲突，已重命名为 `favoriteType` 和 `favoriteContent`
2. **JSON存储**: 复杂对象（如发送人、接收人、会话信息）使用JSON格式存储
3. **软删除**: 使用软删除机制，不会物理删除数据
4. **索引优化**: 为常用查询字段添加了数据库索引
5. **分页支持**: 所有查询都支持分页，默认按置顶状态和创建时间排序

## 扩展功能

### 1. 自动收藏
可以根据关键词、消息类型等条件自动收藏消息

### 2. 收藏分享
支持将收藏分享给其他用户或导出

### 3. 收藏同步
支持多端同步收藏数据

### 4. 收藏统计
提供收藏数量、类型分布等统计信息

### 5. 收藏备份
支持收藏数据的备份和恢复

## 相关文件

- `FavoriteEntity.java` - 收藏实体类
- `FavoriteRequest.java` - 收藏请求类
- `FavoriteResponse.java` - 收藏响应类
- `FavoriteTypeEnum.java` - 收藏类型枚举
- `FavoriteSourceEnum.java` - 收藏来源枚举
- `FavoriteUtils.java` - 收藏工具类
- `FavoriteService.java` - 收藏服务类 