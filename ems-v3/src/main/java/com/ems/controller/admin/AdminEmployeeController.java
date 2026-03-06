package com.ems.controller.admin;
import com.ems.entity.*;
import com.ems.repository.UserRepository;
import com.ems.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller @RequestMapping("/admin/employees") @RequiredArgsConstructor
public class AdminEmployeeController {
    private final EmployeeService empS; private final DepartmentService deptS;
    private final UserRepository userRepo; private final PasswordEncoder enc;
    private void base(Model m){ m.addAttribute("departments",deptS.findAllSimple()); m.addAttribute("activePage","employees"); }
    @GetMapping public String list(@RequestParam(required=false) String q,Model m){
        m.addAttribute("employees",empS.search(q)); m.addAttribute("query",q); base(m); return "admin/employees/list"; }
    @GetMapping("/add") public String addForm(Model m){ m.addAttribute("employee",new Employee()); base(m); return "admin/employees/form"; }
    @PostMapping("/save") public String save(@ModelAttribute Employee emp,@RequestParam(required=false) Long departmentId,
                                              @RequestParam String username,@RequestParam String password,RedirectAttributes ra){
        if(empS.emailExists(emp.getEmail())){ra.addFlashAttribute("error","Email already exists");return "redirect:/admin/employees/add";}
        if(userRepo.existsByUsername(username)){ra.addFlashAttribute("error","Username taken");return "redirect:/admin/employees/add";}
        if(departmentId!=null)emp.setDepartment(deptS.findById(departmentId));
        User u=userRepo.save(User.builder().username(username).password(enc.encode(password)).role(Role.ROLE_EMPLOYEE).build());
        emp.setUser(u); empS.save(emp); ra.addFlashAttribute("success","Employee added"); return "redirect:/admin/employees"; }
    @GetMapping("/{id}/edit") public String edit(@PathVariable Long id,Model m){ m.addAttribute("employee",empS.findById(id)); base(m); return "admin/employees/form"; }
    @PostMapping("/{id}/update") public String update(@PathVariable Long id,@ModelAttribute Employee emp,@RequestParam(required=false)Long departmentId,RedirectAttributes ra){
        Employee ex=empS.findById(id);
        if(!ex.getEmail().equals(emp.getEmail())&&empS.emailExistsForOther(emp.getEmail(),id)){ra.addFlashAttribute("error","Email in use");return "redirect:/admin/employees/"+id+"/edit";}
        if(departmentId!=null)emp.setDepartment(deptS.findById(departmentId));
        emp.setId(id); emp.setUser(ex.getUser()); empS.save(emp); ra.addFlashAttribute("success","Employee updated"); return "redirect:/admin/employees"; }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id,RedirectAttributes ra){ empS.delete(id); ra.addFlashAttribute("success","Deleted"); return "redirect:/admin/employees"; }
}