package com.ems.service;
import com.ems.entity.Payroll;
import com.ems.entity.Payroll.PayStatus;
import com.ems.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional
public class PayrollService {
    private final PayrollRepository repo;
    public List<Payroll> findAll(){ return repo.findAll(); }
    public List<Payroll> findByMonthYear(int m,int y){ return repo.findByPayrollMonthAndPayrollYear(m,y); }
    public List<Payroll> findByEmployee(Long id){ return repo.findByEmployeeIdOrderByPayrollYearDescPayrollMonthDesc(id); }
    public Payroll findById(Long id){ return repo.findById(id).orElseThrow(()->new RuntimeException("Not found")); }
    public Payroll save(Payroll p){ return repo.save(p); }
    public void delete(Long id){ repo.deleteById(id); }
    public void markPaid(Long id){ Payroll p=findById(id); p.setStatus(PayStatus.PAID); p.setPaymentDate(LocalDate.now()); }
    public long countPending(){ return repo.countByStatus(PayStatus.PENDING); }
    public double totalForMonth(int m,int y){ Double r=repo.totalForMonth(m,y); return r==null?0:r; }
    public double paidForMonth(int m,int y){ Double r=repo.paidForMonth(m,y); return r==null?0:r; }
}