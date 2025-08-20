/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-20 15:21:01
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.file;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.utils.ConvertUtils;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.kbase.file.event.FileUpdateDocEvent;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.bytedesk.kbase.utils.KbaseConvertUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FileRestService extends BaseRestServiceWithExport<FileEntity, FileRequest, FileResponse, FileExcel> {

    private final FileRepository fileRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final KbaseRestService kbaseRestService;

    private final UploadRestService uploadRestService;
    
    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    protected Specification<FileEntity> createSpecification(FileRequest request) {
        return FileSpecification.search(request);
    }

    @Override
    protected Page<FileEntity> executePageQuery(Specification<FileEntity> spec, Pageable pageable) {
        return fileRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "file", key = "#uid", unless = "#result==null")
    @Override
    public Optional<FileEntity> findByUid(String uid) {
        return fileRepository.findByUid(uid);
    }

    @Cacheable(value = "file", key = "#kbUid", unless = "#result==null")
    public List<FileEntity> findByKbUid(String kbUid) {
        return fileRepository.findByKbase_UidAndDeletedFalse(kbUid);
    }

    @Override
    public FileResponse create(FileRequest request) {

        FileEntity entity = modelMapper.map(request, FileEntity.class);
        entity.setUid(uidUtils.getUid());

        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(request.getKbUid());
        if (kbase.isPresent()) {
            entity.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        // 
        Optional<UploadEntity> upload = uploadRestService.findByUid(request.getUploadUid());
        if (upload.isPresent()) {
            entity.setUpload(upload.get());
        } else {
            throw new RuntimeException("uploadUid not found");
        }

        FileEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create file failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public FileResponse update(FileRequest request) {
        Optional<FileEntity> optional = fileRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            FileEntity entity = optional.get();
            
            // 判断文件内容是否有变化，如果有变化，发布UpdateDocEvent事件
            if (entity.hasChanged(request)) {
                // 发布事件，更新文档
                FileUpdateDocEvent fileUpdateDocEvent = new FileUpdateDocEvent(entity);
                bytedeskEventPublisher.publishEvent(fileUpdateDocEvent);
            }
            
            // modelMapper.map(request, entity);
            entity.setFileName(request.getFileName());
            entity.setContent(request.getContent());
            // entity.setTagList(request.getTagList());
            // entity.setFileUrl(request.getFileUrl());
            // entity.setContent(request.getContent());
            // entity.setEnabled(request.getEnabled());
            // entity.setStartDate(request.getStartDate());
            // entity.setEndDate(request.getEndDate());
            // entity.setCategoryUid(request.getCategoryUid());
            //
            FileEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update file failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("File not found");
        }
    }

    @CachePut(value = "file", key = "#entity.uid")
    @Override
    protected FileEntity doSave(FileEntity entity) {
        return fileRepository.save(entity);
    }

    @Override
    public FileEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            FileEntity entity) {
        try {
            Optional<FileEntity> latest = fileRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                FileEntity latestEntity = latest.get();
                // 合并需要保留的数据
                latestEntity.setFileName(entity.getFileName());
                latestEntity.setContent(entity.getContent());
                latestEntity.setTagList(entity.getTagList());
                latestEntity.setFileUrl(entity.getFileUrl());
                latestEntity.setContent(entity.getContent());
                latestEntity.setEnabled(entity.getEnabled());
                latestEntity.setStartDate(entity.getStartDate());
                latestEntity.setEndDate(entity.getEndDate());
                latestEntity.setCategoryUid(entity.getCategoryUid());
                latestEntity.setOrgUid(entity.getOrgUid());
                latestEntity.setUserUid(entity.getUserUid());
                // latestEntity.setKbUid(entity.getKbUid());
                // latestEntity.setUploadUid(entity.getUploadUid());

                // 文档ID列表和状态
                latestEntity.setDocIdList(entity.getDocIdList());
                latestEntity.setElasticStatus(entity.getElasticStatus());

                return fileRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
    }

    @CacheEvict(value = "file", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<FileEntity> optional = fileRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // fileRepository.delete(optional.get());
        } else {
            throw new RuntimeException("File not found");
        }
    }

    @Override
    public void delete(FileRequest request) {
        deleteByUid(request.getUid());
    }

    // deleteAll
    public void deleteAll(FileRequest request) {
        List<FileEntity> files = findByKbUid(request.getKbUid());
        if (files != null && !files.isEmpty()) {
            for (FileEntity file : files) {
                file.setDeleted(true);
                save(file);
            }
        }
    }

    // enable/disable file
    public FileResponse enable(FileRequest request) { 
        Optional<FileEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            FileEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            // 
            FileEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Enable/Disable file failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("File not found");
        }
    }

    @Override
    public FileResponse convertToResponse(FileEntity entity) {
        FileResponse response = modelMapper.map(entity, FileResponse.class);
        response.setKbase(KbaseConvertUtils.convertToKbaseResponse(entity.getKbase()));
        response.setUpload(ConvertUtils.convertToUploadResponse(entity.getUpload()));
        return response;
    }

    @Override
    public FileExcel convertToExcel(FileEntity file) {
        FileExcel excel = modelMapper.map(file, FileExcel.class);
        if (file.getEnabled()) {
            excel.setEnabled("是");
        } else {
            excel.setEnabled("否");
        }
        if (file.getKbase()!= null) {
            excel.setKbaseName(file.getKbase().getName());
        }
        // 将状态和向量状态转换为中文
        excel.setStatus(ChunkStatusEnum.toChineseDisplay(file.getElasticStatus()));
        excel.setVectorStatus(ChunkStatusEnum.toChineseDisplay(file.getVectorStatus()));
        return excel;
    }

    public void initFile(String kbUid, String orgUid) {
        if (fileRepository.count() > 0) {
            return;
        }

        // FileEntity entity = FileEntity.builder()
        //         .uid(uidUtils.getUid())
        //         .fileName("文件")
        //         .fileUrl("https://www.bytedesk.com")
        //         .content("文件内容")
        //         .enabled(true)
        //         .startDate(BdDateUtils.now())
        //         .endDate(BdDateUtils.now().plusDays(1))
        //         .orgUid(orgUid)
        //         .build();
        // Optional<KbaseEntity> kbase = kbaseRestService.findByUid(kbUid);
        // if (kbase.isPresent()) {
        //     entity.setKbase(kbase.get());
        // } else {
        //     throw new RuntimeException("kbaseUid not found");
        // }
        // //
        // save(entity);
    }

}
