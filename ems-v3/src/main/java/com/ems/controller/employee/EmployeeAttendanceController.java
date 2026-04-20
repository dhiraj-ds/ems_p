package com.ems.controller.employee;
import com.ems.entity.*;
import com.ems.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@Controller @RequestMapping("/employee/attendance") @RequiredArgsConstructor
public class EmployeeAttendanceController {
    private final EmployeeHelper h; private final AttendanceService attS;
    @GetMapping public String list(Authentication auth,Model m){ Employee e=h.current(auth); LocalDate now=LocalDate.now(); List<Attendance> recs=attS.findByEmpRange(e.getId(),now.minusMonths(1),now); m.addAttribute("records",recs); m.addAttribute("present",recs.stream().filter(a->a.getStatus()==Attendance.AttStatus.PRESENT).count()); m.addAttribute("absent",recs.stream().filter(a->a.getStatus()==Attendance.AttStatus.ABSENT).count()); m.addAttribute("late",recs.stream().filter(a->a.getStatus()==Attendance.AttStatus.LATE).count()); m.addAttribute("activePage","attendance"); return "employee/attendance/list"; }
}