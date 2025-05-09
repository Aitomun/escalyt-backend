package com.decadev.escalayt.service.impl;

import com.decadev.escalayt.entity.NotificationToken;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.repository.NotificationTokenRepository;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.service.NotificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationTokenServiceImpl implements NotificationTokenService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private NotificationTokenRepository tokenRepository;

    @Override
    public void saveToken(Long userId, String token) {
        Person user = personRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        NotificationToken existingToken = tokenRepository.findByToken(token);
        if (existingToken == null) {
            NotificationToken newToken = new NotificationToken();
            newToken.setToken(token);
            newToken.setPerson(user);
            tokenRepository.save(newToken);
        } else {
            System.out.println("Token already exists: " + token);
        }
    }


    @Override
    public void deleteToken(String token) {
        tokenRepository.deleteByToken(token);
    }
}
