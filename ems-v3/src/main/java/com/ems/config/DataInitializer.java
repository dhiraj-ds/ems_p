package com.ems.config;
import com.ems.entity.*;
import com.ems.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component @RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepo;
    private final EmployeeRepository empRepo;
    private final DepartmentRepository deptRepo;
    private final AnnouncementRepository annRepo;

    @Override
    public void run(String... args) {
        if (!userRepo.existsByUsername("admin")) {
            userRepo.save(User.builder()
                .username("admin")
                .password("admin123")   // plain text — no encoding
                .role(Role.ROLE_ADMIN)
                .build());
            System.out.println("[EMS] Admin created — admin / admin123");
        }
        if (!deptRepo.existsByNameIgnoreCase("Engineering")) {
            deptRepo.save(Department.builder().name("Engineering").head("Ravi Kumar").email("eng@company.com").numberOfPositions(20).status(Department.DeptStatus.ACTIVE).build());
            deptRepo.save(Department.builder().name("Human Resources").head("Priya Sharma").email("hr@company.com").numberOfPositions(8).status(Department.DeptStatus.ACTIVE).build());
            deptRepo.save(Department.builder().name("Finance").head("Amit Patel").email("finance@company.com").numberOfPositions(6).status(Department.DeptStatus.ACTIVE).build());
            deptRepo.save(Department.builder().name("Marketing").head("Sunita Rao").email("mkt@company.com").numberOfPositions(10).status(Department.DeptStatus.ACTIVE).build());
        }
        if (!userRepo.existsByUsername("mohan")) {
            var dept = deptRepo.findByNameIgnoreCase("Engineering").orElse(null);
            var u = userRepo.save(User.builder()
                .username("mohan")
                .password("mohan123")   // plain text — no encoding
                .role(Role.ROLE_EMPLOYEE)
                .build());
            empRepo.save(Employee.builder()
                .firstName("Mohan").lastName("Kumar")
                .email("mohan@company.com").phone("9876543210")
                .department(dept).position("Software Engineer")
                .joinDate(LocalDate.of(2024, 1, 15))
                .basicSalary(55000.0).gender("Male")
                .status(Employee.EmpStatus.ACTIVE).user(u).build());
            System.out.println("[EMS] Employee created — mohan / mohan123");
        }
        if (annRepo.count() == 0) {
            annRepo.save(Announcement.builder()
                .title("Welcome to EMS v3!")
                .message("Employee Management System is live. Thymeleaf + JWT edition.")
                .postedBy("Admin").audience(Announcement.Audience.ALL_STAFF)
                .priority(Announcement.Priority.IMPORTANT)
                .postDate(LocalDate.now()).build());
        }
    }
}
