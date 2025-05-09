package com.decadev.escalayt.service;

import com.decadev.escalayt.entity.Category;
import com.decadev.escalayt.payload.request.CategoryRequest;
import com.decadev.escalayt.payload.response.CategoryResponse;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    CategoryResponse createCategory (String email, CategoryRequest categoryRequest);

    List<Map<String, Object>> getCategoryNamesAndIdsByOrgId(Long orgId);

    List<Category> getCategoriesByOrgId(Long orgId);



}
