package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.dto.EmployeeProfileDTO;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.UniqueFieldsDTO;
import com.echo.acknowledgehub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    @Query("SELECT new com.echo.acknowledgehub.dto.EmployeeProfileDTO(em, em.company.id, em.company.name, em.department.id, em.department.name) FROM Employee em WHERE em.id = :id")
    EmployeeProfileDTO findByIdForProfile(@Param("id") Long id);

    @Query("select em from Employee em where em.telegramUsername= :username")
    Employee findByTelegramUsername(@Param("username") String username);

    @Query("select em.telegramUserId from Employee em where em.telegramUsername= :username")
    Long getTelegramChatId(@Param("username") String username);

    @Query("select em.telegramUserId from Employee em where em.id= :userId")
    Long getTelegramChatIdByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("update Employee em set em.telegramUserId= :telegramUserId where em.telegramUsername= :telegramUsername")
    int updateTelegramUserId(@Param("telegramUserId") Long telegramUserId, @Param("telegramUsername") String telegramUsername);

    @Query("select em.telegramUserId from Employee em")
    List<Long> getAllChatId();

    @Modifying
    @Query("UPDATE Employee em set em.password= :defaultPassword WHERE em.status= 'DEFAULT'")
    int changeDefaultPassword(@Param("defaultPassword") String encodedDefaultPassword);

    @Modifying
    @Query("UPDATE Employee em SET em.password= :newPassword WHERE em.email= :email")
    int updatePasswordByEmail(@Param("email") String email, @Param("newPassword") String password);

    @Modifying
    @Query("UPDATE Employee em SET em.password= :newPassword WHERE em.id= :id")
    int updatePasswordById(@Param("id") Long id, @Param("newPassword") String password);

    @Query("SELECT em.password FROM Employee em WHERE em.email= :email")
    String getPasswordByEmail(@Param("email") String email);

    @Query("select em.id from Employee em where em.department.id = :departmentId")
    List<Long> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("select em.id from Employee em where em.company.id = :companyId")
    List<Long> findByCompanyId(@Param("companyId") Long companyId);

    boolean existsByEmail(String email);

    @Query("SELECT em.name from Employee em WHERE em.email = :email")
    String findNameByEmail(@Param("email") String email);

    @Query("select em from Employee em where em.department.id = :departmentId")
    List<Employee> getByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("select em.telegramUserId from Employee em where em.company.id = :companyId")
    List<Long> getAllChatIdByCompanyId(@Param("companyId") Long companyId);

    @Query("select em.telegramUserId from Employee em where em.department.id = :departmentId")
    List<Long> getAllChatIdByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT e.id FROM Employee e WHERE e.telegramUsername = :telegramUsername")
    Long getEmployeeIdByTelegramUsername(@Param("telegramUsername") String telegramUsername);

    @Query("SELECT e.name, e.email, e.address, e.dob, e.gender, e.nrc, e.password, e.role, e.status, e.staffId, e.telegramUsername, e.workEntryDate, c.name AS companyName, d.name AS departmentName " +
            "FROM Employee e " +
            "JOIN e.company c " +
            "JOIN e.department d")
    List<Object[]> getAllUsers();

    @Query("SELECT e FROM Employee e WHERE e.role IN :roles")
    List<Employee> findAllByRole(@Param("roles") List<EmployeeRole> roles);

    @Query("SELECT DISTINCT e.email FROM Employee e")
    List<String> findDistinctEmails();

    @Query("SELECT DISTINCT e.nrc FROM Employee e")
    List<String> findDistinctNrc();

    @Query("SELECT DISTINCT e.staffId FROM Employee e")
    List<String> findDistinctStaffIds();

    @Query("SELECT DISTINCT e.telegramUsername FROM Employee e")
    List<String> findDistinctTelegramUsernames();

}

