package com.ems.controller.employee;
import com.ems.entity.*;
import com.ems.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/employee/leaves") @RequiredArgsConstructor
public class EmployeeLeaveController {
    private final EmployeeHelper h; private final LeaveService leaveS;
    @GetMapping public String list(Authentication auth,Model m){ Employee e=h.current(auth); m.addAttribute("leaves",leaveS.findByEmployee(e.getId())); m.addAttribute("activePage","leaves"); return "employee/leaves/list"; }
    @GetMapping("/apply") public String form(Model m){ m.addAttribute("leave",new LeaveRequest()); m.addAttribute("activePage","leaves"); return "employee/leaves/form"; }
    @PostMapping("/apply") public String apply(@ModelAttribute LeaveRequest l,Authentication auth,RedirectAttributes ra){ l.setEmployee(h.current(auth)); l.setStatus(LeaveRequest.LeaveStatus.PENDING); leaveS.save(l); ra.addFlashAttribute("success","Leave submitted"); return "redirect:/employee/leaves"; }
    @PostMapping("/{id}/cancel") public String cancel(@PathVariable Long id,Authentication auth,RedirectAttributes ra){ LeaveRequest l=leaveS.findById(id); if(l.getEmployee().getId().equals(h.current(auth).getId())&&l.getStatus()==LeaveRequest.LeaveStatus.PENDING){leaveS.delete(id);ra.addFlashAttribute("success","Cancelled");} return "redirect:/employee/leaves"; }
}