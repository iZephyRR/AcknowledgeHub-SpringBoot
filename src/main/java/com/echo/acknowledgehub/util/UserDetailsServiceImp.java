package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.EmployeeRole;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import com.echo.acknowledgehub.entity.Employee;
import com.echo.acknowledgehub.exception_handler.UserDeactivatedException;
import com.echo.acknowledgehub.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class UserDetailsServiceImp implements UserDetailsService {
    private static final Logger LOGGER = Logger.getLogger(UserDetailsServiceImp.class.getName());
    private final EmployeeRepository EMPLOYEE_REPOSITORY;
    private final CheckingBean CHECKING_BEAN;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Optional<Employee> optionalEmployee;
        if (isEmail(input)) {
            optionalEmployee = EMPLOYEE_REPOSITORY.findByEmail(input);
        } else {
            optionalEmployee = EMPLOYEE_REPOSITORY.findById(Long.parseLong(input));
        }
        if (optionalEmployee.isEmpty()) {
            throw new UsernameNotFoundException("User not found by : " + input);
        } else if ((optionalEmployee.get().getStatus() != EmployeeStatus.ACTIVATED) && (optionalEmployee.get().getStatus() != EmployeeStatus.DEFAULT)) {
            throw new UserDeactivatedException("This account is " + optionalEmployee.get().getStatus().name() + ".");
        } else {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + optionalEmployee.get().getRole()));
            LOGGER.info("Authority : "+authorities);
            CHECKING_BEAN.setRole(optionalEmployee.get().getRole());
            CHECKING_BEAN.setStatus(optionalEmployee.get().getStatus());
            CHECKING_BEAN.setName(optionalEmployee.get().getName());
            CHECKING_BEAN.setId(optionalEmployee.get().getId());
            if(optionalEmployee.get().getCompany()!=null) {
                CHECKING_BEAN.setCompanyId(optionalEmployee.get().getCompany().getId());
            }
            if(optionalEmployee.get().getDepartment()!=null){
                CHECKING_BEAN.setDepartmentId(optionalEmployee.get().getDepartment().getId());
            }
            LOGGER.info("User Detail "+CHECKING_BEAN);
            return new User(optionalEmployee.get().getId().toString(), optionalEmployee.get().getPassword(), authorities);
        }
    }

    private boolean isEmail(String input) {
        try {
            Long.parseLong(input);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }
}
