package com.zhaoyi.yijiaoyou.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import com.zhaoyi.yijiaoyou.properties.AliProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Component
public class AliFileUploadUtils {

    @Autowired
    private AliProperties aliProperties;

    public String upload(MultipartFile file) throws IOException {
        String accessKeyId = aliProperties.getAccessKeyId();
        String accessKeySecret = aliProperties.getAccessKeySecret();
        String bucketName = aliProperties.getBucketName();
        String endPoint = aliProperties.getEndPoint();

        InputStream inputStream = file.getInputStream();
        String originalFilename = file.getOriginalFilename();
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);
        String url = endPoint.split("//")[0] + "//" + bucketName + "." + endPoint.split("//")[1] + "/" + fileName;
        ossClient.shutdown();
        return url;
    }
}

