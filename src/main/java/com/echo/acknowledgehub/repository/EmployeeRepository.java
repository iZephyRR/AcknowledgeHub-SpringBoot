package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface EmployeeRepository extends JpaRepository <Employee,Long>{
   Optional<Employee> findByEmail (String email);

   @Query("select em from Employee em where em.telegramUsername= :username")
   Employee findByTelegramUsername (@Param("username")String username);

   @Query ("select em.telegramUserId from Employee em where em.telegramUsername= :username")
   Long getTelegramChatId (@Param("username")String username);

   @Modifying
   @Query ("update Employee em set em.telegramUserId= :telegramUserId where em.telegramUsername= :telegramUsername")
   int updateTelegramUserId(@Param("telegramUserId")Long telegramUserId,@Param("telegramUsername") String telegramUsername);

   @Query("select em.telegramUserId from Employee em")
   List<Long> getAllChatId();

   @Query("SELECT em.password from Employee em where em.id= :id")
   String getPasswordById(@Param("id") Long id);

   @Modifying
   @Query("UPDATE Employee em SET em.password= :newPassword WHERE em.id= :id")
   int updatePassword(@Param("id") Long id,@Param("newPassword")String password);

   @Query("select em.id from Employee em where em.department.id = :departmentId")
   List<Long> findByDepartmentId(@Param("departmentId") Long departmentId);

   @Query("select em.id from Employee em where em.company.id = :companyId")
   List<Long> findByCompanyId(@Param("companyId") Long companyId);

   @Query("select em from Employee em where em.department.id = :departmentId")
   List<Employee> getByDepartmentId(@Param("departmentId") Long departmentId);

   @Query("select em.telegramUserId from Employee em where em.company.id = :companyId")
   List<Long> getAllChatIdByCompanyId(@Param("companyId") Long companyId);

   @Query("select em.telegramUserId from Employee em where em.department.id = :departmentId")
   List<Long> getAllChatIdByDepartmentId(@Param("departmentId") Long departmentId);

   @Query("SELECT e.id FROM Employee e WHERE e.telegramUsername = :telegramUsername")
   Long getEmployeeIdByTelegramUsername(@Param("telegramUsername") String telegramUsername);

}
