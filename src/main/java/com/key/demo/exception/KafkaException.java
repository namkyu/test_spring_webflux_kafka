package com.key.demo.exception;

public class KafkaException extends RuntimeException {

    public static final KafkaException SEND_ERROR = new KafkaException("send failed");

    public KafkaException(String msg) {
        super(msg);
    }
}
