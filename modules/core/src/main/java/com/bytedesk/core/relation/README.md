# 社交关系功能设计文档

## 概述

社交关系功能参考小红书等社交平台的设计，提供完整的社交关系管理功能，包括关注、粉丝、点赞、收藏、分享等社交互动功能。该功能支持用户关系、内容关系、互动关系等多种关系类型。

## 功能特性

### 1. 关系类型

- **基础关系**: THREAD(会话)、CUSTOMER(客户)、TICKET(工单)
- **社交关系**: FOLLOW(关注)、FAN(粉丝)、FRIEND(好友)、LIKE(点赞)、FAVORITE(收藏)、SHARE(分享)、COMMENT(评论)
- **内容关系**: ARTICLE(文章)、NOTE(笔记)、VIDEO(视频)、IMAGE(图片)、AUDIO(音频)
- **互动关系**: MENTION(提及)、REPLY(回复)、QUOTE(引用)、REPOST(转发)
- **商业关系**: BRAND(品牌)、PRODUCT(产品)、SERVICE(服务)、PROMOTION(推广)
- **组织关系**: GROUP(群组)、TEAM(团队)、ORGANIZATION(组织)、COMMUNITY(社区)

### 2. 核心功能

- ✅ 创建社交关系
- ✅ 查询关系列表
- ✅ 更新关系信息
- ✅ 删除关系
- ✅ 关系状态管理
- ✅ 互动数据统计
- ✅ 关系质量评分
- ✅ 分页查询支持
- ✅ 关系验证

## 数据模型

### RelationEntity

```java
@Entity
@Table(name = "bytedesk_core_relation")
public class RelationEntity extends BaseEntity {
    // 基础字段
    private String name;                    // 关系名称
    private String description;             // 关系描述
    private String type;                    // 关系类型
    private String color;                   // 显示颜色
    private Integer order;                  // 显示顺序
    
    // 社交关系字段
    private String subjectUserUid;          // 关系主体用户ID
    private String objectUserUid;           // 关系客体用户ID
    private String objectContentUid;        // 关系客体内容ID
    private String status;                  // 关系状态
    private Integer weight;                 // 关系权重
    private List<String> tagList;           // 关系标签
    private String category;                // 关系分类
    private String subtype;                 // 关系子类型
    
    // 互动数据字段
    private Integer likeCount;              // 点赞数量
    private Integer favoriteCount;          // 收藏数量
    private Integer shareCount;             // 分享数量
    private Integer commentCount;           // 评论数量
    private Integer viewCount;              // 浏览数量
    private Integer followCount;            // 关注数量
    private Integer fanCount;               // 粉丝数量
    
    // 时间字段
    private ZonedDateTime relationStartTime;    // 关系开始时间
    private ZonedDateTime relationEndTime;      // 关系结束时间
    private ZonedDateTime lastInteractionTime;  // 最后互动时间
    
    // 设置字段
    private Boolean isPinned;               // 是否置顶
    private Boolean isPublic;               // 是否公开
    private Boolean isMutual;               // 是否互相关注
    private Boolean isSpecial;              // 是否特别关注
    private Boolean isMuted;                // 是否免打扰
    
    // 扩展字段
    private String remark;                  // 关系备注
    private String source;                  // 关系来源
    private String extra;                   // 额外信息(JSON)
    private String metadata;                // 元数据(JSON)
    
    // 统计字段
    private Integer interactionFrequency;   // 互动频率
    private Integer relationDurationDays;   // 关系持续天数
    private Integer relationQualityScore;   // 关系质量评分
}
```

### RelationRequest

