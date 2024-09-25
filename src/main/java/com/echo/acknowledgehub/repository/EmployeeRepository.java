package com.echo.acknowledgehub.repository;

import com.echo.acknowledgehub.dto.EmployeeNotedDTO;
import com.echo.acknowledgehub.dto.EmployeeProfileDTO;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.dto.StringResponseDTO;
import com.echo.acknowledgehub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT new com.echo.acknowledgehub.dto.EmployeeProfileDTO(em.name, em.role, em.email, em.company.name, em.department.name, em.photoLink) FROM Employee em WHERE em.id = :id")
    EmployeeProfileDTO getProfileInfo(@Param("id") Long id);

    Optional<Employee> findByEmail(String email);

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

    @Query("SELECT new com.echo.acknowledgehub.dto.StringResponseDTO(e.email) FROM Employee e WHERE e.status='DEFAULT'")
    List<StringResponseDTO> getDefaultAccountEmails();

    @Modifying
    @Query("UPDATE Employee em SET em.password= :newPassword, em.status='ACTIVATED' WHERE em.email= :email")
    int updatePasswordByEmail(@Param("email") String email, @Param("newPassword") String password);

    @Modifying
    @Query("UPDATE Employee em SET em.password= :newPassword WHERE em.id= :id")
    int updatePasswordById(@Param("id") Long id, @Param("newPassword") String password);


//     @Query("SELECT em.password FROM Employee em WHERE em.email= :email")
//     String getPasswordByEmail(@Param("email") String email);

    @Query("select em.id from Employee em where em.department.id = :departmentId")
    List<Long> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("select em.id from Employee em where em.company.id = :companyId")
    List<Long> findByCompanyId(@Param("companyId") Long companyId);

    boolean existsByEmail(String email);

    @Query("SELECT em.password from Employee em WHERE em.email = :email")
    String findPasswordByEmail(@Param("email") String email);

    @Query("SELECT em.password from Employee em WHERE em.id = :id")
    String findPasswordById(@Param("id") Long id);


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


    @Query("SELECT e.name, e.email, e.address, e.dob, e.gender, e.nrc, e.password, e.role, e.status, e.staffId, e.telegramUsername, e.photoLink, c.name AS companyName, d.name AS departmentName ,e.id " +
            "FROM Employee e " +
            "JOIN e.company c " +
            "JOIN e.department d")
    List<Object[]> getAllUsers();

    @Query("SELECT e.name, e.email, e.address, e.dob, e.gender, e.nrc, e.password, e.role, e.status, e.staffId, e.telegramUsername, e.photoLink, c.name AS companyName, d.name AS departmentName ,e.id " +
            "FROM Employee e " +
            "JOIN e.company c " +
            "JOIN e.department d where e.company.id=:companyId")
    List<Object[]> getUserByCompanyId(@Param("companyId") Long companyId);

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

    @Query("SELECT new com.echo.acknowledgehub.dto.EmployeeNotedDTO(e.id,e.name, e.gender, e.role, e.status, e.staffId, c.name AS companyName, d.name AS departmentName) " +
            "FROM Employee e " +
            "JOIN e.company c " +
            "JOIN e.department d WHERE e.id = :id")
    EmployeeNotedDTO getEmployeeById(@Param("id") Long id);

    @Query("Select count(e) From Employee e where e.company.id=:id")
    int getEmployeeCountByCompanyId(@Param("id") Long id);

    @Query("SELECT e.email FROM Employee e WHERE e.company.id= :id")
    List<String> getEmailsByCompanyId(@Param("id") Long id);

    @Query("SELECT e.email FROM Employee e WHERE e.department.id= :id")
    List<String> getEmailsByDepartmentId(@Param("id") Long id);

    @Query("SELECT e.email FROM Employee e WHERE e.id= :id")
    List<String> getEmailsByUserId(@Param("id") Long id);

    @Query("SELECT e FROM Employee e JOIN Announcement a ON e.id = a.employee.id " +
            "WHERE a.id = :announcementId AND e.role IN :roles")
    List<Employee> findEmployeesByRolesAndAnnouncement(
            @Param("roles") List<EmployeeRole> roles,
            @Param("announcementId") Long announcementId);

    @Query("SELECT new com.echo.acknowledgehub.dto.EmployeeProfileDTO(em.name, em.role, em.email, em.photoLink) FROM Employee em WHERE em.id = :id")
    EmployeeProfileDTO getAdminProfileInfo(@Param("id") Long id);

    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.role = 'MAIN_HR'")
    Boolean existsMainHR();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.company.id= :id")
    long countForHR(@Param("id")Long companyId);

    @Query("Select count(e) From Employee e where e.department.id=:id")
    int getEmployeeCountByDepartmentId(@Param("id") Long id);

    @Query("SELECT e.photoLink FROM Employee e WHERE e.id=:id")
    byte[] getPhotoById(@Param("id")Long id);

    @Query("SELECT e.id, e.name, e.notedCount,e.company.name,e.department.name,e.staffId FROM Employee e ORDER BY e.notedCount DESC LIMIT 10")
    List<Object[]> getAscNotedCount();

}

