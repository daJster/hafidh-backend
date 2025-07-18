package com.hafidh.exception;


import static com.hafidh.exception.FunctionalErrorCode.WRONG_PASSWORD;

public class WrongPasswordException extends FunctionalException {

    private static final long serialVersionUID = 1L;

    public WrongPasswordException(Class<?> entityClass) {
        super(WRONG_PASSWORD, entityClass.getSimpleName());
    }

}