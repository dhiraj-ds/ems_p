package com.ems.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity @Table(name="payrolls") @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payroll {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="employee_id",nullable=false) private Employee employee;
    private Integer payrollMonth, payrollYear;
    private Double basicSalary, netPay;
    @Builder.Default private Double allowances=0.0, deductions=0.0;
    private String paymentMethod;
    private LocalDate paymentDate;
    @Enumerated(EnumType.STRING) @Builder.Default private PayStatus status = PayStatus.PENDING;
    @PrePersist @PreUpdate public void calcNetPay(){
        double b=basicSalary==null?0:basicSalary,a=allowances==null?0:allowances,d=deductions==null?0:deductions;
        this.netPay=b+a-d;
    }
    public enum PayStatus { PENDING, PAID, CANCELLED }
    public String getMonthName(){
        if(payrollMonth==null)return "";
        String[]m={"","January","February","March","April","May","June","July","August","September","October","November","December"};
        return payrollMonth>=1&&payrollMonth<=12?m[payrollMonth]:"";
    }
}