package edu.pwr.niduc.util;

public class MessageTooLongException extends RuntimeException {

    public MessageTooLongException(String message) {
        super(message);
    }
}
