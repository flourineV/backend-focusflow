package com.example.focusflowbackend.exception;

import java.util.Map;

public class ValidationErrorResponse extends ErrorResponse {

    private Map<String, String> validationErrors;

    public ValidationErrorResponse(int status, String message, String path, Map<String, String> validationErrors) {
        super(status, message, path);
        this.validationErrors = validationErrors;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}