```java
public class RelationRequest extends BaseRequest {
    // 基础字段
    private String name;                    // 关系名称
    private String description;             // 关系描述
    private String color;                   // 显示颜色
    private Integer order;                  // 显示顺序
    
    // 社交关系字段
    private String subjectUserUid;          // 关系主体用户ID
    private String objectUserUid;           // 关系客体用户ID
    private String objectContentUid;        // 关系客体内容ID
    private String status;                  // 关系状态
    private Integer weight;                 // 关系权重
    private List<String> tagList;           // 关系标签
    private String category;                // 关系分类
    private String subtype;                 // 关系子类型
    
    // 互动数据字段
    private Integer likeCount;              // 点赞数量
    private Integer favoriteCount;          // 收藏数量
    private Integer shareCount;             // 分享数量
    private Integer commentCount;           // 评论数量
    private Integer viewCount;              // 浏览数量
    private Integer followCount;            // 关注数量
    private Integer fanCount;               // 粉丝数量
    
    // 时间字段
    private String relationStartTime;       // 关系开始时间
    private String relationEndTime;         // 关系结束时间
    private String lastInteractionTime;     // 最后互动时间
    
    // 设置字段
    private Boolean isPinned;               // 是否置顶
    private Boolean isPublic;               // 是否公开
    private Boolean isMutual;               // 是否互相关注
    private Boolean isSpecial;              // 是否特别关注
    private Boolean isMuted;                // 是否免打扰
    
    // 扩展字段
    private String remark;                  // 关系备注
    private String source;                  // 关系来源
    private String extra;                   // 额外信息
    private String metadata;                // 元数据
    
    // 统计字段
    private Integer interactionFrequency;   // 互动频率
    private Integer relationDurationDays;   // 关系持续天数
    private Integer relationQualityScore;   // 关系质量评分
}
```

### RelationResponse

```java
public class RelationResponse extends BaseResponse {
    // 与 RelationRequest 相同的字段，但时间字段使用 ZonedDateTime 类型
    private ZonedDateTime relationStartTime;    // 关系开始时间
    private ZonedDateTime relationEndTime;      // 关系结束时间
    private ZonedDateTime lastInteractionTime;  // 最后互动时间
}
```

## 使用示例

### 1. 创建关注关系

```java
RelationRequest request = RelationRequest.builder()
    .name("关注关系")
    .type(RelationTypeEnum.FOLLOW.name())
    .subjectUserUid("user_123")
    .objectUserUid("user_456")
    .status(RelationStatusEnum.ACTIVE.name())
    .isPublic(true)
    .source("推荐")
    .userUid("user_123")
    .orgUid("org_123")
    .build();

RelationResponse response = relationService.create(request);
```

### 2. 创建点赞关系

```java
RelationRequest request = RelationRequest.builder()
    .name("点赞关系")
    .type(RelationTypeEnum.LIKE.name())
    .subjectUserUid("user_123")
    .objectContentUid("content_789")
    .status(RelationStatusEnum.ACTIVE.name())
    .isPublic(true)
    .source("内容浏览")
    .userUid("user_123")
    .orgUid("org_123")
    .build();

RelationResponse response = relationService.create(request);
```

### 3. 查询用户关注列表

```java
Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
Page<RelationResponse> followings = relationService.findFollowingsByUserId("user_123", pageable);
```

### 4. 查询用户粉丝列表

```java
Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
Page<RelationResponse> followers = relationService.findFollowersByUserId("user_123", pageable);
```

### 5. 检查关注状态

```java
Boolean isFollowing = relationService.isFollowing("user_123", "user_456");
```

### 6. 检查点赞状态

```java
Boolean hasLiked = relationService.hasLiked("user_123", "content_789");
```

### 7. 统计用户数据

```java
Long followingCount = relationService.countFollowingsByUserId("user_123");
Long followerCount = relationService.countFollowersByUserId("user_123");
```

### 8. 统计内容数据

```java
Long likeCount = relationService.countLikesByContentId("content_789");
Long favoriteCount = relationService.countFavoritesByContentId("content_789");
```

## 工具类

### RelationUtils

提供了以下工具方法：

- `generateRelationName()`: 根据关系类型生成关系名称
- `calculateRelationDurationDays()`: 计算关系持续时间
- `calculateRelationQualityScore()`: 计算关系质量评分
- `validateRelationRequest()`: 验证关系请求参数
- `getRelationTypeDisplayName()`: 获取关系类型显示名称
- `getRelationStatusDisplayName()`: 获取关系状态显示名称
- `isSocialRelation()`: 判断是否为社交关系
- `isContentRelation()`: 判断是否为内容关系
- `isRelationStatusValid()`: 判断关系状态是否有效
- `formatInteractionCount()`: 格式化互动数量显示
- `generateRelationDescription()`: 生成关系描述

## 枚举类型

### RelationTypeEnum

```java
public enum RelationTypeEnum {
    // 基础关系类型
    THREAD, CUSTOMER, TICKET,
    
    // 社交关系类型
    FOLLOW, FAN, FRIEND, LIKE, FAVORITE, SHARE, COMMENT,
    
    // 内容关系类型
    ARTICLE, NOTE, VIDEO, IMAGE, AUDIO,
    
    // 互动关系类型
    MENTION, REPLY, QUOTE, REPOST,
    
    // 商业关系类型
    BRAND, PRODUCT, SERVICE, PROMOTION,
    
    // 组织关系类型
    GROUP, TEAM, ORGANIZATION, COMMUNITY;
}
```

