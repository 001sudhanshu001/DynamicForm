package com.learn.exception;

import com.learn.security.exception.JwtSecurityException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Date;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException e) {

        String message = e.getMessage();
        if (StringUtils.containsIgnoreCase(message, "duplicate")) {
            String[] split = message.split("Detail: Key ");
            String detailFullMessage = split[1];
            String detailRelevantMessage = StringUtils.substringBefore(detailFullMessage, ".]");
            return ResponseEntity.status(CONFLICT)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                    .body(ProblemDetail.forStatusAndDetail(CONFLICT, detailRelevantMessage));
        }
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(ProblemDetail.forStatus(INTERNAL_SERVER_ERROR));
    }

    // TODO -> Improve it
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleRuntime(Exception e) {
        String message = e.getMessage();
        log.error("Unexpected => {}", message, e);

        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(ProblemDetail.forStatusAndDetail(INTERNAL_SERVER_ERROR, message));
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ProblemDetail> handleMyException(ApplicationException e, HttpServletRequest request) {
        String userMessage = e.getMessage();
        if (log.isTraceEnabled()) {
            log.trace("Unexpected => " + userMessage, e);
        }
        return ResponseEntity.status(e.getHttpStatus())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(ProblemDetail.forStatusAndDetail(e.getHttpStatus(), userMessage));
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers, HttpStatusCode status,
                                                               WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setType(URI.create(request.getDescription(true)));
        problemDetail.setDetail("Invalid request content.");
        problemDetail.setInstance(URI.create(request.getDescription(false)));

        Object[] detailMessageArguments = ex.getDetailMessageArguments();
        problemDetail.setProperty("failed", detailMessageArguments);

        return ResponseEntity.status(status)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problemDetail);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex,
                                                                            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                new Date(), HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(), request.getDescription(false)
        );
        log.debug("ConstraintViolationException ::", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotMatching(MethodArgumentTypeMismatchException ex,
                                                                   WebRequest request) {
        ErrorResponse errorResponse =
                new ErrorResponse(new Date(), HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage(), request.getDescription(false));
        log.debug("MethodArgumentTypeMismatchException ::", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(Exception exception,
                                                                 WebRequest request){
        ErrorResponse errorResponse =
                new ErrorResponse(new Date(), HttpStatus.UNAUTHORIZED.value(),
                        exception.getMessage(), request.getDescription(false));

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException accessDeniedException,
                                                                     WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                new Date(), HttpStatus.FORBIDDEN.value(),
                accessDeniedException.getMessage(), request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsExceptionException(AccessDeniedException exception,
                                                                     WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                new Date(), HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage(), request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(JwtSecurityException.class)
    public ResponseEntity<ErrorResponse> handleJwtSecurityException(JwtSecurityException jwtSecurityException,
                                                                    WebRequest request) {
        JwtSecurityException.JWTErrorCode jwtErrorCode = jwtSecurityException.getJwtErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(
                new Date(), jwtErrorCode.getErrorCode(),
                jwtSecurityException.getMessage(), request.getDescription(false)
        );
        log.error(
                "JwtSecurityException Happened On Request:: {}",
                request.getDescription(true), jwtSecurityException
        );
        return new ResponseEntity<>(errorResponse, jwtErrorCode.httpStatus());
    }



}
