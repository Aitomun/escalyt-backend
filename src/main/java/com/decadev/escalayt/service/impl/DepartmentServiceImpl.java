package com.decadev.escalayt.service.impl;

import com.decadev.escalayt.entity.Department;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.enums.Role;
import com.decadev.escalayt.exceptions.NotificationException;
import com.decadev.escalayt.exceptions.UserNotFoundException;
import com.decadev.escalayt.payload.request.DepartmentRequest;
import com.decadev.escalayt.payload.request.NotificationRequest;
import com.decadev.escalayt.payload.response.DepartmentResponse;
import com.decadev.escalayt.repository.DepartmentRepository;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.service.DepartmentService;
import com.decadev.escalayt.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final PersonRepository personRepository;
    private final NotificationService notificationService;

    @Override
    public String createDepartment(DepartmentRequest request, String email) {
        Optional<Person> personOpt = personRepository.findByEmail(email);
        if (personOpt.isEmpty()) {
            throw new UserNotFoundException("Person not found");
        }

        Person currentUser = personOpt.get();
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("Only admins can create departments");
        }

        Long orgId = currentUser.getOrgId();

        Department department = Department.builder()
                .departmentName(request.getDepartmentName())
                .orgId(orgId)
                .build();

         departmentRepository.save(department);
        NotificationRequest notificationRequest = NotificationRequest.builder()
                .topic("NOTIFICATION")
                .title("NOTIFICATION")
                .body(department.getDepartmentName()+" has been successfully created")
                .build();
        try {
            notificationService.sendNotificationToUser(currentUser.getId(), notificationRequest);
        }catch (ExecutionException | InterruptedException e) {
            throw new NotificationException("Failed to send notification", e);
        }

        return "Department Created Successfully";
    }

    @Override
    public List<DepartmentResponse> getAllDepartments(String email) {
        Optional<Person> personOpt = personRepository.findByEmail(email);
        if (personOpt.isEmpty()) {
            throw new UserNotFoundException("Person not found");
        }

        Person currentUser = personOpt.get();
        if (!currentUser.getRole().equals(Role.ADMIN)) {
            throw new SecurityException("Only admins can view departments");
        }

        Long orgId = currentUser.getOrgId();
        List<Department> departments = departmentRepository.findByOrgId(orgId);

        return departments.stream()
                .map(department -> DepartmentResponse.builder()
                        .departmentName(department.getDepartmentName())
                        .orgId(department.getOrgId())
                        .build())
                .collect(Collectors.toList());
    }



    }

