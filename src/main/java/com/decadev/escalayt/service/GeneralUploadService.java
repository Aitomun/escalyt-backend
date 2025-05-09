package com.decadev.escalayt.service;

import com.decadev.escalayt.payload.response.GeneralUserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface GeneralUploadService {
    GeneralUserResponse<String> uploadProfilePicture( String email,MultipartFile file);


}
