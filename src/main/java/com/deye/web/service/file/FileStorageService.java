package com.deye.web.service.file;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    void upload(MultipartFile file, String directory, String fileName);

    void delete(String directory, String fileName);

    String getAccessLink(String directory, String fileName);
}
