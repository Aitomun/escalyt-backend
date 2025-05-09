package com.decadev.escalayt.service;

import com.decadev.escalayt.entity.Department;
import com.decadev.escalayt.payload.request.DepartmentRequest;
import com.decadev.escalayt.payload.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    String createDepartment(DepartmentRequest request, String email);

    List<DepartmentResponse> getAllDepartments(String email);  // New method


}
