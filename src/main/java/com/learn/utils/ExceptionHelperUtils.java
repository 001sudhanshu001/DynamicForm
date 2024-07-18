package com.learn.utils;

import com.learn.exception.ApplicationException;

import java.util.function.Supplier;

import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ExceptionHelperUtils {
    public static Supplier<ApplicationException> notFoundException(String whatNotFound, Long id) {
        String notFoundErrorTemplate = "%s Not Found With Id:: " + id;
        String errorMessage = String.format(notFoundErrorTemplate, whatNotFound);
        return () -> new ApplicationException(NOT_FOUND, errorMessage);
    }

    public static Supplier<ApplicationException> notFoundException(Long id) {
        return () -> new ApplicationException(
                NOT_FOUND, "Data Not Found With Id:: " + id
        );
    }
}
