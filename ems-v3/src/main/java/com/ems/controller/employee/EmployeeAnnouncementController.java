package com.ems.controller.employee;
import com.ems.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller @RequestMapping("/employee/announcements") @RequiredArgsConstructor
public class EmployeeAnnouncementController {
    private final AnnouncementService s;
    @GetMapping public String list(Model m){ m.addAttribute("announcements",s.findAll()); m.addAttribute("activePage","announcements"); return "employee/announcements/list"; }
}