package com.ems.repository;
import com.ems.entity.Employee;
import com.ems.entity.Employee.EmpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    Optional<Employee> findByUserId(Long userId);
    long countByStatus(EmpStatus status);

    /** Eager-load user in the same query — avoids LazyInitializationException
     *  when open-in-view=false and the transaction is already closed. */
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user LEFT JOIN FETCH e.department WHERE e.user.username = :username")
    Optional<Employee> findByUserUsername(@Param("username") String username);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user LEFT JOIN FETCH e.department")
    List<Employee> findAllWithAssociations();

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user LEFT JOIN FETCH e.department " +
           "WHERE LOWER(CONCAT(e.firstName,' ',e.lastName)) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(e.email) LIKE LOWER(CONCAT('%',:q,'%')) " +
           "OR LOWER(COALESCE(e.position,'')) LIKE LOWER(CONCAT('%',:q,'%'))")
    List<Employee> search(@Param("q") String q);
}