/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-15 20:24:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:20:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.upload.aliyun;

import com.bytedesk.core.upload.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * 阿里云OSS上传服务实现
 *
 * @author bytedesk.com
 */
@Service("aliyunUploadService")
public class AliyunUploadService implements UploadService {

    @Autowired
    private AliyunOss aliyunOss;

    @Override
    public String uploadAttachment(MediaType mediaType, String fileName, int width, int height, String username, File file) {
        return aliyunOss.uploadAttachment(mediaType, fileName, width, height, username, file);
    }

    @Override
    public String uploadAvatar(String fileName, File file) {
        return aliyunOss.uploadAvatar(fileName, file);
    }

    @Override
    public String uploadImage(String fileName, File file) {
        return aliyunOss.uploadImage(fileName, file);
    }

    @Override
    public String uploadVoice(String fileName, File file) {
        return aliyunOss.uploadVoice(fileName, file);
    }

    @Override
    public String uploadVideo(String fileName, File file) {
        return aliyunOss.uploadVideo(fileName, file);
    }

    @Override
    public String uploadFile(String fileName, File file) {
        return aliyunOss.uploadFile(fileName, file);
    }

    @Override
    public String uploadIdcard(String fileName, File file) {
        return aliyunOss.uploadIdcard(fileName, file);
    }

    @Override
    public String uploadBusinessLicense(String fileName, File file) {
        return aliyunOss.uploadBusinessLicense(fileName, file);
    }

    @Override
    public String uploadEduLicense(String fileName, File file) {
        return aliyunOss.uploadEduLicense(fileName, file);
    }

    @Override
    public String uploadSchoolAlbum(String fileName, File file) {
        return aliyunOss.uploadSchoolAlbum(fileName, file);
    }

    @Override
    public String uploadSchoolSwiper(String fileName, File file) {
        return aliyunOss.uploadSchoolSwiper(fileName, file);
    }

    @Override
    public String uploadSchoolVideo(String fileName, File file) {
        return aliyunOss.uploadSchoolVideo(fileName, file);
    }

    @Override
    public String uploadVideoCover(String fileName, File file) {
        return aliyunOss.uploadVideoCover(fileName, file);
    }

    @Override
    public String uploadCourseCover(String fileName, File file) {
        return aliyunOss.uploadCourseCover(fileName, file);
    }

    @Override
    public String uploadP12(String fileName, String build, File file) {
        return aliyunOss.uploadP12(fileName, build, file);
    }

    @Override
    public String saveWeChatImageUrl(String fileName, String url) {
        return aliyunOss.saveWeChatImageUrl(fileName, url);
    }

    @Override
    public String saveWeChatInputStream(String fileName, InputStream inputStream) {
        return aliyunOss.saveWeChatInputStream(fileName, inputStream);
    }

    @Override
    public String saveWeChatAvatarUrl(String fileName, String url) {
        return aliyunOss.saveWeChatAvatarUrl(fileName, url);
    }

    @Override
    public String saveWeChatImage(String fileName, File file) {
        return aliyunOss.saveWeChatImage(fileName, file);
    }

    @Override
    public String saveWeChatVoiceUrl(String fileName, String url) {
        return aliyunOss.saveWeChatVoiceUrl(fileName, url);
    }

    @Override
    public String saveWeChatVoice(String fileName, File file) {
        return aliyunOss.saveWeChatVoice(fileName, file);
    }

    @Override
    public String saveWeChatVideoUrl(String fileName, String url) {
        return aliyunOss.saveWeChatVideoUrl(fileName, url);
    }

    @Override
    public String saveWeChatVideo(String fileName, File file) {
        return aliyunOss.saveWeChatVideo(fileName, file);
    }

    @Override
    public String saveWeChatVideoThumbUrl(String fileName, String url) {
        return aliyunOss.saveWeChatVideoThumbUrl(fileName, url);
    }

    @Override
    public String saveWeChatFileUrl(String fileName, String url) {
        return aliyunOss.saveWeChatFileUrl(fileName, url);
    }

    @Override
    public String saveWeChatFile(String fileName, File file) {
        return aliyunOss.saveWeChatFile(fileName, file);
    }

    @Override
    public String saveWeChatThumb(String fileName, File file) {
        return aliyunOss.saveWeChatThumb(fileName, file);
    }

    @Override
    public String uploadWeChatDb(String fileName, File file) {
        return aliyunOss.uploadWeChatDb(fileName, file);
    }

    @Override
    public String saveSchoolLogoUrl(String fileName, String url) {
        return aliyunOss.saveSchoolLogoUrl(fileName, url);
    }

    @Override
    public String saveCourseLogoUrl(String fileName, String url) {
        return aliyunOss.saveCourseLogoUrl(fileName, url);
    }
} 