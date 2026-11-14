package com.bytedesk.core.department;

public class DepartmentDeletionCommittedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DepartmentDeletionCommittedException(String message, Throwable cause) {
        super(message, cause);
    }
}
