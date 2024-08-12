package com.echo.acknowledgehub.repository;


import com.echo.acknowledgehub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository <Employee,Long>{
   Optional<Employee> findByEmail (String email);

   @Query ("select em from Employee em where em.telegramUsername=?1")
   Employee findByTelegramUsername (String username);

   @Query ("select em.telegramUserId from Employee em where em.telegramUsername=?1")
   Long getTelegramChatId (String username);

   @Modifying
   @Query ("update Employee em set em.telegramUserId=?1 where em.telegramUsername=?2")
   int updateTelegramUserId(Long telegramUserId, String telegramUsername);


}
