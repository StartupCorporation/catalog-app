package com.deye.web.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    Object upload(MultipartFile file);
    void delete(String fileName);
}
