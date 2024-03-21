package com.zhaoyi.yijiaoyou.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliProperties {
    private   String accessKeyId ;
    private  String accessKeySecret;
    private   String bucketName;
    private   String endPoint;
}
