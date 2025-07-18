package com.hafidh.exception;

import static com.hafidh.exception.FunctionalErrorCode.WRONG_VALUE;

public class WrongValueException extends FunctionalException {

    private static final long serialVersionUID = 4896893580503414663L;

    public WrongValueException(String field) {
        super(WRONG_VALUE, field);
    }



}
