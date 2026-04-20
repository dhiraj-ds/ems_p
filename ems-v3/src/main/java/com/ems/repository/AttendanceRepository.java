package com.ems.repository;
import com.ems.entity.Attendance;
import com.ems.entity.Attendance.AttStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface AttendanceRepository extends JpaRepository<Attendance,Long> {

    @Query("SELECT a FROM Attendance a JOIN FETCH a.employee e LEFT JOIN FETCH e.department " +
           "WHERE a.date = :d")
    List<Attendance> findByDate(@Param("d") LocalDate d);

    @Query("SELECT a FROM Attendance a JOIN FETCH a.employee e LEFT JOIN FETCH e.department " +
           "WHERE e.id = :id AND a.date BETWEEN :from AND :to ORDER BY a.date DESC")
    List<Attendance> findByEmployeeIdAndDateBetweenOrderByDateDesc(
            @Param("id") Long id, @Param("from") LocalDate from, @Param("to") LocalDate to);

    long countByDateAndStatus(LocalDate d, AttStatus s);

    /** Used by EmployeeService.delete() to clean up before removing an employee. */
    @Modifying
    @Query("DELETE FROM Attendance a WHERE a.employee.id = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") Long employeeId);
}
