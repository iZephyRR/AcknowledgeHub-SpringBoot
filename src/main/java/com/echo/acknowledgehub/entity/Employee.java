package com.echo.acknowledgehub.entity;

import com.echo.acknowledgehub.constant.*;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Entity
@Table(name = "employee")
public class Employee implements UserDetails {
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
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "ENUM('ADMIN', 'MAIN_HR', 'MAIN_HR_ASSISTANCE', 'HR', 'HR_ASSISTANCE', 'STUFF')")
    private EmployeeRole role;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "ENUM('ACTIVATED', 'DEACTIVATED', 'DEPARTED') DEFAULT 'ACTIVATED'")
    private EmployeeStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, columnDefinition = "ENUM('MALE', 'FEMALE', 'CUSTOM')")
    private Gender gender;
    @Column(name = "dob", nullable = false, columnDefinition = "DATE")
    private Date dob;
    @Column(name = "photo_link", columnDefinition = "VARCHAR(125)")
    private String photoLink;
    @Column(name = "address", columnDefinition = "VARCHAR(125)")
    private String address;
    @Column(name = "work_entry_date", nullable = false, columnDefinition = "DATE")
    private Date workEntryDate;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id")
    private Company company;


    //  @Override
//  public Collection<? extends GrantedAuthority> getAuthorities() {
//    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
//    authorities.add(new SimpleGrantedAuthority("ROLE_"+this.role.name()));
//
//    return authorities;
//  }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority(this.getRole().name()));
        System.out.println("Granted Authorities: " + authorities);
        return authorities;
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
