package com.deye.web.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigService {
    private final Environment env;

    public String getMinioBucketName() {
        return env.getProperty("minio.bucket.name");
    }

    public String getMinioSecretKey() {
        return env.getProperty("minio.secretKey");
    }

    public String getMinioAccessKey() {
        return env.getProperty("minio.accessKey");
    }

    public String getMinioUrl() {
        return env.getProperty("minio.url");
    }
}
