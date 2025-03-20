package com.deye.web.service.file;

import com.deye.web.exception.FileStorageException;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

import static com.deye.web.util.error.ErrorCodeUtils.*;
import static com.deye.web.util.error.ErrorMessageUtils.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileService implements FileService {
    private final MinioClient minioClient;

    @Override
    public void upload(MultipartFile file, String directory, String fileName) {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(directory)
                    .build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .bucket(directory)
                        .build();
                minioClient.makeBucket(makeBucketArgs);
            }
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(directory)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .object(fileName)
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            log.error("Error occurred while uploading image file", e);
            throw new FileStorageException(MINIO_UPLOAD_FILE_ERROR_CODE, MINIO_UPLOAD_FILE_ERROR_MESSAGE);
        }
    }

    @Override
    public void delete(String directory, String fileName) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(directory)
                    .object(fileName)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Error occurred while uploading image file", e);
            throw new FileStorageException(MINIO_DELETE_FILE_ERROR_CODE, MINIO_DELETE_FILE_ERROR_MESSAGE);
        }
    }

    @Override
    public String getAccessLink(String directory, String fileName) {
        try {
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .bucket(directory)
                    .object(fileName)
                    .method(Method.GET)
                    .expiry(3, TimeUnit.MINUTES)
                    .build();
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FileStorageException(MINIO_GET_LINK_ERROR_CODE, MINIO_GET_LINK_ERROR_MESSAGE);
        }
    }
}
