package com.hafidh.exception;

import java.util.List;

import static com.hafidh.exception.FunctionalErrorCode.PATTERN_FIELD;
import static java.lang.String.join;


public class PatternFieldException extends FunctionalException {

    private static final long serialVersionUID = -1L;

    public PatternFieldException(String field) {
        super(PATTERN_FIELD, field);
    }

    public PatternFieldException(List<String> fields) {
        super(PATTERN_FIELD, join(", ", fields));
    }

}