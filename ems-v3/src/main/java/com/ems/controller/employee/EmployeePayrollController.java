package com.ems.controller.employee;
import com.ems.entity.*;
import com.ems.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
@Controller @RequestMapping("/employee/payroll") @RequiredArgsConstructor
public class EmployeePayrollController {
    private final EmployeeHelper h; private final PayrollService payS;
    @GetMapping public String list(Authentication auth,Model m){ Employee e=h.current(auth); m.addAttribute("payrolls",payS.findByEmployee(e.getId())); m.addAttribute("activePage","payroll"); return "employee/payroll/list"; }
    @GetMapping("/{id}/payslip") public String slip(@PathVariable Long id,Authentication auth,Model m){ Payroll p=payS.findById(id); Employee e=h.current(auth); if(!p.getEmployee().getId().equals(e.getId()))return "redirect:/employee/payroll"; m.addAttribute("payroll",p); m.addAttribute("employee",e); m.addAttribute("generatedDate",LocalDate.now()); m.addAttribute("autoDownload",false); m.addAttribute("activePage","payroll"); return "employee/payroll/payslip"; }
    @GetMapping("/{id}/payslip/download") public String download(@PathVariable Long id,Authentication auth,Model m){ Payroll p=payS.findById(id); Employee e=h.current(auth); if(!p.getEmployee().getId().equals(e.getId()))return "redirect:/employee/payroll"; m.addAttribute("payroll",p); m.addAttribute("employee",e); m.addAttribute("generatedDate",LocalDate.now()); m.addAttribute("autoDownload",true); m.addAttribute("activePage","payroll"); return "employee/payroll/payslip"; }
}