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
package com.bytedesk.core.upload;

import org.springframework.http.MediaType;

import java.io.File;
import java.io.InputStream;

/**
 * 统一上传服务接口
 * 支持阿里云OSS和腾讯云COS
 *
 * @author bytedesk.com
 */
public interface UploadService {

    /**
     * 上传附件
     */
    String uploadAttachment(MediaType mediaType, String fileName, int width, int height, String username, File file);

    /**
     * 上传头像
     */
    String uploadAvatar(String fileName, File file);

    /**
     * 上传图片
     */
    String uploadImage(String fileName, File file);

    /**
     * 上传语音
     */
    String uploadVoice(String fileName, File file);

    /**
     * 上传视频
     */
    String uploadVideo(String fileName, File file);

    /**
     * 上传文件
     */
    String uploadFile(String fileName, File file);

    /**
     * 上传身份证
     */
    String uploadIdcard(String fileName, File file);

    /**
     * 上传营业执照
     */
    String uploadBusinessLicense(String fileName, File file);

    /**
     * 上传教育许可证
     */
    String uploadEduLicense(String fileName, File file);

    /**
     * 上传学校相册
     */
    String uploadSchoolAlbum(String fileName, File file);

    /**
     * 上传学校轮播图
     */
    String uploadSchoolSwiper(String fileName, File file);

    /**
     * 上传学校视频
     */
    String uploadSchoolVideo(String fileName, File file);

    /**
     * 上传视频封面
     */
    String uploadVideoCover(String fileName, File file);

    /**
     * 上传课程封面
     */
    String uploadCourseCover(String fileName, File file);

    /**
     * 上传p12文件
     */
    String uploadP12(String fileName, String build, File file);

    /**
     * 保存微信图片URL
     */
    String saveWeChatImageUrl(String fileName, String url);

    /**
     * 保存微信输入流
     */
    String saveWeChatInputStream(String fileName, InputStream inputStream);

    /**
     * 保存微信头像URL
     */
    String saveWeChatAvatarUrl(String fileName, String url);

    /**
     * 保存微信图片
     */
    String saveWeChatImage(String fileName, File file);

    /**
     * 保存微信语音URL
     */
    String saveWeChatVoiceUrl(String fileName, String url);

    /**
     * 保存微信语音
     */
    String saveWeChatVoice(String fileName, File file);

    /**
     * 保存微信视频URL
     */
    String saveWeChatVideoUrl(String fileName, String url);

    /**
     * 保存微信视频
     */
    String saveWeChatVideo(String fileName, File file);

    /**
     * 保存微信视频缩略图URL
     */
    String saveWeChatVideoThumbUrl(String fileName, String url);

    /**
     * 保存微信文件URL
     */
    String saveWeChatFileUrl(String fileName, String url);

    /**
     * 保存微信文件
     */
    String saveWeChatFile(String fileName, File file);

    /**
     * 保存微信缩略图
     */
    String saveWeChatThumb(String fileName, File file);

    /**
     * 上传微信数据库
     */
    String uploadWeChatDb(String fileName, File file);

    /**
     * 保存学校Logo URL
     */
    String saveSchoolLogoUrl(String fileName, String url);

    /**
     * 保存课程Logo URL
     */
    String saveCourseLogoUrl(String fileName, String url);

} 