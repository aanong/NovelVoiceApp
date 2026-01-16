package com.gmrfid.excel.exception;

/**
 * Excel处理自定义异?
 */
public class ExcelException extends RuntimeException {

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }
}
