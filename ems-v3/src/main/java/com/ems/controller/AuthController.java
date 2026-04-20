package com.ems.controller;

import com.ems.config.JwtAuthFilter;
import com.ems.config.JwtUtil;
import com.ems.entity.*;
import com.ems.repository.UserRepository;
import com.ems.service.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

/**
 * Auth Controller — Login, Register, Logout, Refresh.
 * Passwords are stored and compared as plain text (no encoding).
 */
@Controller @RequiredArgsConstructor @Slf4j
public class AuthController {

    private final DaoAuthenticationProvider authProvider;
    private final JwtUtil                   jwtUtil;
    private final JwtAuthFilter             jwtFilter;
    private final UserRepository            userRepo;
    private final EmployeeService           empService;
    private final DepartmentService         deptService;
    private final RefreshTokenService       refreshSvc;

    // ── GET /login ────────────────────────────────────────────────────────────
    @GetMapping("/login")
    public String loginPage(@RequestParam(required=false) String error,
                            @RequestParam(required=false) String logout,
                            @RequestParam(required=false) String expired,
                            @RequestParam(required=false) String denied,
                            @RequestParam(required=false) String registered,
                            Authentication auth, Model m) {
        if (HomeController.isLoggedIn(auth)) return HomeController.dashboardFor(auth);
        m.addAttribute("error",      error      != null);
        m.addAttribute("logout",     logout     != null);
        m.addAttribute("expired",    expired    != null);
        m.addAttribute("denied",     denied     != null);
        m.addAttribute("registered", registered != null);
        return "login";
    }

    // ── POST /auth/login ──────────────────────────────────────────────────────
    @PostMapping("/auth/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse res) {
        try {
            Authentication auth = authProvider.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

            String role = auth.getAuthorities().iterator().next().getAuthority();
            String accessToken = jwtUtil.generateToken(username, role);
            jwtFilter.setAccessCookie(res, accessToken);

            User user = userRepo.findByUsername(username).orElseThrow();
            jwtFilter.setRefreshCookie(res, refreshSvc.createToken(user).getToken());

            log.info("Login: {} [{}]", username, role);
            return "ROLE_ADMIN".equals(role) ? "redirect:/admin/dashboard" : "redirect:/employee/dashboard";

        } catch (BadCredentialsException e) {
            log.warn("Login failed: {}", username);
            return "redirect:/login?error";
        }
    }

    // ── GET /auth/logout ──────────────────────────────────────────────────────
    @GetMapping("/auth/logout")
    public String logout(HttpServletRequest req, HttpServletResponse res) {
        String refreshVal = extractCookie(req, "ems_refresh");
        if (refreshVal != null) refreshSvc.deleteToken(refreshVal);
        jwtFilter.clearBothCookies(res);
        return "redirect:/login?logout";
    }

    // ── GET /auth/refresh ─────────────────────────────────────────────────────
    @GetMapping("/auth/refresh")
    public String refresh(Authentication auth) {
        if (HomeController.isLoggedIn(auth)) return HomeController.dashboardFor(auth);
        return "redirect:/login?expired";
    }

    // ── GET /register ─────────────────────────────────────────────────────────
    @GetMapping("/register")
    public String registerPage(Authentication auth, Model m) {
        if (HomeController.isLoggedIn(auth)) return HomeController.dashboardFor(auth);
        m.addAttribute("departments", deptService.findAllSimple());
        return "register";
    }

    // ── POST /auth/register ───────────────────────────────────────────────────
    @PostMapping("/auth/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam String email,
                           @RequestParam(required=false) String phone,
                           @RequestParam(required=false) Long departmentId,
                           @RequestParam(required=false) String position,
                           @RequestParam(required=false) String gender,
                           RedirectAttributes ra) {
        if (userRepo.existsByUsername(username)) {
            ra.addFlashAttribute("error", "Username '" + username + "' is already taken.");
            return "redirect:/register";
        }
        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/register";
        }
        if (empService.emailExists(email)) {
            ra.addFlashAttribute("error", "Email '" + email + "' is already registered.");
            return "redirect:/register";
        }

        // Save plain-text password — no encoding
        User user = userRepo.save(User.builder()
            .username(username)
            .password(password)     // plain text
            .role(Role.ROLE_EMPLOYEE)
            .build());

        Department dept = departmentId != null ? deptService.findById(departmentId) : null;
        empService.save(Employee.builder()
            .firstName(firstName).lastName(lastName).email(email).phone(phone)
            .position(position).gender(gender).department(dept)
            .joinDate(LocalDate.now()).basicSalary(0.0)
            .status(Employee.EmpStatus.ACTIVE).user(user).build());

        log.info("Registered: {}", username);
        return "redirect:/login?registered";
    }

    private String extractCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies())
            if (name.equals(c.getName())) return c.getValue();
        return null;
    }
}
