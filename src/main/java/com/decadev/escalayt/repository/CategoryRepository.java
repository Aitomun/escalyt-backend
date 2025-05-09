package com.decadev.escalayt.repository;

import com.decadev.escalayt.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Boolean existsByCategoryName(String categoryName);

    List<Category> findByOrgId(Long orgId);


}
