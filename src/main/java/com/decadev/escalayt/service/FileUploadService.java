package com.decadev.escalayt.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {
    String uploadFile(MultipartFile file) throws IOException;
}
