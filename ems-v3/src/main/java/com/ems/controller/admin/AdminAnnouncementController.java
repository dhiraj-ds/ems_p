package com.ems.controller.admin;
import com.ems.entity.Announcement;
import com.ems.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
@Controller @RequestMapping("/admin/announcements") @RequiredArgsConstructor
public class AdminAnnouncementController {
    private final AnnouncementService s;
    @GetMapping public String list(Model m){ m.addAttribute("announcements",s.findAll()); m.addAttribute("activePage","announcements"); return "admin/announcements/list"; }
    @GetMapping("/add") public String add(Model m){ m.addAttribute("announcement",new Announcement()); m.addAttribute("activePage","announcements"); return "admin/announcements/form"; }
    @PostMapping("/save") public String save(@ModelAttribute Announcement a,Authentication auth,RedirectAttributes ra){ a.setPostDate(LocalDate.now()); a.setPostedBy(auth.getName()); s.save(a); ra.addFlashAttribute("success","Posted"); return "redirect:/admin/announcements"; }
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model m){ m.addAttribute("announcement",s.findById(id)); m.addAttribute("activePage","announcements"); return "admin/announcements/form"; }
    @PostMapping("/{id}/update") public String update(@PathVariable Long id,@ModelAttribute Announcement a,Authentication auth,RedirectAttributes ra){ a.setId(id); a.setPostDate(LocalDate.now()); a.setPostedBy(auth.getName()); s.save(a); ra.addFlashAttribute("success","Updated"); return "redirect:/admin/announcements"; }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,RedirectAttributes ra){ s.delete(id); ra.addFlashAttribute("success","Deleted"); return "redirect:/admin/announcements"; }
}