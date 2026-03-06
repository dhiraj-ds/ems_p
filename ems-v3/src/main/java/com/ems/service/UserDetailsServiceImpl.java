package com.ems.service;
import com.ems.entity.User;
import com.ems.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u=repo.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Not found: "+username));
        return new org.springframework.security.core.userdetails.User(u.getUsername(),u.getPassword(),List.of(new SimpleGrantedAuthority(u.getRole().name())));
    }
}