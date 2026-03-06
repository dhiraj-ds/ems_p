package com.ems.controller.admin;
import com.ems.entity.Department;
import com.ems.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/admin/departments") @RequiredArgsConstructor
public class AdminDepartmentController {
    private final DepartmentService s;
    @GetMapping public String list(Model m){ m.addAttribute("departments",s.findAll()); m.addAttribute("activePage","departments"); return "admin/departments/list"; }
    @GetMapping("/add") public String add(Model m){ m.addAttribute("department",new Department()); m.addAttribute("activePage","departments"); return "admin/departments/form"; }
    @PostMapping("/save") public String save(@ModelAttribute Department d,RedirectAttributes ra){ if(s.nameExists(d.getName())){ra.addFlashAttribute("error","Dept exists");return "redirect:/admin/departments/add";} s.save(d); ra.addFlashAttribute("success","Added"); return "redirect:/admin/departments"; }
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model m){ m.addAttribute("department",s.findById(id)); m.addAttribute("activePage","departments"); return "admin/departments/form"; }
    @PostMapping("/{id}/update") public String update(@PathVariable Long id,@ModelAttribute Department d,RedirectAttributes ra){ if(s.nameExistsForOther(d.getName(),id)){ra.addFlashAttribute("error","Name in use");return "redirect:/admin/departments/"+id+"/edit";} d.setId(id); s.save(d); ra.addFlashAttribute("success","Updated"); return "redirect:/admin/departments"; }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,RedirectAttributes ra){ s.delete(id); ra.addFlashAttribute("success","Deleted"); return "redirect:/admin/departments"; }
}