/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-22 18:40:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 16:23:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_image;

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
import com.bytedesk.kbase.llm_file.FileEntity;
import com.bytedesk.kbase.llm_file.FileRepository;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm_image.event.ImageUpdateDocEvent;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageRestService extends BaseRestServiceWithExport<ImageEntity, ImageRequest, ImageResponse, ImageExcel> {

    private final ImageRepository imageRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final KbaseRestService kbaseRestService;

    private final FileRepository fileRepository;
    
    private final BytedeskEventPublisher bytedeskEventPublisher;

    @Override
    protected Specification<ImageEntity> createSpecification(ImageRequest request) {
        return ImageSpecification.search(request, authService);
    }

    @Override
    protected Page<ImageEntity> executePageQuery(Specification<ImageEntity> spec, Pageable pageable) {
        return imageRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "image", key = "#uid", unless = "#result==null")
    @Override
    public Optional<ImageEntity> findByUid(String uid) {
        return imageRepository.findByUid(uid);
    }

    // findByKbUid
    @Cacheable(value = "image", key = "#kbUid", unless = "#result==null")
    public List<ImageEntity> findByKbUid(String kbUid) {
        return imageRepository.findByKbase_UidAndDeletedFalse(kbUid);
    }
    
    // findByFileUid
    @Cacheable(value = "image", key = "#fileUid", unless = "#result==null")
    public List<ImageEntity> findByFileUid(String fileUid) {
        return imageRepository.findByFile_UidAndDeletedFalse(fileUid);
    }

    @Override
    public ImageResponse create(ImageRequest request) {
        // log.info("ImageRestService create: {}", request);
        ImageEntity entity = ImageEntity.builder()
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
        Optional<FileEntity> file = fileRepository.findByUid(request.getFileUid());
        if (file.isPresent()) {
            entity.setFile(file.get());
        } else {
            throw new RuntimeException("fileUid not found");
        }
        //
        ImageEntity savedEntity = save(entity);
        if (savedEntity == null) {
            throw new RuntimeException("Create image failed");
        }
        return convertToResponse(savedEntity);
    }

    @Override
    public ImageResponse update(ImageRequest request) {
        //
        Optional<ImageEntity> optional = imageRepository.findByUid(request.getUid());
        if (optional.isPresent()) {
            ImageEntity entity = optional.get();
            // modelMapper.map(request, entity);
            // 判断必要的内容是否有变化，如果内容发生变化，发布UpdateDocEvent事件
            if (entity.hasChanged(request)) {
                // 发布事件，更新文档
                ImageUpdateDocEvent imageUpdateDocEvent = new ImageUpdateDocEvent(entity);
                bytedeskEventPublisher.publishEvent(imageUpdateDocEvent);
            }

            entity.setName(request.getName());
            entity.setContent(request.getContent());
            entity.setTagList(request.getTagList());
            entity.setEnabled(request.getEnabled());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            entity.setCategoryUid(request.getCategoryUid());
            // entity.setType(request.getType());

            //
            ImageEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update image failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Image not found");
        }
    }
    
    protected ImageEntity doSave(ImageEntity entity) {
        return imageRepository.save(entity);
    }

    @Override
    public ImageEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            ImageEntity entity) {
        try {
            log.warn("处理乐观锁冲突: {}", entity.getUid());
            Optional<ImageEntity> latest = imageRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                ImageEntity latestEntity = latest.get();
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

                return imageRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("无法处理乐观锁冲突: {}", ex.getMessage(), ex);
        }
        throw new RuntimeException("无法解决实体版本冲突: " + entity.getUid());
    }

    public void deleteByDocList(List<String> docIdList) {
        // 遍历 docIdList
        for (String docId : docIdList) {
            // 查找 docId 对应的所有 image
            Optional<ImageEntity> imageList = imageRepository.findByDocId(docId);
            // 删除
            imageList.ifPresent(image -> {
                image.setDeleted(true);
                save(image);
            });
        }
    }

    @Override
    public void deleteByUid(String uid) {
        Optional<ImageEntity> optional = imageRepository.findByUid(uid);
        if (optional.isPresent()) {
            optional.get().setDeleted(true);
            save(optional.get());
        } else {
            throw new RuntimeException("Image not found");
        }
    }

    @Override
    public void delete(ImageRequest request) {
        deleteByUid(request.getUid());
    }

    // deleteAll
    public void deleteAll(ImageRequest request) {
        List<ImageEntity> images = findByKbUid(request.getKbUid());
        if (images != null && !images.isEmpty()) {
            for (ImageEntity image : images) {
                image.setDeleted(true);
                save(image);
            }
        }
    }

    // enable
    public ImageResponse enable(ImageRequest request) {
        Optional<ImageEntity> optional = findByUid(request.getUid());
        if (optional.isPresent()) {
            ImageEntity entity = optional.get();
            entity.setEnabled(request.getEnabled());
            // 
            ImageEntity savedEntity = save(entity);
            if (savedEntity == null) {
                throw new RuntimeException("Update image failed");
            }
            return convertToResponse(savedEntity);
        } else {
            throw new RuntimeException("Image not found");
        }
    }

    @Override
    public ImageResponse convertToResponse(ImageEntity entity) {
        return modelMapper.map(entity, ImageResponse.class);
    }

    @Override
    public ImageExcel convertToExcel(ImageEntity image) {
        ImageExcel excel = modelMapper.map(image, ImageExcel.class);
        if (image.getEnabled()) {
            excel.setEnabled("是");
        } else {
            excel.setEnabled("否");
        }
        if (image.getFile() != null) {
            excel.setFileName(image.getFile().getFileName());
        }
        if (image.getKbase() != null) {
            excel.setKbaseName(image.getKbase().getName());
        }
        // 将状态和向量状态转换为中文
        excel.setStatus(ImageStatusEnum.toChineseDisplay(image.getElasticStatus()));
        excel.setVectorStatus(ImageStatusEnum.toChineseDisplay(image.getVectorStatus()));
        return excel;
    }

    public void initImage(String kbUid, String orgUid) {
        if (imageRepository.count() > 0) {
            return;
        }
        //
        ImageEntity image = ImageEntity.builder()
                .uid(uidUtils.getUid())
                .name("测试image")
                .content("测试image内容")
                .enabled(true)
                .startDate(BdDateUtils.now())
                .endDate(BdDateUtils.now().plusDays(1))
                .orgUid(orgUid)
                .build();
        Optional<KbaseEntity> kbase = kbaseRestService.findByUid(kbUid);
        if (kbase.isPresent()) {
            image.setKbase(kbase.get());
        } else {
            throw new RuntimeException("kbaseUid not found");
        }
        //
        save(image);
    }
}
