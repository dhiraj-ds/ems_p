package com.ems.controller.admin;
import com.ems.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/admin/leaves") @RequiredArgsConstructor
public class AdminLeaveController {
    private final LeaveService leaveS;
    @GetMapping public String list(Model m){ m.addAttribute("leaves",leaveS.findAll()); m.addAttribute("activePage","leaves"); return "admin/leaves/list"; }
    @GetMapping("/pending") public String pending(Model m){ m.addAttribute("leaves",leaveS.findPending()); m.addAttribute("activePage","leaves"); return "admin/leaves/pending"; }
    @PostMapping("/{id}/approve") public String approve(@PathVariable Long id,RedirectAttributes ra){ leaveS.approve(id); ra.addFlashAttribute("success","Leave approved"); return "redirect:/admin/leaves/pending"; }
    @PostMapping("/{id}/reject") public String reject(@PathVariable Long id,@RequestParam(defaultValue="")String remarks,RedirectAttributes ra){ leaveS.reject(id,remarks); ra.addFlashAttribute("info","Leave rejected"); return "redirect:/admin/leaves/pending"; }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,RedirectAttributes ra){ leaveS.delete(id); ra.addFlashAttribute("success","Deleted"); return "redirect:/admin/leaves"; }
}