package com.decadev.escalayt.infrastructure.controller;

import com.decadev.escalayt.service.TokenValidationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class EmailConfirmationController {

    private final TokenValidationService tokenValidationService;

   /* @GetMapping("/confirm")
    public ResponseEntity<?> confirmEmail(@RequestParam("token") String token){

        String result = tokenValidationService.validateToken(token);
        if ("Email confirmed successfully".equals(result)) {
            return ResponseEntity.ok(Collections.singletonMap("message", result));
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", result));
        }

    }*/

    @GetMapping("/confirm")
    public void confirmEmail(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        String result = tokenValidationService.validateToken(token);
        if ("Email confirmed successfully!".equals(result)) {
            response.sendRedirect("http://localhost:5174/email-confirmation-success");
        } else {
            String encodedResult = URLEncoder.encode(result, StandardCharsets.UTF_8.toString());
            response.sendRedirect("http://localhost:5174/email-confirmation-failure");
        }
    }


}
