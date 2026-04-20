package com.ems.controller.employee;

import com.ems.entity.Employee;
import com.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Resolves the Employee entity for the currently logged-in user.
 *
 * Uses findByUsername() which does a targeted JOIN FETCH on user,
 * instead of loading all employees and streaming (which triggered
 * LazyInitializationException with open-in-view=false).
 */
@Component @RequiredArgsConstructor
public class EmployeeHelper {
    private final EmployeeService empS;

    public Employee current(Authentication auth) {
        return empS.findByUsername(auth.getName());
    }
}