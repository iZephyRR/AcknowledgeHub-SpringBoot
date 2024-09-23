package com.echo.acknowledgehub.exception_handler;

import com.echo.acknowledgehub.dto.ErrorResponseDTO;
import jakarta.mail.MessagingException;
import org.modelmapper.ConfigurationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionHandler.class.getName());
    private static final Map<Class<? extends Exception>, ErrorResponseDTO> STATUS_MAP = new HashMap<>();
    private static final ErrorResponseDTO ERROR_RESPONSE = new ErrorResponseDTO();

    static {
        STATUS_MAP.put(EmailSenderException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), null));
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
        STATUS_MAP.put(NoResourceFoundException.class, new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), "This rout cannot be reach."));
        STATUS_MAP.put(HttpRequestMethodNotSupportedException.class, new ErrorResponseDTO(HttpStatus.METHOD_NOT_ALLOWED.value(), null));
        STATUS_MAP.put(DuplicatedEnteryException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), null));
        STATUS_MAP.put(CompletionException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), null));
        STATUS_MAP.put(UpdatePasswordException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), null));
        STATUS_MAP.put(DataNotFoundException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), null));
        STATUS_MAP.put(MaxUploadSizeExceededException.class, new ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), null));
        STATUS_MAP.put(UnknownHostException.class, new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Please try again later"));
        STATUS_MAP.put(NoSuchElementException.class, new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        STATUS_MAP.put(IllegalArgumentException.class, new ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), null));
        STATUS_MAP.put(RestingSystemException.class, new ErrorResponseDTO(HttpStatus.SERVICE_UNAVAILABLE.value(), null));


        //Can add more exception that you want to handle.
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        LOGGER.severe("Exception : "+ex);
        ErrorResponseDTO errorResponse = STATUS_MAP.getOrDefault(ex.getClass(), new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred. Please try again later."
        ));
        ERROR_RESPONSE.setErrorCode(errorResponse.getErrorCode());
        if(errorResponse.getMessage()==null){
            ERROR_RESPONSE.setMessage(ex.getMessage());
        }else {
            ERROR_RESPONSE.setMessage(errorResponse.getMessage());
        }
        ResponseEntity<ErrorResponseDTO> responseDTOResponseEntity=new ResponseEntity<>(ERROR_RESPONSE, HttpStatus.valueOf(ERROR_RESPONSE.getErrorCode()));
        System.out.println("Response : "+responseDTOResponseEntity);
        return responseDTOResponseEntity;
    }

}
