package com.ems.service;
import com.ems.entity.Department;
import com.ems.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @RequiredArgsConstructor @Transactional
public class DepartmentService {
    private final DepartmentRepository repo;
    public List<Department> findAll(){ return repo.findAllWithEmployees(); }
    public List<Department> findAllSimple(){ return repo.findAll(); }
    public Department findById(Long id){ return repo.findById(id).orElseThrow(()->new RuntimeException("Not found")); }
    public Department save(Department d){ return repo.save(d); }
    public void delete(Long id){ repo.deleteById(id); }
    public long count(){ return repo.count(); }
    public boolean nameExists(String n){ return repo.existsByNameIgnoreCase(n); }
    public boolean nameExistsForOther(String n,Long id){ return repo.existsByNameIgnoreCaseAndIdNot(n,id); }
    public Optional<Department> findByName(String n){ return repo.findByNameIgnoreCase(n); }
}