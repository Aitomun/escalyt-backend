package com.decadev.escalayt.infrastructure.controller;

import com.decadev.escalayt.payload.request.NotificationTokenRequest;
import com.decadev.escalayt.service.NotificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
@CrossOrigin(origins = "http://localhost:5173")
public class
NotificationTokenController {

    @Autowired
    private NotificationTokenService tokenService;

    @PostMapping("/save/{userId}")
    public ResponseEntity<String> saveToken(@RequestBody NotificationTokenRequest tokenRequest, @PathVariable Long userId) {
        tokenService.saveToken(userId,tokenRequest.getToken());
        return ResponseEntity.ok("Token saved successfully");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteToken(@RequestBody NotificationTokenRequest tokenRequest) {
        tokenService.deleteToken(tokenRequest.getToken());
        return ResponseEntity.ok("Token deleted successfully");
    }
}
