package com.hafidh.exception;


import static com.hafidh.exception.FunctionalErrorCode.EMAIL_ALREADY_EXIST_EXCEPTION;

public class EmailAlreadyExisteException extends FunctionalException {

    private static final long serialVersionUID = 4896893580503414663L;

    public EmailAlreadyExisteException() {
        super(EMAIL_ALREADY_EXIST_EXCEPTION);
    }


}
