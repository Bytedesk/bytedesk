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
package com.bytedesk.core.upload.tencent;

import com.bytedesk.core.upload.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * 腾讯云COS上传服务实现
 *
 * @author bytedesk.com
 */
@Service("tencentUploadService")
public class TencentUploadService implements UploadService {

    @Autowired
    private TencentCos tencentCos;

    @Override
    public String uploadAttachment(MediaType mediaType, String fileName, int width, int height, String username, File file) {
        return tencentCos.uploadAttachment(mediaType, fileName, width, height, username, file);
    }

    @Override
    public String uploadAvatar(String fileName, File file) {
        return tencentCos.uploadAvatar(fileName, file);
    }

    @Override
    public String uploadImage(String fileName, File file) {
        return tencentCos.uploadImage(fileName, file);
    }

    @Override
    public String uploadVoice(String fileName, File file) {
        return tencentCos.uploadVoice(fileName, file);
    }

    @Override
    public String uploadVideo(String fileName, File file) {
        return tencentCos.uploadVideo(fileName, file);
    }

    @Override
    public String uploadFile(String fileName, File file) {
        return tencentCos.uploadFile(fileName, file);
    }

    @Override
    public String uploadIdcard(String fileName, File file) {
        return tencentCos.uploadIdcard(fileName, file);
    }

    @Override
    public String uploadBusinessLicense(String fileName, File file) {
        return tencentCos.uploadBusinessLicense(fileName, file);
    }

    @Override
    public String uploadEduLicense(String fileName, File file) {
        return tencentCos.uploadEduLicense(fileName, file);
    }

    @Override
    public String uploadSchoolAlbum(String fileName, File file) {
        return tencentCos.uploadSchoolAlbum(fileName, file);
    }

    @Override
    public String uploadSchoolSwiper(String fileName, File file) {
        return tencentCos.uploadSchoolSwiper(fileName, file);
    }

    @Override
    public String uploadSchoolVideo(String fileName, File file) {
        return tencentCos.uploadSchoolVideo(fileName, file);
    }

    @Override
    public String uploadVideoCover(String fileName, File file) {
        return tencentCos.uploadVideoCover(fileName, file);
    }

    @Override
    public String uploadCourseCover(String fileName, File file) {
        return tencentCos.uploadCourseCover(fileName, file);
    }

    @Override
    public String uploadP12(String fileName, String build, File file) {
        return tencentCos.uploadP12(fileName, build, file);
    }

    @Override
    public String saveWeChatImageUrl(String fileName, String url) {
        return tencentCos.saveWeChatImageUrl(fileName, url);
    }

    @Override
    public String saveWeChatInputStream(String fileName, InputStream inputStream) {
        return tencentCos.saveWeChatInputStream(fileName, inputStream);
    }

    @Override
    public String saveWeChatAvatarUrl(String fileName, String url) {
        return tencentCos.saveWeChatAvatarUrl(fileName, url);
    }

    @Override
    public String saveWeChatImage(String fileName, File file) {
        return tencentCos.saveWeChatImage(fileName, file);
    }

    @Override
    public String saveWeChatVoiceUrl(String fileName, String url) {
        return tencentCos.saveWeChatVoiceUrl(fileName, url);
    }

    @Override
    public String saveWeChatVoice(String fileName, File file) {
        return tencentCos.saveWeChatVoice(fileName, file);
    }

    @Override
    public String saveWeChatVideoUrl(String fileName, String url) {
        return tencentCos.saveWeChatVideoUrl(fileName, url);
    }

    @Override
    public String saveWeChatVideo(String fileName, File file) {
        return tencentCos.saveWeChatVideo(fileName, file);
    }

    @Override
    public String saveWeChatVideoThumbUrl(String fileName, String url) {
        return tencentCos.saveWeChatVideoThumbUrl(fileName, url);
    }

    @Override
    public String saveWeChatFileUrl(String fileName, String url) {
        return tencentCos.saveWeChatFileUrl(fileName, url);
    }

    @Override
    public String saveWeChatFile(String fileName, File file) {
        return tencentCos.saveWeChatFile(fileName, file);
    }

    @Override
    public String saveWeChatThumb(String fileName, File file) {
        return tencentCos.saveWeChatThumb(fileName, file);
    }

    @Override
    public String uploadWeChatDb(String fileName, File file) {
        return tencentCos.uploadWeChatDb(fileName, file);
    }

    @Override
    public String saveSchoolLogoUrl(String fileName, String url) {
        return tencentCos.saveSchoolLogoUrl(fileName, url);
    }

    @Override
    public String saveCourseLogoUrl(String fileName, String url) {
        return tencentCos.saveCourseLogoUrl(fileName, url);
    }
} 