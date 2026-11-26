package com.example.edu_plat.exception;

import org.springframework.modulith.NamedInterface;

@NamedInterface("exceptions")
public class InvalidMediaFormatException extends RuntimeException {
    public InvalidMediaFormatException(String message) {
        super(message);
    }
}