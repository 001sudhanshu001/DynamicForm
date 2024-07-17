package com.learn.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;

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
                                                               HttpHeaders headers,
                                                               HttpStatusCode status,
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
}
