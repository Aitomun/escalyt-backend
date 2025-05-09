package com.decadev.escalayt.service.impl;

import com.decadev.escalayt.entity.*;
import com.decadev.escalayt.enums.Role;
import com.decadev.escalayt.enums.TokenType;
import com.decadev.escalayt.exceptions.*;
import com.decadev.escalayt.infrastructure.config.JwtService;
import com.decadev.escalayt.payload.request.*;
import com.decadev.escalayt.payload.response.*;
import com.decadev.escalayt.repository.ConfirmationTokenRepository;
import com.decadev.escalayt.repository.DepartmentRepository;
import com.decadev.escalayt.repository.JTokenRepository;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.service.EmailService;
import com.decadev.escalayt.service.NotificationService;
import com.decadev.escalayt.service.PersonService;
import com.decadev.escalayt.util.AccountUtils;
import com.decadev.escalayt.util.AppConstants;
import com.decadev.escalayt.util.EmailUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final JTokenRepository jTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;
    private final DepartmentRepository departmentRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final NotificationService notificationService;
    private static final Logger logger = Logger.getLogger(PersonServiceImpl.class.getName());


    @Value("${baseUrl}")
    private String baseUrl;

    @Override
    public String registerUser(PersonRegisterRequest registrationRequest) throws MessagingException {
        // Validate email format
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(registrationRequest.getEmail());

        if(!matcher.matches()){
            return "Invalid Email domain";
        }

        String[] emailParts = registrationRequest.getEmail().split("\\.");
        if (emailParts.length < 2 || emailParts[emailParts.length - 1].length() < 2) {
            System.out.println("Invalid email domain. Email parts: " + Arrays.toString(emailParts));
            return "Invalid Email domain";
        }

        if(!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())){
            throw new IllegalArgumentException("Passwords do not match!");
        }

        Optional<Person> existingUser = personRepository.findByEmail(registrationRequest.getEmail());

        if(existingUser.isPresent()){
            throw new EmailAlreadyExistsException("Email already exists. Login to your account!");
        }
