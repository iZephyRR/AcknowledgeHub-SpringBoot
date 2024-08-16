package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.constant.EmployeeStatus;
import com.echo.acknowledgehub.controller.EmployeeController;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class JWTService {

    private static final Logger LOGGER = Logger.getLogger(JWTService.class.getName());
    private final CheckingBean CHECKING_BEAN;

    private SecretKey getSignInKey() {
        final String SECRET_KEY = "1c5644d5b85c1d0a06f470f95b24347c311226902d63397f7512a33329d2c02e";
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String id) {
        return Jwts.builder()
                .subject(id)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+60*60*1000))
                .signWith(getSignInKey())
                .compact();
    }

    private Claims extractAllClaims(String token){
            return Jwts
                    .parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> resolver){
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // take id from token
    public String extractId(String token){
        return extractClaim(token, Claims::getSubject);
    }
    //Please don't delete following command...!
//    // take empId from token
//    public Integer extractEmpId (String token) {
//        Claims claims = extractAllClaims(token);
//        return (int) claims.get("employeeId");
//    }
//
//    // take role from token
//    public EmployeeRole extractRole (String token) {
//        Claims claims = extractAllClaims(token);
//        String role = claims.get("role", String.class);
//        return EmployeeRole.valueOf(role);
//    }

    // take exp-time
    public Date extractExpiration(String token) { return extractClaim(token , Claims::getExpiration); }

    // check token is exp or not
    public boolean isTokenExpired(String token) { return extractExpiration(token).before(new Date()); }

    // check email from token
    public boolean isValid(String token, UserDetails user){
        return (extractId(token).equals(user.getUsername())) && !isTokenExpired(token) && CHECKING_BEAN.getStatus() == EmployeeStatus.ACTIVATED;
    }

}
