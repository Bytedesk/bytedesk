package com.bytedesk.kbase.llm.file;

import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 文件Excel导出服务
 */
@Slf4j
@Service
public class FileExcelService {

    /**
     * 导出文件列表到Excel
     * 
     * @param files 文件列表
     * @param outputStream 输出流
     */
    public void exportToExcel(List<FileEntity> files, OutputStream outputStream) {
        try {
            List<FileExcel> excelData = files.stream()
                .map(this::convertToExcel)
                .collect(Collectors.toList());

            EasyExcel.write(outputStream, FileExcel.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("文件列表")
                .doWrite(excelData);

            log.info("Successfully exported {} files to Excel", files.size());
        } catch (Exception e) {
            log.error("Failed to export files to Excel", e);
            throw new RuntimeException("Failed to export files to Excel", e);
        }
    }

    /**
     * 将文件实体转换为Excel对象
     */
    private FileExcel convertToExcel(FileEntity entity) {
        FileExcel excel = new FileExcel();
        
        excel.setFileName(entity.getFileName());
        excel.setContent(entity.getContent());
        excel.setFileUrl(entity.getFileUrl());
        excel.setStatus(entity.getStatus());
        // excel.setLevel(entity.getLevel());
        // excel.setPlatform(entity.getPlatform());
        // excel.setCategoryUid(entity.getCategoryUid());
        // excel.setKbUid(entity.getKbase().getUid());
        // excel.setUploadUid(entity.getUploadUid());
        // excel.setUserUid(entity.getUserUid());
        // excel.setDocIdList(String.join(",", entity.getDocIdList()));
        excel.setCreatedAt(entity.getCreatedAt().toString());
        excel.setUpdatedAt(entity.getUpdatedAt().toString());

        return excel;
    }
} 