### RelationStatusEnum

```java
public enum RelationStatusEnum {
    ACTIVE,     // 活跃
    INACTIVE,   // 非活跃
    BLOCKED,    // 屏蔽
    DELETED,    // 删除
    PENDING,    // 待处理
    REJECTED,   // 拒绝
    EXPIRED,    // 过期
    SUSPENDED;  // 暂停
}
```

## 数据库设计

### 表结构

```sql
CREATE TABLE bytedesk_core_relation (
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
    
    -- 基础字段
    name VARCHAR(500),
    description TEXT,
    relation_type VARCHAR(50) DEFAULT 'CUSTOMER',
    relation_color VARCHAR(20) DEFAULT 'red',
    relation_order INT DEFAULT 0,
    
    -- 社交关系字段
    subject_user_uid VARCHAR(255),
    object_user_uid VARCHAR(255),
    object_content_uid VARCHAR(255),
    relation_status VARCHAR(50) DEFAULT 'ACTIVE',
    relation_weight INT DEFAULT 50,
    tag_list TEXT,
    category VARCHAR(100),
    relation_subtype VARCHAR(50),
    
    -- 互动数据字段
    like_count INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    share_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    view_count INT DEFAULT 0,
    follow_count INT DEFAULT 0,
    fan_count INT DEFAULT 0,
    
    -- 时间字段
    relation_start_time TIMESTAMP,
    relation_end_time TIMESTAMP,
    last_interaction_time TIMESTAMP,
    
    -- 设置字段
    is_pinned BOOLEAN DEFAULT FALSE,
    is_public BOOLEAN DEFAULT TRUE,
    is_mutual BOOLEAN DEFAULT FALSE,
    is_special BOOLEAN DEFAULT FALSE,
    is_muted BOOLEAN DEFAULT FALSE,
    
    -- 扩展字段
    remark TEXT,
    relation_source VARCHAR(50),
    relation_extra TEXT,
    relation_metadata TEXT,
    
    -- 统计字段
    interaction_frequency INT DEFAULT 0,
    relation_duration_days INT DEFAULT 0,
    relation_quality_score INT DEFAULT 50,
    
    -- 索引
    INDEX idx_user_uid (user_uid),
    INDEX idx_org_uid (org_uid),
    INDEX idx_relation_type (relation_type),
    INDEX idx_subject_user_uid (subject_user_uid),
    INDEX idx_object_user_uid (object_user_uid),
    INDEX idx_object_content_uid (object_content_uid),
    INDEX idx_relation_status (relation_status),
    INDEX idx_is_pinned (is_pinned),
    INDEX idx_is_public (is_public),
    INDEX idx_is_mutual (is_mutual),
    INDEX idx_relation_start_time (relation_start_time),
    INDEX idx_last_interaction_time (last_interaction_time)
);
```

## 注意事项

1. **关系唯一性**: 同一主体用户对同一客体用户或内容的关系类型应该是唯一的
2. **数据一致性**: 关注关系需要维护互相关注状态
3. **性能优化**: 为常用查询字段添加了数据库索引
4. **软删除**: 使用软删除机制，不会物理删除数据
5. **分页支持**: 所有查询都支持分页，默认按创建时间排序
6. **数据验证**: 提供了完整的参数验证机制

## 扩展功能

### 1. 关系推荐

基于用户行为和关系网络推荐新的关系

### 2. 关系分析

提供关系网络分析、影响力分析等功能

### 3. 关系同步

支持多端同步关系数据

### 4. 关系备份

支持关系数据的备份和恢复

### 5. 关系统计

提供详细的关系统计报表

## 相关文件

- `RelationEntity.java` - 关系实体类
- `RelationRequest.java` - 关系请求类
- `RelationResponse.java` - 关系响应类
- `RelationTypeEnum.java` - 关系类型枚举
- `RelationStatusEnum.java` - 关系状态枚举
- `RelationUtils.java` - 关系工具类
- `RelationRepository.java` - 关系数据访问层
- `RelationRestService.java` - 关系服务层
- `RelationRestController.java` - 关系控制器层
