package com.echo.acknowledgehub.exception_handler;

import com.echo.acknowledgehub.dto.ErrorResponseDTO;
import org.modelmapper.ConfigurationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());
    private static final Map<Class<? extends Exception>, ErrorResponseDTO> STATUS_MAP = new HashMap<>();

    //Can add more exception that you want to handle.
    static {
        STATUS_MAP.put(EmailSenderException.class, new ErrorResponseDTO(HttpStatus.NO_CONTENT.value(), null));
        STATUS_MAP.put(XlsxReaderException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), null));
        STATUS_MAP.put(NullPointerException.class, new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later."));
        STATUS_MAP.put(BadCredentialsException.class, new ErrorResponseDTO(HttpStatus.FORBIDDEN.value(), "Invalid email or password."));
        STATUS_MAP.put(UsernameNotFoundException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), "An unexpected error occurred. Please try again later."));
        STATUS_MAP.put(UserRegistrationException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), null));
        STATUS_MAP.put(ConfigurationException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), "Failed to map object."));
        STATUS_MAP.put(DataIntegrityViolationException.class, new ErrorResponseDTO(HttpStatus.NOT_ACCEPTABLE.value(), "Incorrect input."));
        STATUS_MAP.put(IOException.class, new ErrorResponseDTO(HttpStatus.NOT_ACCEPTABLE.value(), "Incorrect input."));
        STATUS_MAP.put(SessionExpireException.class, new ErrorResponseDTO(HttpStatus.NOT_ACCEPTABLE.value(), null));
        STATUS_MAP.put(AsyncRequestTimeoutException.class, new ErrorResponseDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), "Request timeout. Please try again later."));
        STATUS_MAP.put(UserDeactivatedException.class, new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), null));
        STATUS_MAP.put(InternalAuthenticationServiceException.class, new ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), null));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        LOGGER.severe(ex.toString());
        ErrorResponseDTO errorResponse = STATUS_MAP.getOrDefault(ex.getClass(), new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later."
        ));
        if(errorResponse.getMessage()==null){
            errorResponse.setMessage(ex.getMessage());
        }
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getErrorCode()));
    }
}
