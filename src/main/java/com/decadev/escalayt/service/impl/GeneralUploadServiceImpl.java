package com.decadev.escalayt.service.impl;

import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.exceptions.NotFoundException;
import com.decadev.escalayt.payload.response.GeneralUserResponse;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.service.FileUploadService;
import com.decadev.escalayt.service.GeneralUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GeneralUploadServiceImpl implements GeneralUploadService {

    private final FileUploadService fileUploadService;
    private final PersonRepository personRepository;


    @Override
    public GeneralUserResponse<String> uploadProfilePicture( String email,MultipartFile file) {
        Person person = personRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Person not found"));
        // This is to upload the file

        String fileUrl;
        try{
            fileUrl = fileUploadService.uploadFile(file);
            person.setProfilePicture(fileUrl);
            personRepository.save(person);

        } catch (Exception e) {
            throw new RuntimeException("Failed to Upload your file",e);
        }

        return new GeneralUserResponse<>("Upload successful",fileUrl);
    }


}
