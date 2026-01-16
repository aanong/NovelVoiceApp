package com.gmrfid.excel.exception;

/**
 * Excel业务异常
 */
public class ExcelException extends RuntimeException {

    private String code;

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }
}
