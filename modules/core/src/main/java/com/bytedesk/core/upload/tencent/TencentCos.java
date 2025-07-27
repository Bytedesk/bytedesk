package com.bytedesk.core.upload.tencent;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;

// import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 腾讯云对象存储
 * @see <a href="https://console.cloud.tencent.com/cos5/bucket">腾讯云后台</a>
 *
 * @author bytedesk.com on 2019/4/4
 */
// @Slf4j
@Component
public class TencentCos {

    @Value("${tencent.bucket.location:}")
    private String bucketLocation;

    @Value("${tencent.bucket.name:}")
    private String bucketName;

    @Value("${tencent.bucket.domain:}")
    private String bucketDomain;

    @Value("${tencent.appid:}")
    private String appId;

    @Value("${tencent.secretid:}")
    private String secretId;

    @Value("${tencent.secretkey:}")
    private String secretKey;

    @Value("${upload.dir.prefix:}")
    private String uploadDirPrefix;

    // @Autowired
    // AttachmentService attachmentService;

    public String uploadAttachment(MediaType mediaType, String fileName, int width, int height, String username, File file) {

        String folder = uploadDirPrefix + "attachments/";
        // return uploadCommon(folder, fileName, file);

        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(bucketLocation));
        // log.info("secretId:" + secretId + " secretKey:" + secretKey + "
        // bucketLocation:" + bucketLocation);
        // 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);
        //
        String path = folder + fileName;
        // 指定要上传到 COS 上的路径
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, path, file);
        cosclient.putObject(putObjectRequest);

        String url = bucketDomain + "/" + path;

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

        String folder = uploadDirPrefix + "files/";
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

    /**
     * 简单文件上传, 最大支持 5 GB, 适用于小文件上传, 建议 20 M 以下的文件使用该接口 大文件上传请参照 API 文档高级 API 上传
     *
     * @param folder   folder
     * @param fileName filename
     * @param file     locale
     * @return string
     */
    private String uploadCommon(String folder, String fileName, File file) {

        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig(new Region(bucketLocation));
        // log.info("secretId:" + secretId + " secretKey:" + secretKey + " bucketLocation:" + bucketLocation);

        // 生成cos客户端
        COSClient cosclient = new COSClient(cred, clientConfig);

        //
        String path = folder + fileName;
        // 指定要上传到 COS 上的路径
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,  path, file);
        cosclient.putObject(putObjectRequest);

        return bucketDomain + "/"  + path;
    }

}
