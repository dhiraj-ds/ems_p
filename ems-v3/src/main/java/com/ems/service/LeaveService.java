package com.ems.service;
import com.ems.entity.LeaveRequest;
import com.ems.entity.LeaveRequest.LeaveStatus;
import com.ems.repository.LeaveRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional
public class LeaveService {
    private final LeaveRequestRepository repo;
    public List<LeaveRequest> findAll(){ return repo.findAllByOrderByFromDateDesc(); }
    public List<LeaveRequest> findByEmployee(Long id){ return repo.findByEmployeeIdOrderByFromDateDesc(id); }
    public List<LeaveRequest> findPending(){ return repo.findByStatusOrderByFromDateDesc(LeaveStatus.PENDING); }
    public LeaveRequest findById(Long id){ return repo.findById(id).orElseThrow(()->new RuntimeException("Not found")); }
    public LeaveRequest save(LeaveRequest l){ return repo.save(l); }
    public void delete(Long id){ repo.deleteById(id); }
    public void approve(Long id){ findById(id).setStatus(LeaveStatus.APPROVED); }
    public void reject(Long id,String r){ LeaveRequest l=findById(id); l.setStatus(LeaveStatus.REJECTED); l.setRemarks(r); }
    public long countPending(){ return repo.countByStatus(LeaveStatus.PENDING); }
    public long countApproved(){ return repo.countByStatus(LeaveStatus.APPROVED); }
}