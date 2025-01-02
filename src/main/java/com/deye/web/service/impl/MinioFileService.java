package com.deye.web.service.impl;

import com.deye.web.exception.MinioException;
import com.deye.web.service.FileService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.deye.web.utils.error.ErrorCodeUtils.*;
import static com.deye.web.utils.error.ErrorMessageUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileService implements FileService {
    private static final String BUCKET = "deye-admin-files";

    private final ConfigService configService;
    private final MinioClient minio;

    @PostConstruct
    public void init() {
        BucketExistsArgs bucket = BucketExistsArgs.builder()
                .bucket(BUCKET)
                .build();
        try {
            if (!minio.bucketExists(bucket)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .bucket(BUCKET)
                        .build();
                minio.makeBucket(makeBucketArgs);
            }
        } catch (Exception e) {
            log.error("Error during creating bucket", e);
            throw new MinioException(MINIO_CREATE_BUCKET_ERROR_CODE, MINIO_CREATE_BUCKET_ERROR_MESSAGE);
        }
    }

    public String upload(MultipartFile file) {
        try (InputStream content = file.getInputStream()) {
            String fileName = UUID.randomUUID() + "_" + file.getName();
            log.info("Generated filename: {}", fileName);
            uploadFile(content, fileName, file.getContentType(), file.getSize());
            log.info("Successfully uploaded image: {}", fileName);
            return fileName;
        } catch (Exception e) {
            log.error("Error occurred while uploading image file", e);
            throw new MinioException(MINIO_UPLOAD_FILE_ERROR_CODE, MINIO_UPLOAD_FILE_ERROR_MESSAGE);
        }
    }

    public void delete(String fileName) {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                .bucket(BUCKET)
                .object(fileName)
                .build();
        try {
            minio.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("Error occurred while deleting image file", e);
            throw new MinioException(MINIO_DELETE_FILE_ERROR_CODE, MINIO_DELETE_FILE_ERROR_MESSAGE);
        }
    }

    private void uploadFile(InputStream content, String fileName, String contentType, Long imageSize) throws Exception {
        PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                .bucket(BUCKET)
                .object(fileName)
                .contentType(contentType)
                .stream(content, imageSize, -1)
                .build();
        minio.putObject(putObjectArgs);
    }

    public String generateFileLink(String fileName) {
        GetPresignedObjectUrlArgs urlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(BUCKET)
                .object(fileName)
                .method(Method.GET)
                .expiry(configService.getMinioLinkExpiryMinutes(), TimeUnit.MINUTES)
                .build();
        try {
            return minio.getPresignedObjectUrl(urlArgs);
        } catch (Exception e) {
            log.error("Error occurred while getting presigned url", e);
            throw new MinioException(MINIO_GENERATE_URL_ERROR_CODE, MINIO_GENERATE_URL_ERROR_MESSAGE);
        }
    }
}
