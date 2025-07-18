package com.hafidh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.hafidh.exception.FunctionalErrorCode.WRONG_PASSWORD_FORMAT;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WrongPasswordFormatException extends FunctionalException {
    private static final long serialVersionUID = 3515826689201951584L;

    public WrongPasswordFormatException() {
        super(WRONG_PASSWORD_FORMAT);
    }
}
