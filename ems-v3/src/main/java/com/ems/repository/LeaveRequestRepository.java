package com.ems.repository;
import com.ems.entity.LeaveRequest;
import com.ems.entity.LeaveRequest.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,Long> {

    @Query("SELECT l FROM LeaveRequest l JOIN FETCH l.employee e LEFT JOIN FETCH e.department " +
           "WHERE e.id = :id ORDER BY l.fromDate DESC")
    List<LeaveRequest> findByEmployeeIdOrderByFromDateDesc(@Param("id") Long id);

    @Query("SELECT l FROM LeaveRequest l JOIN FETCH l.employee e LEFT JOIN FETCH e.department " +
           "WHERE l.status = :s ORDER BY l.fromDate DESC")
    List<LeaveRequest> findByStatusOrderByFromDateDesc(@Param("s") LeaveStatus s);

    @Query("SELECT l FROM LeaveRequest l JOIN FETCH l.employee e LEFT JOIN FETCH e.department " +
           "ORDER BY l.fromDate DESC")
    List<LeaveRequest> findAllByOrderByFromDateDesc();

    long countByStatus(LeaveStatus s);

    /** Used by EmployeeService.delete() to clean up before removing an employee. */
    @Modifying
    @Query("DELETE FROM LeaveRequest l WHERE l.employee.id = :employeeId")
    void deleteByEmployeeId(@Param("employeeId") Long employeeId);
}
