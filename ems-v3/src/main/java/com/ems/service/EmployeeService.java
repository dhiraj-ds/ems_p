package com.ems.service;
import com.ems.entity.Employee;
import com.ems.entity.Employee.EmpStatus;
import com.ems.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class EmployeeService {
    private final EmployeeRepository repo;

    /** Loads employees with user + department eagerly — safe with open-in-view=false. */
    public List<Employee> findAll() { return repo.findAllWithAssociations(); }

    public List<Employee> search(String q) {
        return q == null || q.isBlank() ? repo.findAllWithAssociations() : repo.search(q);
    }

    public Employee findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Employee not found: " + id));
    }

    /** Find employee by their login username — used by EmployeeHelper. */
    public Employee findByUsername(String username) {
        return repo.findByUserUsername(username)
            .orElseThrow(() -> new RuntimeException("No employee profile for user: " + username));
    }

    public Optional<Employee> findByUserId(Long uid) { return repo.findByUserId(uid); }
    public Employee save(Employee e) { return repo.save(e); }
    public void delete(Long id) { repo.deleteById(id); }
    public long countAll() { return repo.count(); }
    public long countByStatus(EmpStatus s) { return repo.countByStatus(s); }
    public boolean emailExists(String e) { return repo.existsByEmail(e); }
    public boolean emailExistsForOther(String e, Long id) { return repo.existsByEmailAndIdNot(e, id); }
}