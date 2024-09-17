package net.finmath.smartcontract.model;


public class SDCException extends RuntimeException {
    private final String message;

    private final ExceptionId id;

    private Integer statusCode;

    public SDCException(ExceptionId id, String message) {
        this.id = id;
        this.message = message;
    }

    public SDCException(ExceptionId id, String message, int statusCode) {
        this.id = id;
        this.message = message;
        this.statusCode = statusCode;
    }

    public ExceptionId getId() {
        return id;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    public String getMessage() {
        return id + " " + message;
    }
}


