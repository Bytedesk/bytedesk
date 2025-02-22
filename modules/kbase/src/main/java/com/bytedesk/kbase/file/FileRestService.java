/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-20 11:17:49
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.file;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.uid.UidUtils;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FileRestService extends BaseRestService<FileEntity, FileRequest, FileResponse> {

    private final FileRepository fileRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    @Override
    public Page<FileResponse> queryByOrg(FileRequest request) {
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.ASC,
                "updatedAt");
        Specification<FileEntity> spec = FileSpecification.search(request);
        Page<FileEntity> page = fileRepository.findAll(spec, pageable);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<FileResponse> queryByUser(FileRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Cacheable(value = "file", key = "#uid", unless="#result==null")
    @Override
    public Optional<FileEntity> findByUid(String uid) {
        return fileRepository.findByUid(uid);
    }

    @Override
    public FileResponse create(FileRequest request) {
        
        FileEntity entity = modelMapper.map(request, FileEntity.class);
        entity.setUid(uidUtils.getUid());

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
        }
        else {
            throw new RuntimeException("File not found");
        }
    }

    @Override
    public FileEntity save(FileEntity entity) {
        try {
            return fileRepository.save(entity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<FileEntity> optional = fileRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
            // fileRepository.delete(optional.get());
        }
        else {
            throw new RuntimeException("File not found");
        }
    }

    @Override
    public void delete(FileRequest request) {
        deleteByUid(request.getUid());
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, FileEntity entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

    @Override
    public FileResponse convertToResponse(FileEntity entity) {
        return modelMapper.map(entity, FileResponse.class);
    }
    
}
