package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import com.echo.acknowledgehub.constant.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.logging.Logger;

@Data
@Entity
@Table(name = "employee")
public class Employee implements UserDetails {
    private static final Logger LOGGER = Logger.getLogger(Employee.class.getName());
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;
    @Column(name = "telegram_username", unique = true, columnDefinition = "VARCHAR(25)")
    private String telegramUsername;
    @Column(name = "telegram_user_id", unique = true, columnDefinition = "BIGINT")
    private Long telegramUserId;
    @Column(name = "email", unique = true, nullable = false, columnDefinition = "VARCHAR(100)")
    private String email;
    @Column(name = "staff_id", unique = true, columnDefinition = "VARCHAR(12)")
    private String staffId;
    @Column(name = "nrc", unique = true, columnDefinition = "VARCHAR(20)")
    private String nrc;
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(75)")
    private String name;
    @Column(name = "password", columnDefinition = "VARCHAR(200)")
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "ENUM('ADMIN', 'MAIN_HR', 'MAIN_HR_ASSISTANCE', 'HR', 'HR_ASSISTANCE', 'STAFF')")
    private EmployeeRole role;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('DEFAULT', 'ACTIVATED', 'DEACTIVATED', 'DEPARTED')")
    private EmployeeStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", columnDefinition = "ENUM('MALE', 'FEMALE', 'CUSTOM')")
    private Gender gender;
    @Column(name = "dob", columnDefinition = "DATE")
    private Date dob;
    @Column(name = "photo_link", columnDefinition = "MEDIUMBLOB")
    private byte[] photoLink;
    @Column(name = "address", columnDefinition = "VARCHAR(125)")
    private String address;
    @Column(name = "work_entry_date", columnDefinition = "DATE")
    private Date workEntryDate;
    @Column(name = "noted_count",  nullable = false )
    private int notedCount = 0;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "department_id")
    private Department department;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id")
    private Company company;

    @PrePersist
    private void prePersist(){
        this.status=EmployeeStatus.ACTIVATED;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(this.getRole().name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
