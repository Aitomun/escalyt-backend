package com.decadev.escalayt.infrastructure.controller;

import com.decadev.escalayt.entity.Category;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.exceptions.PasswordMismatchException;
import com.decadev.escalayt.exceptions.UserNotFoundException;
import com.decadev.escalayt.payload.request.*;
import com.decadev.escalayt.payload.response.CategoryResponse;
import com.decadev.escalayt.payload.response.LoginResponse;
import com.decadev.escalayt.payload.response.UpdatePersonResponse;
import com.decadev.escalayt.payload.response.UserDetailResponse;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.service.CategoryService;
import com.decadev.escalayt.service.PersonService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final PersonService personService;
    private final CategoryService categoryService;
    private final PersonRepository personRepository;



    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody PersonRegisterRequest registrationRequest) {

        try {
            String registerUser = personService.registerUser(registrationRequest);
            if (!registerUser.equals("Invalid Email domain")) {
                return ResponseEntity.ok("User registered successfully. Please check your email to confirm your account");
            } else {
                return ResponseEntity.badRequest().body("Invalid Email!!!");
            }

        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequestDto) {

        return ResponseEntity.ok(personService.loginUser(loginRequestDto));

    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPasswordRequest(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        try {
            String response = personService.forgotPasswordRequest(passwordResetRequest);
            return ResponseEntity.ok(response);
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body("An error occurred while sending the password reset email.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // do this after token confirmation
    @GetMapping("/confirm-forget-password-token")
    public ResponseEntity<?> confirmPasswordForget(@RequestParam("token") String token) {

        return ResponseEntity.ok("Confirmed");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> confirmPasswordReset(@RequestParam("token") String token, @Valid @RequestBody PasswordResetConfirmationRequest passwordResetConfirmationRequest) {
        try {
            String response = personService.confirmResetPassword(token, passwordResetConfirmationRequest);
            return ResponseEntity.ok(response);
        } catch (PasswordMismatchException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/update-user")
    public ResponseEntity<UpdatePersonResponse> updateUser(@Valid @RequestBody UpdatePersonRequest updatePersonRequest) {

        //Get the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Get the email of the user
        String currentUser = authentication.getName();

        UpdatePersonResponse response = personService.updatePersonDetails(currentUser, updatePersonRequest);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/authenticated-user")
    public ResponseEntity<UserDetailResponse> getAuthenticatedUser() {
        UserDetailResponse userDetails = personService.getAuthenticatedUserDetails();
        return new ResponseEntity<>(userDetails, HttpStatus.OK);
    }
    @GetMapping("/category")
    public ResponseEntity<List<Category>> getAllCategoriesForOrg() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long orgId = getOrgIdFromEmail(email);

        List<Category> categories = categoryService.getCategoriesByOrgId(orgId);
        return ResponseEntity.ok(categories);
    }
    private Long getOrgIdFromEmail(String email) {
        // Assuming you have a method in the personRepository to find the orgId by email
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"))
                .getOrgId();
    }

    @GetMapping("/verify-token")
    public ResponseEntity<?> verifyToken() {
        // If the request reaches this point, it means the token is valid
        return ResponseEntity.ok(Collections.singletonMap("message", "Token is valid"));
    }

}
