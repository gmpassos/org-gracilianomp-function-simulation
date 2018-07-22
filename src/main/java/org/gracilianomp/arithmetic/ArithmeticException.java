package org.gracilianomp.arithmetic;

public class ArithmeticException extends RuntimeException {

    public ArithmeticException(String message) {
        super(message);
    }

    public ArithmeticException(String message, Throwable cause) {
        super(message, cause);
    }

}
