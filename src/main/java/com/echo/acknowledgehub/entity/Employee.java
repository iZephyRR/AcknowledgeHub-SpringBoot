package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "employee")
public class Employee {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String telegramUsername;
  private String telegramUserId;
  private String email;
  private String stuffId;
  private String name;
  private String password;
  private EmployeeRole role;
  private EmployeeStatus status;
  private byte[] photo;
  private String address;
  private String workEntryDate;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "department", nullable = false)
  private Department department;
  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "company", nullable = false)
  private Company company;

}
