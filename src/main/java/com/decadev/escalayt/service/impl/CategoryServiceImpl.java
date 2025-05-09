package com.decadev.escalayt.service.impl;

import com.decadev.escalayt.entity.Category;
import com.decadev.escalayt.entity.Person;
import com.decadev.escalayt.exceptions.AlreadyExistsException;
import com.decadev.escalayt.exceptions.UserNotFoundException;
import com.decadev.escalayt.payload.request.CategoryRequest;
import com.decadev.escalayt.payload.response.CategoryResponse;
import com.decadev.escalayt.repository.CategoryRepository;
import com.decadev.escalayt.repository.PersonRepository;
import com.decadev.escalayt.service.CategoryService;
import com.decadev.escalayt.service.PersonService;
import com.decadev.escalayt.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {


    private final PersonRepository personRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse createCategory(String email, CategoryRequest categoryRequest) {

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

        //finding the user by email
        personRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " + email));

        //checking if category exists by name
        if (categoryRepository.existsByCategoryName(categoryRequest.getCategoryName())) {
            throw new AlreadyExistsException("Category already exists");
        }

        Category newCategory = Category.builder()
                .categoryName(categoryRequest.getCategoryName())
                .description(categoryRequest.getDescription())
                .orgId(orgId)
                .build();
        categoryRepository.save(newCategory);

        return CategoryResponse.builder()
                .responseCode(AppConstants.CATEGORY_CREATION_SUCCESS_CODE)
                .responseMessage(AppConstants.CATEGORY_CREATION_SUCCESS_MESSAGE)
                .build();
    }

    @Override
    public List<Map<String, Object>> getCategoryNamesAndIdsByOrgId(Long orgId) {
        // Retrieve the authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Person> userOpt = personRepository.findByEmail(email);

        if (!userOpt.isPresent()) {
            throw new AccessDeniedException("User not found.");
        }

        Person user = userOpt.get();
        orgId = user.getOrgId(); // Use the orgId from the authenticated user

        List<Category> categories = categoryRepository.findByOrgId(orgId); // Use the new repository method
        return categories.stream()
                .map(category -> {
                    Map<String, Object> categoryMap = new HashMap<>();
                    categoryMap.put("id", category.getId());
                    categoryMap.put("name", category.getCategoryName());
                    return categoryMap;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Category> getCategoriesByOrgId(Long orgId) {

        return categoryRepository.findByOrgId(orgId);
    }


}