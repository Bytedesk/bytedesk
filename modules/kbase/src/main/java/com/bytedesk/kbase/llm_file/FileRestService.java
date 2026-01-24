/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-11 18:25:45
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-05 15:47:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.llm_file;

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
import org.springframework.ai.document.Document;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.upload.UploadEntity;
import com.bytedesk.core.upload.UploadRestService;
import com.bytedesk.core.utils.ConvertUtils;
// import com.bytedesk.core.config.BytedeskEventPublisher;
// import com.bytedesk.kbase.llm_file.event.FileUpdateDocEvent;
import com.bytedesk.kbase.kbase.KbaseEntity;
import com.bytedesk.kbase.kbase.KbaseRestService;
import com.bytedesk.kbase.llm_chunk.ChunkStatusEnum;
import com.bytedesk.kbase.llm_chunk.ChunkRestService;
import com.bytedesk.kbase.utils.KbaseConvertUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileRestService extends BaseRestServiceWithExport<FileEntity, FileRequest, FileResponse, FileExcel> {

    private final FileRepository fileRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final KbaseRestService kbaseRestService;

    private final UploadRestService uploadRestService;
    
    private final FileService fileService;

    private final ChunkRestService chunkRestService;

    private final FileChunkService fileChunkService;

    @Override
    protected Specification<FileEntity> createSpecification(FileRequest request) {
        return FileSpecification.search(request, authService);
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
            
            // modelMapper.map(request, entity);
            entity.setFileName(request.getFileName());
            entity.setContent(request.getContent());
            entity.setTagList(request.getTagList());
            entity.setFileUrl(request.getFileUrl());
            entity.setContent(request.getContent());
            entity.setEnabled(request.getEnabled());
            entity.setStartDate(request.getStartDate());
            entity.setEndDate(request.getEndDate());
            entity.setCategoryUid(request.getCategoryUid());
            entity.setChunkingStrategy(request.getChunkingStrategy());
            entity.setChunkSize(request.getChunkSize());
            entity.setChunkOverlap(request.getChunkOverlap());

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

    /**
     * 重新对文件进行chunk切分
     * @param fileUid 文件UID
     * @return 切分结果
     */
    public FileResponse rechunkFile(String fileUid) {
        Optional<FileEntity> fileOptional = findByUid(fileUid);
        if (!fileOptional.isPresent()) {
            throw new RuntimeException("File not found: " + fileUid);
        }
        
        FileEntity file = fileOptional.get();
        
        // 删除原有的chunk记录
        if (file.getDocIdList() != null && !file.getDocIdList().isEmpty()) {
            chunkRestService.deleteByDocList(file.getDocIdList());
        }
        
        // 重新解析文件内容
        UploadEntity upload = file.getUpload();
        if (upload == null) {
            throw new RuntimeException("Upload entity not found for file: " + fileUid);
        }
        
        // 重新解析文档
        List<Document> documents = fileService.parseFileContent(upload);
        
        // 更新文件内容
        int maxContentLength = 60000; // 设置最大内容长度
        String content = fileService.extractContentSummary(documents, maxContentLength);
        file.setContent(content);
        
        // 重置状态
        file.setElasticStatus(ChunkStatusEnum.NEW.name());
        file.setVectorStatus(ChunkStatusEnum.NEW.name());
        file.setDocIdList(null);
        
        // 保存文件
        FileEntity savedFile = save(file);
        
        // 重新进行chunk切分
        FileResponse fileResponse = convertToResponse(savedFile);
        
        // 根据文档数量选择处理方式
        if (FileChunkUtils.shouldUseAsyncProcessing(documents)) {
            // 大文件使用异步处理
            log.info("重新切分大文件({} 个文档)使用异步处理: {}", 
                    documents.size(), file.getFileName());
            
            fileChunkService.processFileChunksAsync(documents, fileResponse)
                .thenAccept(docIdList -> {
                    log.info("重新切分异步处理完成: {}, 生成chunks: {}", file.getFileName(), docIdList.size());
                    // 更新文件的docIdList
                    savedFile.setDocIdList(docIdList);
                    savedFile.setElasticSuccess();
                    savedFile.setVectorSuccess();
                    save(savedFile);
                })
                .exceptionally(throwable -> {
                    log.error("重新切分异步处理失败: {}", throwable.getMessage(), throwable);
                    // 标记文件处理失败
                    savedFile.setElasticError();
                    savedFile.setVectorError();
                    save(savedFile);
                    return null;
                });
            
            // 对于异步处理，返回处理中状态
            savedFile.setElasticStatus(ChunkStatusEnum.PROCESSING.name());
            savedFile.setVectorStatus(ChunkStatusEnum.PROCESSING.name());
            save(savedFile);
            
        } else {
            // 小文件使用同步处理
            log.info("重新切分小文件({} 个文档)使用同步处理: {}", 
                    documents.size(), file.getFileName());
            
            List<String> docIdList = fileChunkService.processFileChunks(documents, fileResponse);
            
            // 更新文件的docIdList
            savedFile.setDocIdList(docIdList);
            save(savedFile);
        }
        
        return convertToResponse(savedFile);
    }

    public void initFile(String kbUid, String orgUid) {
    }

}
