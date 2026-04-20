package com.ems.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
@Entity @Table(name="attendances") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Attendance {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="employee_id",nullable=false) private Employee employee;
    private LocalDate date;
    private LocalTime checkIn, checkOut;
    @Enumerated(EnumType.STRING) @Builder.Default private AttStatus status = AttStatus.PRESENT;
    private String remarks;
    public enum AttStatus { PRESENT, ABSENT, LATE, HALF_DAY, ON_LEAVE }
}