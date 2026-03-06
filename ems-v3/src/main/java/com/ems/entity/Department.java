package com.ems.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
@Entity @Table(name="departments") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Department {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true) private String name;
    private String head, email, description;
    private Integer numberOfPositions;
    @Enumerated(EnumType.STRING) @Builder.Default private DeptStatus status = DeptStatus.ACTIVE;

    /** Initialize to empty list so getEmployeeCount() never throws NPE. */
    @OneToMany(mappedBy="department", fetch=FetchType.LAZY)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    public enum DeptStatus { ACTIVE, INACTIVE, EXPANDING }

    /** Null-safe employee count. */
    public int getEmployeeCount() {
        return employees == null ? 0 : employees.size();
    }
}