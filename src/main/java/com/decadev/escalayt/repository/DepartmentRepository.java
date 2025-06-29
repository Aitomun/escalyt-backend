package com.decadev.escalayt.repository;

import com.decadev.escalayt.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByDepartmentName(String departmentName);

    List<Department> findByOrgId(Long orgId);
}
