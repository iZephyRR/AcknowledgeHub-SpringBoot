package com.echo.acknowledgehub.util;

import com.echo.acknowledgehub.bean.CheckingBean;
import com.echo.acknowledgehub.exception_handler.SessionExpireException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

@Component
@AllArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = Logger.getLogger(JWTAuthenticationFilter.class.getName());
    private final JWTService JWT_SERVICE;
    private final UserDetailsService USER_DETAILS_SERVICE;
    private final CheckingBean CHECKING_BEAN;
    private final BaseURL BASE_URL;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        //LOGGER.info("Token : "+authHeader.substring(7));
        //LOGGER.info("BaseUrl : " + BASE_URL);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            LOGGER.warning("Unauthorized request for : " + authHeader);
            CHECKING_BEAN.refresh();
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            String username = JWT_SERVICE.extractId(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = USER_DETAILS_SERVICE.loadUserByUsername(username);
                if (JWT_SERVICE.isValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                } else {
                    CHECKING_BEAN.refresh();
                }

            }
        } catch (ExpiredJwtException e) {
            LOGGER.severe(e.getMessage());
            throw new SessionExpireException();
        }
        filterChain.doFilter(request, response);
    }
}
