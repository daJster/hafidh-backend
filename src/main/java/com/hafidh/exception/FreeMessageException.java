package com.hafidh.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static com.hafidh.exception.FunctionalErrorCode.JUST_MESSAGE;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FreeMessageException extends FunctionalException {
    private static final long serialVersionUID = 3515826689201951584L;

    public FreeMessageException(String message) {
        super(JUST_MESSAGE,message);
    }
}
