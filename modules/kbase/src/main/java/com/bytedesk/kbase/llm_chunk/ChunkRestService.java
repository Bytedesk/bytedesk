/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 18:40:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:18:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_chunk;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.file.FileEntity;
import com.bytedesk.kbase.file.FileRestService;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm_chunk.event.ChunkUpdateDocEvent;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChunkRestService extends BaseRestServiceWithExport<ChunkEntity, ChunkRequest, ChunkResponse, ChunkExcel> {

    private final ChunkRepository chunkRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final KbaseRestService kbaseRestService;

    private final FileRestService fileRestService;
    
    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    protected Specification<ChunkEntity> createSpecification(ChunkRequest request) {
        return ChunkSpecification.search(request);
    }

    @Override
    protected Page<ChunkEntity> executePageQuery(Specification<ChunkEntity> spec, Pageable pageable) {
        return chunkRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "chunk", key = "#uid", unless = "#result==null")
    @Override
    public Optional<ChunkEntity> findByUid(String uid) {
        return chunkRepository.findByUid(uid);
    }

    // findByKbUid
    @Cacheable(value = "chunk", key = "#kbUid", unless = "#result==null")
    public List<ChunkEntity> findByKbUid(String kbUid) {
        return chunkRepository.findByKbase_UidAndDeletedFalse(kbUid);
    }

    @Override
    public ChunkResponse create(ChunkRequest request) {
        // log.info("ChunkRestService create: {}", request);
        ChunkEntity entity = ChunkEntity.builder()
                .uid(uidUtils.getUid())
                .name(request.getName())
                .content(request.getContent())
                // .type(request.getType())
                .docId(request.getDocId())
                // .typeUid(request.getTypeUid())
                .enabled(request.getEnabled())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .categoryUid(request.getCategoryUid())
                .orgUid(request.getOrgUid())
                .build();
        //
        UserEntity user = authService.getUser();
        if (user != null) {
            entity.setUserUid(user.getUid());
        } else {
            entity.setUserUid(request.getUserUid());
        }
        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(request.getKbUid());
        if (kbase.isPresent()) {
            entity.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        // 
        Optional<FileEntity> file = fileRestService.findByUid(request.getFileUid());
        if (file.isPresent()) {
            entity.setFile(file.get());
        } else {
            throw new RuntimeException("fileUid not found");
        }
        //
        ChunkEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create chunk failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public ChunkResponse update(ChunkRequest request) {
        //
        Optional<ChunkEntity> optional = chunkRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ChunkEntity entity = optional.get();
            // modelMapper.map(request, entity);
            // 判断必要的内容是否有变化，如果内容发生变化，发布UpdateDocEvent事件
            if (entity.hasChanged(request)) {
                // 发布事件，更新文档
                ChunkUpdateDocEvent chunkUpdateDocEvent = new ChunkUpdateDocEvent(entity);
                bytedeskEventPublisher.publishEvent(chunkUpdateDocEvent);
            }

            entity.setName(request.getName());
            entity.setContent(request.getContent());
            entity.setEnabled(request.getEnabled());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            entity.setCategoryUid(request.getCategoryUid());
            // entity.setType(request.getType());

            //
            ChunkEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update chunk failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Chunk not found");
        }
    }
    
    protected ChunkEntity doSave(ChunkEntity entity) {
        return chunkRepository.save(entity);
    }

    @Override
    public ChunkEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            ChunkEntity entity) {
        try {
            log.warn("处理乐观锁冲突: {}", entity.getUid());
            Optional<ChunkEntity> latest = chunkRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ChunkEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setName(entity.getName());
                latestEntity.setContent(entity.getContent());
                latestEntity.setType(entity.getType());
                // latestEntity.setTypeUid(entity.getTypeUid());
                latestEntity.setLevel(entity.getLevel());
                latestEntity.setPlatform(entity.getPlatform());
                latestEntity.setEnabled(entity.getEnabled());
                latestEntity.setStartDate(entity.getStartDate());
                latestEntity.setEndDate(entity.getEndDate());

                // 文档ID列表和状态
                // latestEntity.setStatus(entity.getStatus());
                latestEntity.setDocId(entity.getDocId());

                return chunkRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
    }

    public void deleteByDocList(List<String> docIdList) {
        // 遍历 docIdList
        for (String docId : docIdList) {
            // 查找 docId 对应的所有 chunk
            Optional<ChunkEntity> chunkList = chunkRepository.findByDocId(docId);
            // 删除
            chunkList.ifPresent(chunk -> {
                chunk.setDeleted(true);
                save(chunk);
            });
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ChunkEntity> optional = chunkRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        } else {
            throw new RuntimeException("Chunk not found");
        }
    }

    @Override
    public void delete(ChunkRequest request) {
        deleteByUid(request.getUid());
    }

    // deleteAll
    public void deleteAll(ChunkRequest request) {
        List<ChunkEntity> chunks = findByKbUid(request.getKbUid());
        if (chunks != null && !chunks.isEmpty()) {
            for (ChunkEntity chunk : chunks) {
                chunk.setDeleted(true);
                save(chunk);
            }
        }
    }

    // enable
    public ChunkResponse enable(ChunkRequest request) {
        Optional<ChunkEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            ChunkEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            // 
            ChunkEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update chunk failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Chunk not found");
        }
    }

    @Override
    public ChunkResponse convertToResponse(ChunkEntity entity) {
        return modelMapper.map(entity, ChunkResponse.class);
    }

    @Override
    public ChunkExcel convertToExcel(ChunkEntity chunk) {
        ChunkExcel excel = modelMapper.map(chunk, ChunkExcel.class);
        if (chunk.getEnabled()) {
            excel.setEnabled("是");
        } else {
            excel.setEnabled("否");
        }
        if (chunk.getFile() != null) {
            excel.setFileName(chunk.getFile().getFileName());
        }
        if (chunk.getKbase() != null) {
            excel.setKbaseName(chunk.getKbase().getName());
        }
        // 将状态和向量状态转换为中文
        excel.setStatus(ChunkStatusEnum.toChineseDisplay(chunk.getElasticStatus()));
        excel.setVectorStatus(ChunkStatusEnum.toChineseDisplay(chunk.getVectorStatus()));
        return excel;
    }

    public void initChunk(String kbUid, String orgUid) {
        if (chunkRepository.count() > 0) {
            return;
        }
        //
        ChunkEntity chunk = ChunkEntity.builder()
                .uid(uidUtils.getUid())
                .name("测试chunk")
                .content("测试chunk内容")
                .enabled(true)
                .startDate(BdDateUtils.now())
                .endDate(BdDateUtils.now().plusDays(1))
                .orgUid(orgUid)
                .build();
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(kbUid);
        if (kbase.isPresent()) {
            chunk.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        //
        save(chunk);
    }
}
