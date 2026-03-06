package com.ems.repository;
import com.ems.entity.Payroll;
import com.ems.entity.Payroll.PayStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface PayrollRepository extends JpaRepository<Payroll,Long> {

    @Query("SELECT p FROM Payroll p JOIN FETCH p.employee e LEFT JOIN FETCH e.department " +
           "WHERE p.payrollMonth = :m AND p.payrollYear = :y")
    List<Payroll> findByPayrollMonthAndPayrollYear(@Param("m") int m, @Param("y") int y);

    @Query("SELECT p FROM Payroll p JOIN FETCH p.employee e LEFT JOIN FETCH e.department " +
           "WHERE e.id = :id ORDER BY p.payrollYear DESC, p.payrollMonth DESC")
    List<Payroll> findByEmployeeIdOrderByPayrollYearDescPayrollMonthDesc(@Param("id") Long id);

    long countByStatus(PayStatus s);

    @Query("SELECT COALESCE(SUM(p.netPay),0) FROM Payroll p WHERE p.payrollMonth=:m AND p.payrollYear=:y")
    Double totalForMonth(@Param("m") int m, @Param("y") int y);

    @Query("SELECT COALESCE(SUM(p.netPay),0) FROM Payroll p WHERE p.payrollMonth=:m AND p.payrollYear=:y AND p.status='PAID'")
    Double paidForMonth(@Param("m") int m, @Param("y") int y);
}