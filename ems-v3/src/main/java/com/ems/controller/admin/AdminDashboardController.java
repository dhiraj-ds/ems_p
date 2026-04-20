package com.ems.controller.admin;
import com.ems.entity.Attendance.AttStatus;
import com.ems.entity.Employee.EmpStatus;
import com.ems.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
@Controller @RequestMapping("/admin") @RequiredArgsConstructor
public class AdminDashboardController {
    private final EmployeeService empS; private final DepartmentService deptS;
    private final LeaveService leaveS; private final PayrollService payS;
    private final AttendanceService attS; private final AnnouncementService annS;
    @GetMapping({"/","/dashboard"})
    public String dashboard(Model m){
        LocalDate now=LocalDate.now();
        m.addAttribute("totalEmployees",empS.countAll());
        m.addAttribute("onLeave",empS.countByStatus(EmpStatus.ON_LEAVE));
        m.addAttribute("departments",deptS.count());
        m.addAttribute("pendingLeaves",leaveS.countPending());
        m.addAttribute("presentToday",attS.countTodayByStatus(AttStatus.PRESENT));
        m.addAttribute("announcements",annS.count());
        m.addAttribute("approvedLeaves",leaveS.countApproved());
        m.addAttribute("pendingPayroll",payS.countPending());
        m.addAttribute("totalPayroll",payS.totalForMonth(now.getMonthValue(),now.getYear()));
        m.addAttribute("paidPayroll",payS.paidForMonth(now.getMonthValue(),now.getYear()));
        m.addAttribute("activePage","dashboard");
        return "admin/dashboard";
    }
}