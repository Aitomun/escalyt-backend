package com.decadev.escalayt.service.impl;


import com.decadev.escalayt.entity.ConfirmationTokenModel;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.repository.ConfirmationTokenRepository;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.service.TokenValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenValidationServiceImpl implements TokenValidationService {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final PersonRepository userModelRepository;


    @Override
    public String validateToken(String token) {

        Optional<ConfirmationTokenModel> confirmationTokenOptional = confirmationTokenRepository.findByToken(token);
        if (confirmationTokenOptional.isEmpty()) {
            return "Invalid token";
        }

        ConfirmationTokenModel confirmationToken = confirmationTokenOptional.get();

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return "Token has expired";
        }

        Person user = confirmationToken.getPerson();
        user.setEnabled(true);
        userModelRepository.save(user);

        confirmationTokenRepository.delete(confirmationToken); //delete the token after successful verification

        return "Email confirmed successfully!";

    }
}
