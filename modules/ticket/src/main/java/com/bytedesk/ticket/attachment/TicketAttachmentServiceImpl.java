package com.bytedesk.ticket.attachment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.bytedesk.ticket.attachment.exception.AttachmentException;
import com.bytedesk.ticket.comment.config.CommentConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TicketAttachmentServiceImpl implements TicketAttachmentService {

    @Autowired
    private TicketAttachmentRepository attachmentRepository;
    
    @Autowired
    private CommentConfig commentConfig;
    
    @Value("${bytedesk.upload.dir}")
    private String uploadPath;
    
    @Value("${bytedesk.upload.url}")
    private String uploadUrl;

    @Override
    @Transactional
    public TicketAttachmentEntity saveTicketAttachment(Long ticketId, MultipartFile file) {
        validateAttachment(file);
        
        try {
            String filename = generateFilename(file.getOriginalFilename());
            String storagePath = saveFile(file, filename);
            String downloadUrl = generateDownloadUrl(filename);
            
            TicketAttachmentEntity attachment = new TicketAttachmentEntity();
            attachment.setTicketId(ticketId);
            attachment.setFilename(file.getOriginalFilename());
            attachment.setContentType(file.getContentType());
            attachment.setSize(file.getSize());
            attachment.setStoragePath(storagePath);
            attachment.setDownloadUrl(downloadUrl);
            
            // 如果是图片，生成缩略图
            if (isImage(file.getContentType())) {
                attachment.setThumbnail(generateThumbnail(storagePath));
            }
            
            return attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new AttachmentException("Failed to save attachment", e);
        }
    }

    @Override
    @Transactional
    public TicketAttachmentEntity saveCommentAttachment(Long ticketId, Long commentId, MultipartFile file) {
        TicketAttachmentEntity attachment = saveTicketAttachment(ticketId, file);
        attachment.setCommentId(commentId);
        return attachmentRepository.save(attachment);
    }

    @Override
    public List<TicketAttachmentEntity> getTicketAttachments(Long ticketId) {
        // return attachmentRepository.findByTicketId(ticketId);
        return null;
    }

    @Override
    public List<TicketAttachmentEntity> getCommentAttachments(Long commentId) {
        // return attachmentRepository.findByCommentId(commentId);
        return null;
    }

    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId) {
        TicketAttachmentEntity attachment = getAttachment(attachmentId);
        
        // 删除物理文件
        try {
            Files.deleteIfExists(Paths.get(attachment.getStoragePath()));
            if (attachment.getThumbnail() != null) {
                Files.deleteIfExists(Paths.get(getThumbnailPath(attachment.getStoragePath())));
            }
        } catch (IOException e) {
            log.error("Failed to delete attachment file", e);
        }
        
        // 删除数据库记录
        attachmentRepository.delete(attachment);
    }

    @Override
    @Transactional
    public void deleteTicketAttachments(Long ticketId) {
        getTicketAttachments(ticketId).forEach(attachment -> 
            deleteAttachment(attachment.getId()));
    }

    @Override
    @Transactional
    public void deleteCommentAttachments(Long commentId) {
        getCommentAttachments(commentId).forEach(attachment -> 
            deleteAttachment(attachment.getId()));
    }

    @Override
    public TicketAttachmentEntity getAttachment(Long attachmentId) {
        // return attachmentRepository.findById(attachmentId)
        //     .orElseThrow(() -> new AttachmentNotFoundException(attachmentId));
        return null;
    }

    @Override
    public byte[] getAttachmentContent(Long attachmentId) {
        TicketAttachmentEntity attachment = getAttachment(attachmentId);
        try {
            return Files.readAllBytes(Paths.get(attachment.getStoragePath()));
        } catch (IOException e) {
            throw new AttachmentException("Failed to read attachment content", e);
        }
    }

    @Override
    public String generateThumbnail(Long attachmentId) {
        TicketAttachmentEntity attachment = getAttachment(attachmentId);
        if (!isImage(attachment.getContentType())) {
            return null;
        }
        return generateThumbnail(attachment.getStoragePath());
    }

    @Override
    public void validateAttachment(MultipartFile file) {
        // 检查文件大小
        if (file.getSize() > commentConfig.getMaxAttachmentSize()) {
            throw new AttachmentException("File size exceeds limit");
        }
        
        // 检查文件类型
        String contentType = file.getContentType();
        if (!Arrays.asList(commentConfig.getAllowedAttachmentTypes()).contains(contentType)) {
            throw new AttachmentException("File type not allowed");
        }
    }

    private String generateFilename(String originalFilename) {
        String extension = StringUtils.getFilenameExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String saveFile(MultipartFile file, String filename) throws IOException {
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        return filePath.toString();
    }

    private String generateDownloadUrl(String filename) {
        return uploadUrl + "/" + filename;
    }

    private boolean isImage(String contentType) {
        return contentType != null && contentType.startsWith("image/");
    }

    private String generateThumbnail(String imagePath) {
        // TODO: 实现缩略图生成逻辑
        return null;
    }

    private String getThumbnailPath(String originalPath) {
        return originalPath + "_thumb";
    }
} 