package com.decadev.escalayt.infrastructure.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.decadev.escalayt.payload.response.GeneralUserResponse;
import com.decadev.escalayt.service.GeneralUploadService;
import com.decadev.escalayt.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class GeneralUploadController {
    private final GeneralUploadService generalUploadService;

    @Autowired
    private Cloudinary cloudinary;


    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PutMapping("/profile-picture")
    public ResponseEntity<GeneralUserResponse<String>> uploadProfilePicture(@RequestParam("file") MultipartFile profilePicture) {

        //Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Get the email of the user
        String currentUser = authentication.getName();

        if(profilePicture.getSize() > AppConstants.MAX_FILE_SIZE){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new GeneralUserResponse<>("File exceeds the normal limit "));
        }

        GeneralUserResponse<String> response = generalUploadService.uploadProfilePicture(currentUser,profilePicture);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return ResponseEntity.ok(uploadResult);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("File upload failed: " + e.getMessage());
        }
    }

}
