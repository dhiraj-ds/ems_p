package com.ems.controller.admin;
import com.ems.entity.Payroll;
import com.ems.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
@Controller @RequestMapping("/admin/payroll") @RequiredArgsConstructor
public class AdminPayrollController {
    private final PayrollService payS; private final EmployeeService empS;
    @GetMapping public String list(Model m){ LocalDate n=LocalDate.now(); int mo=n.getMonthValue(),yr=n.getYear(); m.addAttribute("payrolls",payS.findByMonthYear(mo,yr)); m.addAttribute("totalPayroll",payS.totalForMonth(mo,yr)); m.addAttribute("totalPaid",payS.paidForMonth(mo,yr)); m.addAttribute("currentMonth",n.getMonth().getDisplayName(TextStyle.FULL,Locale.ENGLISH)+" "+yr); m.addAttribute("activePage","payroll"); return "admin/payroll/list"; }
    @GetMapping("/add") public String add(Model m){ m.addAttribute("payroll",new Payroll()); m.addAttribute("employees",empS.findAll()); m.addAttribute("activePage","payroll"); return "admin/payroll/form"; }
    @PostMapping("/save") public String save(@ModelAttribute Payroll p,@RequestParam Long employeeId,RedirectAttributes ra){ p.setEmployee(empS.findById(employeeId)); payS.save(p); ra.addFlashAttribute("success","Payroll created"); return "redirect:/admin/payroll"; }
    @PostMapping("/{id}/paid") public String paid(@PathVariable Long id,RedirectAttributes ra){ payS.markPaid(id); ra.addFlashAttribute("success","Marked paid"); return "redirect:/admin/payroll"; }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,RedirectAttributes ra){ payS.delete(id); ra.addFlashAttribute("success","Deleted"); return "redirect:/admin/payroll"; }
}