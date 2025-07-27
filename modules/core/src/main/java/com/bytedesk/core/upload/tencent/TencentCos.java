package com.bytedesk.core.upload.tencent;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.ObjectMetadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 腾讯云COS对象存储服务工具类
 *
 * 参考
 * @see <a href="https://console.cloud.tencent.com/cos5/bucket">腾讯云后台</a>
 *
 *      腾讯云官方文档
 * @see <a href="https://cloud.tencent.com/document/product/436/10199">腾讯云COS Java SDK</a>
 *
 * @author bytedesk.com
 */
@Component
public class TencentCos {
    
    // @Autowired
    // AttachmentService attachmentService;

    private String uploadDirPrefix = "";

    @Autowired
    private TencentProperties tencentProperties;

    @Autowired
    private COSClient cosClient;

    public String uploadAttachment(MediaType mediaType, String fileName, int width, int height, String username, File file) {

        String folder = uploadDirPrefix + "attachments/";
        // return uploadCommon(folder, fileName, file);
        // PutObjectRequest putObjectRequest =
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, file);

        String url = tencentProperties.getBucketDomain() + "/" + folder + fileName;

        // attachmentService.create(url, mediaType, fileName, file.length(), width, height, username);

        return url;
    }

    public String uploadAvatar(String fileName, File file) {

        String folder = uploadDirPrefix + "avatars/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadImage(String fileName, File file) {

        String folder = uploadDirPrefix + "images/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadVoice(String fileName, File file) {

        String folder = uploadDirPrefix + "voices/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadVideo(String fileName, File file) {

        String folder = uploadDirPrefix + "videos/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadFile(String fileName, File file) {

        String folder = uploadDirPrefix + "files6/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadIdcard(String fileName, File file) {

        String folder = uploadDirPrefix + "school/idcard/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadBusinessLicense(String fileName, File file) {

        String folder = uploadDirPrefix + "school/businesslicense/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadEduLicense(String fileName, File file) {

        String folder = uploadDirPrefix + "school/edulicense/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadSchoolAlbum(String fileName, File file) {

        String folder = uploadDirPrefix + "school/album/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadSchoolSwiper(String fileName, File file) {

        String folder = uploadDirPrefix + "school/swiper/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadSchoolVideo(String fileName, File file) {

        String folder = uploadDirPrefix + "school/video/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadVideoCover(String fileName, File file) {

        String folder = uploadDirPrefix + "school/cover/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadCourseCover(String fileName, File file) {

        String folder = uploadDirPrefix + "course/cover/";
        return uploadCommon(folder, fileName, file);
    }

    /**
     * 上传p12文件
     * @param fileName 文件名
     * @param build debug or release
     * @param file file
     * @return string
     */
    public String uploadP12(String fileName, String build, File file) {

        String folder = build.equals("debug") ? "apns/development/" : "apns/production/";
        return uploadCommon(folder, fileName, file);
    }

    public String saveWeChatImageUrl(String fileName, String url) {
        //
        String folder = "wechat/images/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, inputStream, metadata);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    // 保存小程序码
    public String saveWeChatInputStream(String fileName, InputStream inputStream) {
        //
        String folder = "wechat/images/";
        ObjectMetadata metadata = new ObjectMetadata();
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, inputStream, metadata);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatAvatarUrl(String fileName, String url) {

        String folder = "wechat/avatars/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, inputStream, metadata);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatImage(String fileName, File file) {

        String folder = "wechat/images/";
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, file);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatVoiceUrl(String fileName, String url) {

        String folder = "wechat/voices/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, inputStream, metadata);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatVoice(String fileName, File file) {

        String folder = "wechat/voices/";
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, file);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatVideoUrl(String fileName, String url) {

        String folder = "wechat/videos/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, inputStream, metadata);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatVideo(String fileName, File file) {

        String folder = "wechat/videos/";
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, file);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatVideoThumbUrl(String fileName, String url) {

        String folder = "wechat/thumbs/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, inputStream, metadata);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatFileUrl(String fileName, String url) {

        String folder = "wechat/files/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, inputStream, metadata);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatFile(String fileName, File file) {

        String folder = "wechat/files/";
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, file);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveWeChatThumb(String fileName, File file) {

        String folder = "wechat/thumbs/";
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, file);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String uploadWeChatDb(String fileName, File file) {

        String folder = "wechat/db/";
        return uploadCommon(folder, fileName, file);
    }

    /**
     * 
     */
    public String saveSchoolLogoUrl(String fileName, String url) {

        String folder = "school/logo/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, inputStream, metadata);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    public String saveCourseLogoUrl(String fileName, String url) {

        String folder = "course/logo/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectMetadata metadata = new ObjectMetadata();
        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, inputStream, metadata);
        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

    /**
     * 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20 M 以下的文件使用该接口 大文件上传请参照 API 文档高级 API 上传
     *
     * @param folder   folder
     * @param fileName filename
     * @param file     locale
     * @return string
     */
    private String uploadCommon(String folder, String fileName, File file) {

        cosClient.putObject(tencentProperties.getBucketName(), folder + fileName, file);

        return tencentProperties.getBucketDomain() + "/" + folder + fileName;
    }

}
