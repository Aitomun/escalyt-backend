package com.decadev.escalayt.service;

import com.decadev.escalayt.entity.Category;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.payload.request.*;
import com.decadev.escalayt.payload.response.*;
import jakarta.mail.MessagingException;


import java.util.Optional;
import java.util.concurrent.ExecutionException;

import java.util.List;


public interface PersonService {

    String registerUser(PersonRegisterRequest registrationRequest) throws MessagingException;

    LoginResponse loginUser(LoginRequest loginRequestDto) ;

    String forgotPasswordRequest(PasswordResetRequest passwordResetRequest) throws MessagingException;

    String confirmResetPassword(String token, PasswordResetConfirmationRequest passwordResetConfirmation);

    UpdatePersonResponse updatePersonDetails(String email, UpdatePersonRequest updatePersonRequest) ;


    NewEmployeeResponse addEmployee(String email, NewEmployeeRequest registrationRequest) throws MessagingException;


   EmployeeInfoResponse getEmployeeDetailsById(Long id);
    List<Person> getPersonsByOrgId(Long orgId);

    UserDetailResponse getAuthenticatedUserDetails();


    Person findById(Long personId);

    Optional<PersonResponseDTO> findPersonByAssigneeId(Long ticketId); // New method


}


