package org.marketplace.requests;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(EntityExistsException.class)
    protected ResponseEntity<ErrorResponse> handleEntityExists(EntityExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FOUND.value(), ex.getMessage());
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        String errorMessage = ex.getMessage();
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
        String errorMessage = ex.getMessage();
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEntryException(DuplicateEntryException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException ex) {
        String errorMessage = ex.getMessage();
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorMessage);
    }

    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<String> handleMailSendException(MailSendException ex) {
        String errorMessage = ex.getMessage();
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        String errorMessage = ex.getMessage();
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        String errorMessage = ex.getMessage();
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<String> handleAuthenticationServiceException(AuthenticationServiceException ex) {
        logger.error(ex.getClass() + ": " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    /*

     I could not find a solution to this problem - I cannot override the ResponseEntityExceptionHandler

     */
//    @Override
//    protected ResponseEntity<Object> handleMethodArgumentNotValid(
//            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
//        List<String> errorMessages = ex
//                .getBindingResult()
//                .getFieldErrors()
//                .stream()
//                .map(fieldError -> fieldError.getDefaultMessage())
//                .collect(Collectors.toList());
//
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMessages);
//
//        return handleExceptionInternal(ex, errorResponse, headers, HttpStatus.BAD_REQUEST, request);
//    }
}