package com.ems.service;
import com.ems.entity.Attendance;
import com.ems.entity.Attendance.AttStatus;
import com.ems.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional
public class AttendanceService {
    private final AttendanceRepository repo;
    public List<Attendance> findToday(){ return repo.findByDate(LocalDate.now()); }
    public List<Attendance> findByEmpRange(Long id,LocalDate from,LocalDate to){ return repo.findByEmployeeIdAndDateBetweenOrderByDateDesc(id,from,to); }
    public Attendance findById(Long id){ return repo.findById(id).orElseThrow(()->new RuntimeException("Not found")); }
    public Attendance save(Attendance a){ return repo.save(a); }
    public void delete(Long id){ repo.deleteById(id); }
    public long countTodayByStatus(AttStatus s){ return repo.countByDateAndStatus(LocalDate.now(),s); }
}