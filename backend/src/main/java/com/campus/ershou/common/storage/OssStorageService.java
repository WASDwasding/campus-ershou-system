package com.campus.ershou.common.storage;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.campus.ershou.common.exception.BizException;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;

@Service
public class OssStorageService {

    private final OssProperties properties;
    private volatile OSS client;

    public OssStorageService(OssProperties properties) {
        this.properties = properties;
    }

    public String upload(String objectKey, InputStream inputStream, long contentLength, String contentType) {
        if (!properties.configured()) {
            throw new BizException("OSS 未配置，请在 application-local.yml 填写 endpoint/bucket/密钥");
        }
        String key = properties.getDirPrefix() + objectKey;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentLength);
        if (StringUtils.hasText(contentType)) {
            metadata.setContentType(contentType);
        }
        client().putObject(properties.getBucket(), key, inputStream, metadata);
        return publicUrl(key);
    }

    public String publicUrl(String objectKey) {
        if (StringUtils.hasText(properties.getCustomDomain())) {
            String domain = properties.getCustomDomain().replaceAll("/$", "");
            return domain + "/" + objectKey;
        }
        return "https://" + properties.getBucket() + "." + properties.getEndpoint() + "/" + objectKey;
    }

    private OSS client() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = new OSSClientBuilder().build(
                            properties.getEndpoint(),
                            properties.getAccessKeyId(),
                            properties.getAccessKeySecret());
                }
            }
        }
        return client;
    }

    @PreDestroy
    public void shutdown() {
        if (client != null) {
            client.shutdown();
        }
    }
}
