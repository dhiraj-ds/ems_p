package com.ems.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name="employees") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Employee {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String firstName, lastName;
    @Column(nullable=false,unique=true) private String email;
    private String phone, position, gender, address;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="department_id") private Department department;
    private LocalDate joinDate;
    private Double basicSalary;
    @Enumerated(EnumType.STRING) @Builder.Default private EmpStatus status = EmpStatus.ACTIVE;
    @OneToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id") private User user;
    public String getFullName(){ return firstName+" "+lastName; }
    public enum EmpStatus { ACTIVE, INACTIVE, ON_LEAVE }
}