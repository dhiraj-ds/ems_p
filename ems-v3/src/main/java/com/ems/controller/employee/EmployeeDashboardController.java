package com.ems.controller.employee;
import com.ems.entity.*;
import com.ems.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
@Controller @RequestMapping("/employee") @RequiredArgsConstructor
public class EmployeeDashboardController {
    private final EmployeeHelper h; private final LeaveService leaveS;
    private final AttendanceService attS; private final AnnouncementService annS;
    @GetMapping({"/","/dashboard"}) public String dashboard(Authentication auth,Model m){
        Employee emp=h.current(auth); LocalDate now=LocalDate.now();
        long pending=leaveS.findByEmployee(emp.getId()).stream().filter(l->l.getStatus()==LeaveRequest.LeaveStatus.PENDING).count();
        long approved=leaveS.findByEmployee(emp.getId()).stream().filter(l->l.getStatus()==LeaveRequest.LeaveStatus.APPROVED).count();
        long att=attS.findByEmpRange(emp.getId(),now.withDayOfMonth(1),now).stream().filter(a->a.getStatus()==Attendance.AttStatus.PRESENT).count();
        m.addAttribute("employee",emp); m.addAttribute("pendingLeaves",pending);
        m.addAttribute("approvedLeaves",approved); m.addAttribute("attendanceDays",att);
        m.addAttribute("announcements",annS.count()); m.addAttribute("activePage","dashboard");
        return "employee/dashboard";
    }
}