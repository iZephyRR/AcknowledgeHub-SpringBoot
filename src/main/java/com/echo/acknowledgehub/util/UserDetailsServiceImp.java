package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.persistence.entity.Employee;
import com.echo.acknowledgehub.persistence.repository.EmployeeRepository;
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

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Optional<Employee> optionalEmployee;
        if(isEmail(input)){
            optionalEmployee = EMPLOYEE_REPOSITORY.findByEmail(input);
        }else {
            optionalEmployee = EMPLOYEE_REPOSITORY.findById(Long.parseLong(input));
        }
        if (optionalEmployee.isEmpty()) {
            throw new UsernameNotFoundException("User not found by : "+ input);
        }else {
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + optionalEmployee.get().getRole()));
            //LOGGER.info("Authority : "+authorities);
            return new User(optionalEmployee.get().getId().toString(),optionalEmployee.get().getPassword(),authorities);
        }
    }

    private boolean isEmail(String input){
        try{
            Long.parseLong(input);
            return false;
        }catch (NumberFormatException e){
            return true;
        }
    }
}
