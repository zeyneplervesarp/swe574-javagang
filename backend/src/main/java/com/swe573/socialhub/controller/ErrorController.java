package com.swe573.socialhub.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(ErrorController.class);

    private static class ErrorMessage {
        private final String message;

        private ErrorMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private static final String EXCEPTION_SEPARATOR = "Exception: ";

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorMessage> exception(final Throwable throwable, final Model model) {
        logger.error("Exception during execution of SpringSecurity application", throwable);

        String errorMessage;

        try {
            final var responseException = (ResponseStatusException) throwable;
            final var reason = responseException.getReason();
            if (reason.contains(EXCEPTION_SEPARATOR)) {
                errorMessage = StringUtils.substringAfterLast(reason, EXCEPTION_SEPARATOR);
            } else {
                errorMessage = reason;
            }
            return ResponseEntity.status(responseException.getStatus()).body(new ErrorMessage(errorMessage));
        } catch (Exception e) {
            errorMessage = (throwable != null ? throwable.getMessage() : "Unknown error");
            model.addAttribute(errorMessage);
            return ResponseEntity.internalServerError().body(new ErrorMessage(errorMessage));
        }

    }

}