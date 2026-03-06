package com.ems.controller.admin;
import com.ems.entity.*;
import com.ems.entity.Attendance.AttStatus;
import com.ems.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/admin/attendance") @RequiredArgsConstructor
public class AdminAttendanceController {
    private final AttendanceService attS; private final EmployeeService empS;
    @GetMapping public String list(Model m){ m.addAttribute("records",attS.findToday()); m.addAttribute("present",attS.countTodayByStatus(AttStatus.PRESENT)); m.addAttribute("absent",attS.countTodayByStatus(AttStatus.ABSENT)); m.addAttribute("late",attS.countTodayByStatus(AttStatus.LATE)); m.addAttribute("onLeave",attS.countTodayByStatus(AttStatus.ON_LEAVE)); m.addAttribute("activePage","attendance"); return "admin/attendance/list"; }
    @GetMapping("/add") public String add(Model m){ m.addAttribute("record",new Attendance()); m.addAttribute("employees",empS.findAll()); m.addAttribute("activePage","attendance"); return "admin/attendance/form"; }
    @PostMapping("/save") public String save(@ModelAttribute Attendance a,@RequestParam Long employeeId,RedirectAttributes ra){ a.setEmployee(empS.findById(employeeId)); attS.save(a); ra.addFlashAttribute("success","Recorded"); return "redirect:/admin/attendance"; }
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model m){ m.addAttribute("record",attS.findById(id)); m.addAttribute("employees",empS.findAll()); m.addAttribute("activePage","attendance"); return "admin/attendance/form"; }
    @PostMapping("/{id}/update") public String update(@PathVariable Long id,@ModelAttribute Attendance a,@RequestParam Long employeeId,RedirectAttributes ra){ a.setId(id); a.setEmployee(empS.findById(employeeId)); attS.save(a); ra.addFlashAttribute("success","Updated"); return "redirect:/admin/attendance"; }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,RedirectAttributes ra){ attS.delete(id); ra.addFlashAttribute("success","Deleted"); return "redirect:/admin/attendance"; }
}