package com.ems.service;
import com.ems.entity.Announcement;
import com.ems.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @RequiredArgsConstructor @Transactional
public class AnnouncementService {
    private final AnnouncementRepository repo;
    public List<Announcement> findAll(){ return repo.findAllByOrderByPostDateDesc(); }
    public Announcement findById(Long id){ return repo.findById(id).orElseThrow(()->new RuntimeException("Not found")); }
    public Announcement save(Announcement a){ return repo.save(a); }
    public void delete(Long id){ repo.deleteById(id); }
    public long count(){ return repo.count(); }
}