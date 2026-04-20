package com.ems.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HomeController — serves the public landing page.
 *
 * Fix: authenticated users are immediately redirected to their dashboard.
 * This prevents logged-in users from seeing the landing page's Register/Login
 * CTAs and accidentally navigating to /register after they've already signed in.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String landing(Authentication auth) {
        if (isLoggedIn(auth)) return dashboardFor(auth);
        return "index";
    }

    static boolean isLoggedIn(Authentication auth) {
        return auth != null && auth.isAuthenticated()
            && !"anonymousUser".equals(auth.getPrincipal());
    }

    static String dashboardFor(Authentication auth) {
        boolean isAdmin = auth.getAuthorities()
            .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        return isAdmin ? "redirect:/admin/dashboard" : "redirect:/employee/dashboard";
    }
}