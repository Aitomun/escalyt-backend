package com.decadev.escalayt.infrastructure.controller;

import com.decadev.escalayt.entity.Category;
import com.decadev.escalayt.entity.Department;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.payload.request.CategoryRequest;
import com.decadev.escalayt.payload.request.DepartmentRequest;
import com.decadev.escalayt.payload.request.NewEmployeeRequest;
import com.decadev.escalayt.payload.response.*;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.exceptions.UserNotFoundException;
import com.decadev.escalayt.payload.request.CategoryRequest;
import com.decadev.escalayt.payload.request.DepartmentRequest;
import com.decadev.escalayt.payload.request.NewEmployeeRequest;
import com.decadev.escalayt.payload.response.CategoryResponse;
import com.decadev.escalayt.payload.response.DepartmentResponse;
import com.decadev.escalayt.payload.response.EmployeeInfoResponse;
import com.decadev.escalayt.payload.response.NewEmployeeResponse;

import com.decadev.escalayt.service.CategoryService;
import com.decadev.escalayt.service.CommentService;
import com.decadev.escalayt.service.DepartmentService;
import com.decadev.escalayt.service.PersonService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

import java.util.concurrent.ExecutionException;

import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PersonService personService;
    private final CategoryService categoryService;
    private final CommentService commentService;
    private final DepartmentService departmentService;

    @Autowired
    private PersonRepository personRepository;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add-employee")
    public ResponseEntity<?> addEmployee(@RequestBody NewEmployeeRequest request) throws MessagingException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            NewEmployeeResponse response = personService.addEmployee(email,request);
            return ResponseEntity.ok(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/new-category")
    public ResponseEntity <CategoryResponse> createCategory (@Valid @RequestBody CategoryRequest categoryRequest){
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        return ResponseEntity.ok(categoryService.createCategory(email, categoryRequest));
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create-department")
    public ResponseEntity<?> createDepartment(@RequestBody DepartmentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            String response = departmentService.createDepartment(request, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/departments")
    public ResponseEntity<?> getAllDepartmentsForOrg() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        try {
            List<DepartmentResponse> departments = departmentService.getAllDepartments(email);
            return ResponseEntity.ok(departments);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/categories")
    public ResponseEntity<AllCategoryResponse> getAllCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long orgId = getOrgIdFromEmail(email); // Implement this method to fetch orgId using email

        List<Map<String, Object>> categories = categoryService.getCategoryNamesAndIdsByOrgId(orgId); // Use the new service method
        AllCategoryResponse response = AllCategoryResponse.builder()
                .responseCode("200")
                .responseMessage("Categories fetched successfully")
                .categories(categories)
                .build();
        return ResponseEntity.ok(response);
    }



    @GetMapping("/get-person-by-org")
    public ResponseEntity<AllPersonResponse> getPersonsByOrgId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long orgId = getOrgIdFromEmail(email); // Implement this method to fetch orgId using email

        List<Person> persons = personService.getPersonsByOrgId(orgId); // Use the new service method
        List<Map<String, Object>> personNames = persons.stream()
                .map(person -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", person.getId());
                    map.put("name", person.getFirstName() + " " + person.getLastName());
                    return map;
                })
                .collect(Collectors.toList());

        AllPersonResponse response = AllPersonResponse.builder()
                .responseCode("200")
                .responseMessage("Persons fetched successfully")
                .allPersons(personNames)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    private Long getOrgIdFromEmail(String email) {
        // Assuming you have a method in the personRepository to find the orgId by email
        return personRepository.findByEmail(email)
                .orElseThrow(() -> new AccessDeniedException("User not found"))
                .getOrgId();
    }

}
