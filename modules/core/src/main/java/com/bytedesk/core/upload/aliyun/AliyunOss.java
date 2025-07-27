package com.bytedesk.core.upload.aliyun;

import com.aliyun.oss.OSS;
// import com.aliyun.oss.model.PutObjectResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 阿里云OSS对象存储服务工具类
 *
 * 参考
 * 
 * @see <a href="https://blog.csdn.net/Holmofy/article/details/79102577">url</a>
 *
 *      阿里云官方文档
 * @see <a href=
 *      "https://help.aliyun.com/document_detail/32013.html?spm=a2c4g.11186623.6.681.NK35jK"></a>
 * @see <a href=
 *      "https://github.com/aliyun/aliyun-oss-java-sdk/blob/master/src/samples/SimpleGetObjectSample.java"></a>
 *
 * @author bytedesk.com
 */
@Component
public class AliyunOss {
    
    // @Autowired
    // AttachmentService attachmentService;

    private String uploadDirPrefix = "";

    public AliyunOss() {
        // 构造函数
    }

    @Autowired
    private AliyunProperties aliyunProperties;

    @Autowired
    private OSS ossClient;

    public String uploadAttachment(MediaType mediaType, String fileName, int width, int height, String username,
            File file) {

        String folder = uploadDirPrefix + "attachments/";
        // return uploadCommon(folder, fileName, file);
        // PutObjectResult putObjectResult =
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, file);

        String url = aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;

        // attachmentService.create(url, mediaType, fileName, file.length(), width,
        // height, username);

        return url;
    }

    public String uploadAvatar(String fileName, File file) {

        String folder = uploadDirPrefix + "avatars6/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadImage(String fileName, File file) {

        String folder = uploadDirPrefix + "images6/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadVoice(String fileName, File file) {

        String folder = uploadDirPrefix + "voices6/";
        return uploadCommon(folder, fileName, file);
    }

    public String uploadVideo(String fileName, File file) {

        String folder = uploadDirPrefix + "videos6/";
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
     * 
     * @param fileName 文件名
     * @param build    debug or release
     * @param file     file
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
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, inputStream);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    // 保存小程序码
    public String saveWeChatInputStream(String fileName, InputStream inputStream) {
        //
        String folder = "wechat/images/";
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, inputStream);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatAvatarUrl(String fileName, String url) {

        String folder = "wechat/avatars/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, inputStream);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatImage(String fileName, File file) {

        String folder = "wechat/images/";
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, file);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatVoiceUrl(String fileName, String url) {

        String folder = "wechat/voices/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, inputStream);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatVoice(String fileName, File file) {

        String folder = "wechat/voices/";
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, file);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatVideoUrl(String fileName, String url) {

        String folder = "wechat/videos/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, inputStream);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatVideo(String fileName, File file) {

        String folder = "wechat/videos/";
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, file);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatVideoThumbUrl(String fileName, String url) {

        String folder = "wechat/thumbs/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, inputStream);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatFileUrl(String fileName, String url) {

        String folder = "wechat/files/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, inputStream);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatFile(String fileName, File file) {

        String folder = "wechat/files/";
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, file);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveWeChatThumb(String fileName, File file) {

        String folder = "wechat/thumbs/";
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, file);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
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
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, inputStream);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    public String saveCourseLogoUrl(String fileName, String url) {

        String folder = "course/logo/";
        InputStream inputStream = null;
        try {
            inputStream = new URL(url).openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, inputStream);
        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

    private String uploadCommon(String folder, String fileName, File file) {

        ossClient.putObject(aliyunProperties.getOssBucketName(), folder + fileName, file);

        return aliyunProperties.getOssBaseUrl() + "/" + folder + fileName;
    }

}
