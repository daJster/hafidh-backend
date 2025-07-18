package com.hafidh.exception;


import static com.hafidh.exception.FunctionalErrorCode.NOT_NULL_ENTITY;

public class NullEntityException extends FunctionalException {

    private static final long serialVersionUID = 1L;

    public NullEntityException(Class<?> entityClass) {
        super(NOT_NULL_ENTITY, entityClass.getSimpleName());
    }

}
