package com.example.audit.audit_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception levée lors d'un conflit de données (ex: email ou username déjà existant).
 * 
 * Retourne automatiquement un statut HTTP 409.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s existe déjà avec %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
