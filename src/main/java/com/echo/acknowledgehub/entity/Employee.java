package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import com.echo.acknowledgehub.constant.Gender;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "employee")
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", columnDefinition = "BIGINT")
  private Long id;
  @Column(name = "telegram_username", unique = true, columnDefinition = "VARCHAR(25)")
  private String telegramUsername;
  @Column(name = "telegram_user_id", unique = true, columnDefinition = "VARCHAR(25)")
  private String telegramUserId;
  @Column(name = "email", unique = true, nullable = false, columnDefinition = "VARCHAR(100)")
  private String email;
  @Column(name = "stuff_id", unique = true, columnDefinition = "VARCHAR(12)")
  private String stuffId;
  @Column(name = "nrc", unique = true, columnDefinition = "VARCHAR(20)")
  private String nRC;
  @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(75)")
  private String name;
  @Column(name = "password", nullable = false, columnDefinition = "VARCHAR(200)")
  private String password;
  @Column(name = "role", nullable = false, columnDefinition = "ENUM('ADMIN', 'MAIN_HR', 'MAIN_HR_ASSISTANCE', 'HR', 'HR_ASSISTANCE')")
  private EmployeeRole role;
  @Column(name = "status", nullable = false, columnDefinition = "ENUM('ACTIVATED', 'DEACTIVATED', 'DEPARTED') DEFAULT 'ACTIVATED'")
  private EmployeeStatus status;
  @Column(name = "gender", nullable = false, columnDefinition = "ENUM('MALE', 'FEMALE')")
  private Gender gender;
  @Column(name = "dob", nullable = false, columnDefinition = "VARCHAR(45)")
  private String dob;
  @Column(name = "photo_link", columnDefinition = "VARCHAR(125)")
  private String photoLink;
  @Column(name = "address", columnDefinition = "VARCHAR(125)")
  private String address;
  @Column(name = "work_entry_date", nullable = false,columnDefinition = "VARCHAR(20)")
  private String workEntryDate;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "department", nullable = false)
  private Department department;
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "company", nullable = false)
  private Company company;

}
