package com.deye.web.service.file;

import com.deye.web.exception.FileStorageException;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.deye.web.util.error.ErrorCodeUtils.MINIO_DELETE_FILE_ERROR_CODE;
import static com.deye.web.util.error.ErrorCodeUtils.MINIO_UPLOAD_FILE_ERROR_CODE;
import static com.deye.web.util.error.ErrorMessageUtils.MINIO_DELETE_FILE_ERROR_MESSAGE;
import static com.deye.web.util.error.ErrorMessageUtils.MINIO_UPLOAD_FILE_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileStorageService implements FileStorageService {
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
            log.error("Error occurred while uploading image file", e);
            throw new FileStorageException(MINIO_DELETE_FILE_ERROR_CODE, MINIO_DELETE_FILE_ERROR_MESSAGE);
        }
    }

    @Override
    public String getAccessLink(String directory, String fileName) {
        return directory + "/" + fileName;
    }
}
