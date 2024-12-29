package com.deye.web.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigService {
    private static final int DEFAULT_MINIO_LINK_EXPIRY_IN_MINUTES = 1;

    private final Environment environment;

    public Integer getMinioLinkExpiryMinutes() {
        String minioLinkExpiry = environment.getProperty("minio.link.expiry.minutes");
        if (StringUtils.isNotBlank(minioLinkExpiry)) {
            try {
                return Integer.parseInt(minioLinkExpiry);
            } catch (NumberFormatException e) {
                log.error(e.getMessage());
            }
        }
        return DEFAULT_MINIO_LINK_EXPIRY_IN_MINUTES;
    }
}