//     just implemented this

        Optional<Person> existingUserByUsername = personRepository.findByUsername(registrationRequest.getUsername());
        if(existingUserByUsername.isPresent()){
            throw new UsernameAlreadyExistsException("Username already exists. Choose another username!");
        }


        Person newUser = Person.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(Role.ADMIN)
                .personCategory(registrationRequest.getCategory())
                .position(registrationRequest.getPosition())
                .username(registrationRequest.getUsername())
                .profilePicture(registrationRequest.getProfilePicture())
                .department(registrationRequest.getDepartment())
                .orgId(null)
                .build();

        Person savedUser = personRepository.save(newUser);


        savedUser.setOrgId(savedUser.getId());
        personRepository.save(savedUser);
        ConfirmationTokenModel confirmationToken = new ConfirmationTokenModel(savedUser);
        confirmationTokenRepository.save(confirmationToken);

        String confirmationUrl = EmailUtil.getVerificationUrl(confirmationToken.getToken());

        //Here is for the Sending of mail

        EmailDetails emailDetails = EmailDetails.builder()
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .recipient(savedUser.getEmail())
                .subject("ESCALAYT ACCOUNT SUCCESSFUL")
                .link(confirmationUrl)
                .build();
        emailService.sendEmailAlerts(emailDetails, "email-verification");

        return "Confirmed Email";

    }

    private void saveUserToken(Person person, String jwtToken) {
        var token = JToken.builder()
                .person(person)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        jTokenRepository.save(token);
    }

    private void revokeAllUserTokens(Person person) {
        var validUserTokens = jTokenRepository.findAllValidTokenByUser(person.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        jTokenRepository.saveAll(validUserTokens);
    }


    @Override
    public LoginResponse loginUser(LoginRequest loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getEmail(),
                        loginRequestDto.getPassword()
                )
        );
        Person user = personRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + loginRequestDto.getEmail()));

        if (!user.isEnabled()) {
            throw new UserNotEnabledException("User account is not enabled. Please check your email to confirm your account.");
        }

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .topic("NOTIFICATION")
                .title("NOTIFICATION")
                .body(user.getFirstName() + " "+ user.getLastName()+ " has been successfully logged in")
                .build();
        try {
            notificationService.sendNotificationToUser(user.getId(), notificationRequest);
        }catch (ExecutionException | InterruptedException e) {
            throw new NotificationException("Failed to send notification", e);
        }

        return LoginResponse.builder()
                .responseCode("002")
                .responseMessage("Login Successfully")
                .loginInfo(LoginInfo.builder()
                        .email(user.getEmail())
                        .token(jwtToken)
                        .role(user.getRole())
                        .build())
                .build();

    }

    @Override
    public String forgotPasswordRequest(PasswordResetRequest passwordResetRequest) throws MessagingException{
        Optional<Person> userOptional = personRepository.findByEmail(passwordResetRequest.getEmail());

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + passwordResetRequest.getEmail());
        }

        Person user = userOptional.get();
   //     String token = UUID.randomUUID().toString();
        ConfirmationTokenModel confirmationToken = new ConfirmationTokenModel(user);
        confirmationTokenRepository.save(confirmationToken);

        String resetUrl = EmailUtil.getResetPasswordUrl(confirmationToken.getToken());
        //String resetUrl = "http://localhost:8080/api/auth/reset?token=" + token;

        EmailDetails emailDetails = EmailDetails.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .recipient(user.getEmail())
                .link(resetUrl)
                .subject("ESCALAYT PASSWORD RESET")
                .build();
        emailService.sendEmailAlerts(emailDetails, "forgot-password");

        return "Password reset email sent";
    }

    @Override
    public String confirmResetPassword(String token, PasswordResetConfirmationRequest passwordResetConfirmationRequest) {
        Optional<ConfirmationTokenModel> tokenOptional = confirmationTokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            throw new IllegalArgumentException("Invalid token");
        }


        if(!passwordResetConfirmationRequest.getNewPassword().equals(passwordResetConfirmationRequest.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New passwords do not match");
        }

        ConfirmationTokenModel tokenModel = tokenOptional.get();
        Person user = tokenModel.getPerson();
        user.setPassword(passwordEncoder.encode(passwordResetConfirmationRequest.getConfirmNewPassword()));
        personRepository.save(user);

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .topic("WARNING")
                .title("WARNING")
                .body("Your password has been changed")
                .build();
        try {
            notificationService.sendNotificationToUser(user.getId(), notificationRequest);
        }catch (ExecutionException | InterruptedException e) {
            throw new NotificationException("Failed to send notification", e);
        }
        confirmationTokenRepository.delete(tokenModel);

        return "Password reset successfully";
    }

    @Override
    public UpdatePersonResponse updatePersonDetails( String email, UpdatePersonRequest updatePersonRequest) {
        // Find the person through the email
        Person person = personRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email));
        // Set everything
        person.setFirstName(updatePersonRequest.getFirstName());
        person.setLastName(updatePersonRequest.getLastName());
        person.setPhoneNumber(updatePersonRequest.getPhoneNumber());
        person.setDepartment(updatePersonRequest.getDepartment());
        person.setPosition(updatePersonRequest.getPosition());
        person.setProfilePicture(updatePersonRequest.getProfilePicture());
        person.setUsername(updatePersonRequest.getUsername());

        // Save the person
        personRepository.save(person);

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .topic("NOTIFICATION")
                .title("NOTIFICATION")
                .body("Your details have been successfully updated")
                .build();
        try {
            notificationService.sendNotificationToUser(person.getId(), notificationRequest);
        }catch (ExecutionException | InterruptedException e) {
            throw new NotificationException("Failed to send notification", e);
        }
        // Add the response
        return UpdatePersonResponse.builder()
                .responseCode("003")
                .responseMessage("Person updated successfully")
                .build();
    }


    @Override
    public NewEmployeeResponse addEmployee(String email, NewEmployeeRequest registrationRequest) throws MessagingException {

        // Retrieve the admin user by email to get the orgId
        Optional<Person> adminUserOpt = personRepository.findByEmail(email);

        if (!adminUserOpt.isPresent()) {
            throw new AccessDeniedException("Admin user not found.");
        }

        Person adminUser = adminUserOpt.get();
        if (!"ADMIN".equalsIgnoreCase(String.valueOf(adminUser.getRole()))) {
            throw new AccessDeniedException("You do not have the necessary permissions to perform this action");
        }

        Long orgId = adminUser.getId();

        // Validate Email
        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(registrationRequest.getEmail());

        if (!matcher.matches()) {
            throw new BadRequestException("Invalid Email domain");
        }

        String[] emailParts = registrationRequest.getEmail().split("\\.");
        if (emailParts.length < 2 || emailParts[emailParts.length - 1].length() < 2) {
            throw new BadRequestException("Invalid Email domain");
        }

        Optional<Person> existingUserByEmail = personRepository.findByEmail(registrationRequest.getEmail());
        if (existingUserByEmail.isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists.");
        }

        // Check if the department exists
        Optional<Department> departmentOpt = departmentRepository.findByDepartmentName(registrationRequest.getDepartment());
        if (!departmentOpt.isPresent()) {
            throw new BadRequestException("Department does not exist.");

        }
        System.out.println("check if it happen 1st");
        String generatedPassword = AccountUtils.generateRandomPassword();
        String generatedUsername = AccountUtils.generateRandomUsername();

        Person newUser = Person.builder()
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .role(Role.USER)
                .password(passwordEncoder.encode(generatedPassword))
                .personCategory(registrationRequest.getCategory())
                .position(registrationRequest.getPosition())
                .username(generatedUsername)
                .department(departmentOpt.get().getDepartmentName()) // Use the existing department
                .orgId(orgId)
                .build();
        System.out.println("it builds");

        Person savedEmployee = personRepository.save(newUser);


        System.out.println("saves here ");
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .topic("NOTIFICATION")
                .title("NOTIFICATION")
                .body(newUser.getFirstName() + " "+ newUser.getLastName()+ " has been successfully created")
                .build();
        System.out.println("send notification eromo");

//        try {
//            notificationService.sendNotificationToUser(newUser.getId(), notificationRequest);
//            System.out.println("success notif");
//
//        }catch (ExecutionException | InterruptedException e) {
//            throw new NotificationException("Failed to send notification", e);
//        }

        System.out.println("success notif");

        ConfirmationTokenModel confirmationToken = new ConfirmationTokenModel(savedEmployee);
        confirmationTokenRepository.save(confirmationToken);

        String confirmationUrl = EmailUtil.getVerificationUrl(confirmationToken.getToken());
        //String confirmationUrl = EmailUtil.getVerificationUrl(savedEmployee.getId().toString());
        System.out.println("confirmation url: " + confirmationUrl);
                // Create and send verification email
        EmailDetails emailDetails = EmailDetails.builder()
                .firstName(savedEmployee.getFirstName())
                .lastName(savedEmployee.getLastName())
                .recipient(savedEmployee.getEmail())
                .subject("ESCALAYT ACCOUNT SUCCESSFUL")
                .link(confirmationUrl)
                .build();

        emailService.sendEmailAlerts(emailDetails, "email-verification");
        System.out.println("email verification successful");

        // send email with the generated password
        EmailDetails emailDetails1 = EmailDetails.builder()
                .recipient(savedEmployee.getEmail())
                .subject("ESCALAYT ACCOUNT DETAILS")
                .messageBody("Dear " + savedEmployee.getFirstName() + " " + savedEmployee.getLastName() + ",\n\n" +
                        "Here are your account details:\n\n" +
                        "Password: " + generatedPassword + "\n\n" +
                        "Ensure you login with your verified email. Also, change your password after your first login.\n\n" +
                        "Best regards,\n" +
                        "ESCALAYT")
                .build();

        emailService.sendNewEmployeeAlerts(emailDetails1);
        System.out.println("email not sent");
        return NewEmployeeResponse.builder()
                .responseCode(AppConstants.EMPLOYEE_CREATION_SUCCESS_CODE)
                .responseMessage(AppConstants.EMPLOYEE_CREATION_SUCCESS_MESSAGE)
                .employeeInfo(EmployeeInfo.builder()
                        .firstName(savedEmployee.getFirstName())
                        .lastName(savedEmployee.getLastName())
                        .email(savedEmployee.getEmail())
                        .orgId(savedEmployee.getOrgId())
                        .build())
                .build();
    }

    @Override

    public List<Person> getPersonsByOrgId(Long orgId) {
        // Retrieve the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Person> userOpt = personRepository.findByEmail(email);

        if (!userOpt.isPresent()) {
            throw new AccessDeniedException("User not found.");
        }

        Person user = userOpt.get();
        orgId = user.getOrgId(); // Use the orgId from the authenticated user

        return personRepository.findByOrgId(orgId); // Fetch persons by orgId
    }


    @Override
    public UserDetailResponse getAuthenticatedUserDetails() {
        // Retrieve the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Person> userOpt = personRepository.findByEmail(email);

        if (!userOpt.isPresent()) {
            throw new AccessDeniedException("User not found.");
        }

        Person user = userOpt.get();
        return UserDetailResponse.builder()
                .responseCode("200")
                .responseMessage("User fetched successfully")
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .department(user.getDepartment())
                .position(user.getPosition())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .phoneNumber(user.getPhoneNumber())
                .profilePicture(user.getProfilePicture())
                .password(user.getPassword())
                .personCategory(user.getPersonCategory())
                .orgId(user.getOrgId())
                .build();
    }

    @Override
    public Person findById(Long personId) {
        return personRepository.findById(personId)
                .orElseThrow(() -> new UserNotFoundException("Person not found with id: " + personId));

    }

    @Override
    public Optional<PersonResponseDTO> findPersonByAssigneeId(Long ticketId) {
        Optional<Person> person = personRepository.findPersonByAssigneeId(ticketId);
        return person.map(p -> PersonResponseDTO.builder()
                .name(p.getFirstName() + " " + p.getLastName())
                .email(p.getEmail())
                .position(p.getPosition())
                .phoneNumber(p.getPhoneNumber())
                .build());
    }

    public EmployeeInfoResponse getEmployeeDetailsById(Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + id));

        return EmployeeInfoResponse.builder()
                .fullName(person.getFirstName() + " " + person.getLastName())
                .position(person.getPosition())
                .email(person.getEmail())
                .department(person.getDepartment())
                .phoneNumber(person.getPhoneNumber())
                .build();
    }



}

   

