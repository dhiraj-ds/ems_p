package com.ems.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
@Entity @Table(name="leave_requests") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaveRequest {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="employee_id",nullable=false) private Employee employee;
    @Enumerated(EnumType.STRING) private LeaveType leaveType;
    private LocalDate fromDate, toDate;
    @Column(columnDefinition="TEXT") private String reason, remarks;
    @Enumerated(EnumType.STRING) @Builder.Default private LeaveStatus status = LeaveStatus.PENDING;
    public long getDays(){ if(fromDate==null||toDate==null)return 0; return ChronoUnit.DAYS.between(fromDate,toDate)+1; }
    public enum LeaveType { ANNUAL, SICK, CASUAL, MEDICAL, MATERNITY, PATERNITY }
    public enum LeaveStatus { PENDING, APPROVED, REJECTED }
}