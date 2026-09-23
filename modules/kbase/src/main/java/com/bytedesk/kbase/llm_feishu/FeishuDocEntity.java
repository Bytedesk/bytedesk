/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-09-20 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_feishu;

import java.util.ArrayList;
import java.util.List;

import com.bytedesk.core.base.BaseEntity;
import com.bytedesk.core.constant.TypeConsts;
import com.bytedesk.core.converter.StringListConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * 飞书云文档同步实体：一篇文章库 wiki 节点 / 云盘文件同步后的本地落库
 * 唯一键：feishuUid + resourceType + objToken（软删除行可复活，不产生唯一冲突）
 */
@Entity
@Data
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners({FeishuDocEntityListener.class})
@Table(name = "bytedesk_kbase_feishu_doc", indexes = {
        @Index(name = "idx_feishu_doc_org_uid", columnList = "org_uid"),
        @Index(name = "idx_feishu_doc_kb_uid", columnList = "kb_uid"),
        @Index(name = "idx_feishu_doc_feishu_uid", columnList = "feishu_uid")
})
public class FeishuDocEntity extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 所属飞书应用配置（channels/douyin FeishuEntity.uid） */
    @Column(name = "feishu_uid")
    private String feishuUid;

    /** 目标知识库 UID（D3：存字符串，不建 @ManyToOne） */
    @Column(name = "kb_uid")
    private String kbUid;

    /** 可选分类 */
    @Column(name = "category_uid")
    private String categoryUid;

    /** 资源类型：WIKI_NODE / DRIVE_FILE，禁止仅靠 token 推断来源 */
    @Column(name = "resource_type")
    private String resourceType;

    /** wiki 空间 ID */
    @Column(name = "space_id")
    private String spaceId;

    /** wiki 节点 token */
    @Column(name = "node_token")
    private String nodeToken;

    /** 云盘文件夹 token（drive 同步时使用） */
    @Column(name = "folder_token")
    private String folderToken;

    /** 飞书对象 token（docx/doc/sheet/bitable/file 等） */
    @Column(name = "obj_token")
    private String objToken;

    /** 飞书对象类型：docx/doc/sheet/bitable/file/mindnote/slides/board */
    @Column(name = "obj_type")
    private String objType;

    /** 文档标题 */
    @Column(name = "title")
    private String title;

    /** 正文内容（Markdown/纯文本，检索主字段） */
    @Column(name = "content", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String content;

    /** 飞书原文 URL（不保存短期导出下载 URL） */
    @Column(name = "source_url")
    private String sourceUrl;

    /** 飞书创建时间（Unix 秒） */
    @Column(name = "obj_create_time")
    private Long objCreateTime;

    /** 飞书编辑时间（Unix 秒，增量游标依据） */
    @Column(name = "obj_edit_time")
    private Long objEditTime;

    /** 规范化正文 SHA-256，用于无变化跳过索引和幂等更新 */
    @Column(name = "content_hash")
    private String contentHash;

    /** 最近同步批次 UID */
    @Column(name = "last_sync_batch_uid")
    private String lastSyncBatchUid;

    /** 最近一次同步错误摘要 */
    @Column(name = "last_sync_error", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private String lastSyncError;

    /** 同步状态（FeishuDocStatusEnum） */
    @Builder.Default
    @Column(name = "sync_status")
    private String syncStatus = FeishuDocStatusEnum.NEW.name();

    /** 全文索引状态（FeishuDocStatusEnum） */
    @Builder.Default
    @Column(name = "elastic_status")
    private String elasticStatus = FeishuDocStatusEnum.NEW.name();

    /** 向量索引状态（FeishuDocStatusEnum） */
    @Builder.Default
    @Column(name = "vector_status")
    private String vectorStatus = FeishuDocStatusEnum.NEW.name();

    /** 向量库文档 ID 列表（分块后多个文档） */
    @Builder.Default
    @Convert(converter = StringListConverter.class)
    @Column(name = "doc_id_list", columnDefinition = TypeConsts.COLUMN_TYPE_TEXT)
    private List<String> docIdList = new ArrayList<>();

    /** 是否启用（禁用则不入索引/检索） */
    @Builder.Default
    @Column(name = "is_enabled")
    private Boolean enabled = true;

    public FeishuDocEntity setElasticSuccess() {
        this.elasticStatus = FeishuDocStatusEnum.SUCCESS.name();
        return this;
    }

    public FeishuDocEntity setElasticError() {
        this.elasticStatus = FeishuDocStatusEnum.ERROR.name();
        return this;
    }

    public FeishuDocEntity setVectorSuccess() {
        this.vectorStatus = FeishuDocStatusEnum.SUCCESS.name();
        return this;
    }

    public FeishuDocEntity setVectorError() {
        this.vectorStatus = FeishuDocStatusEnum.ERROR.name();
        return this;
    }
}
