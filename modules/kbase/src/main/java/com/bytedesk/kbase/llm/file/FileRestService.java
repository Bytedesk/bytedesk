/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-23 17:03:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm.file;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FileRestService extends BaseRestServiceWithExcel<FileEntity, FileRequest, FileResponse, FileExcel> {

    private final FileRepository fileRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final KbaseRestService kbaseRestService;

    @Override
    public Page<FileEntity> queryByOrgEntity(FileRequest request) {
        Pageable pageable = request.getPageable();
        Specification<FileEntity> spec = FileSpecification.search(request);
        return fileRepository.findAll(spec, pageable);
    }

    @Override
    public Page<FileResponse> queryByOrg(FileRequest request) {
        Page<FileEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FileResponse> queryByUser(FileRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "file", key = "#uid", unless = "#result==null")
    @Override
    public Optional<FileEntity> findByUid(String uid) {
        return fileRepository.findByUid(uid);
    }

    @Override
    public FileResponse create(FileRequest request) {

        FileEntity entity = modelMapper.map(request, FileEntity.class);
        entity.setUid(uidUtils.getUid());

        //
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(request.getKbUid());
        if (kbase.isPresent()) {
            entity.setKbaseEntity(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
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
            modelMapper.map(request, entity);
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

    @Override
    public FileEntity save(FileEntity entity) {
        try {
            return doSave(entity);
        } catch (ObjectOptimisticLockingFailureException e) {
            return handleOptimisticLockingFailureException(e, entity);
        }
    }

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
                latestEntity.setEnabled(entity.isEnabled());
                latestEntity.setStartDate(entity.getStartDate());
                latestEntity.setEndDate(entity.getEndDate());
                latestEntity.setCategoryUid(entity.getCategoryUid());
                latestEntity.setOrgUid(entity.getOrgUid());
                latestEntity.setUserUid(entity.getUserUid());
                // latestEntity.setKbUid(entity.getKbUid());
                latestEntity.setUploadUid(entity.getUploadUid());

                // 文档ID列表和状态
                latestEntity.setDocIdList(entity.getDocIdList());
                latestEntity.setStatus(entity.getStatus());

                return fileRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
    }

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

    @Override
    public FileResponse convertToResponse(FileEntity entity) {
        return modelMapper.map(entity, FileResponse.class);
    }

    @Override
    public FileExcel convertToExcel(FileEntity file) {
        return modelMapper.map(file, FileExcel.class);
    }

}
