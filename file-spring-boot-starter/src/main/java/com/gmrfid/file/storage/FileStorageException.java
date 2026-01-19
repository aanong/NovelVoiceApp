package com.gmrfid.file.storage;

public class FileStorageException extends RuntimeException {

    private int code;
    private String msg;

    public FileStorageException(String msg) {
        super(msg);
        this.msg = msg;
        this.code = 500;
    }

    public FileStorageException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public FileStorageException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
        this.code = 500;
    }
}
