package com.deye.web.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * uploads file to the storage
     *
     * @param file - file, e.g image
     * @return file name
     */
    String upload(MultipartFile file);

    /**
     * deletes file from storage
     *
     * @param fileName key to find the file
     */
    void delete(String fileName);

    /**
     * generates link for file accessing depending on generated file name(upload method response)
     *
     * @param fileName basing on this param we are generating the link
     * @return http get link
     */
    String generateFileLink(String fileName);
}
