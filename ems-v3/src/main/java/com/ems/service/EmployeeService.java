package com.ems.service;
import com.ems.entity.Employee;
import com.ems.entity.Employee.EmpStatus;
import com.ems.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor @Transactional
public class EmployeeService {
    private final EmployeeRepository      repo;
    private final LeaveRequestRepository  leaveRepo;
    private final PayrollRepository       payrollRepo;
    private final AttendanceRepository    attendanceRepo;
    private final RefreshTokenRepository  refreshTokenRepo;
    private final UserRepository          userRepo;

    public List<Employee> findAll() { return repo.findAllWithAssociations(); }

    public List<Employee> search(String q) {
        return q == null || q.isBlank() ? repo.findAllWithAssociations() : repo.search(q);
    }

    public Employee findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Employee not found: " + id));
    }

    public Employee findByUsername(String username) {
        return repo.findByUserUsername(username)
            .orElseThrow(() -> new RuntimeException("No employee profile for user: " + username));
    }

    public Optional<Employee> findByUserId(Long uid) { return repo.findByUserId(uid); }

    public Employee save(Employee e) { return repo.save(e); }

    /**
     * Delete employee and all related records in the correct order to avoid FK violations:
     * 1. Refresh tokens (linked to User)
     * 2. Leave requests
     * 3. Payroll records
     * 4. Attendance records
     * 5. Employee row
     * 6. User row
     */
    public void delete(Long id) {
        Employee emp = findById(id);

        // 1. Delete refresh tokens for the linked user
        if (emp.getUser() != null) {
            refreshTokenRepo.deleteByUserId(emp.getUser().getId());
        }

        // 2. Delete leave requests
        leaveRepo.deleteByEmployeeId(id);

        // 3. Delete payroll records
        payrollRepo.deleteByEmployeeId(id);

        // 4. Delete attendance records
        attendanceRepo.deleteByEmployeeId(id);

        // 5. Delete employee (nullify user reference first to avoid constraint issue)
        Long userId = emp.getUser() != null ? emp.getUser().getId() : null;
        emp.setUser(null);
        repo.save(emp);
        repo.deleteById(id);

        // 6. Delete user account
        if (userId != null) {
            userRepo.deleteById(userId);
        }
    }

    public long countAll() { return repo.count(); }
    public long countByStatus(EmpStatus s) { return repo.countByStatus(s); }
    public boolean emailExists(String e) { return repo.existsByEmail(e); }
    public boolean emailExistsForOther(String e, Long id) { return repo.existsByEmailAndIdNot(e, id); }
}
