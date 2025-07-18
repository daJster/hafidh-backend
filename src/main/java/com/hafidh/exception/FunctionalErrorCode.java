package com.hafidh.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static java.lang.String.format;

/**
 * This enum represents the functional error codes.
 */
@Getter
public enum FunctionalErrorCode {

    BAD_REQUEST(0, HttpStatus.BAD_REQUEST, "Bad Request"),
    NOT_FOUND_ENTITY_ID(1, HttpStatus.NOT_FOUND, "No record of type %s and with id %s is present in the database"),
    NOT_FOUND_ENTITY_VALUE(1, HttpStatus.NOT_FOUND, "No record of type %s and with value : %s is present in the database"),
    NOT_FOUND_ENTITY(1, HttpStatus.NOT_FOUND, "No record of type %s found"),
    NOT_FOUND(1, HttpStatus.NOT_FOUND, "Not found"),
    NOT_FOUND_BY_VALUE(1, HttpStatus.NOT_FOUND, "No record of type %s and with value %s is present in the database"),
    USER_NOT_FOUND(2, HttpStatus.NOT_FOUND, "Login inexist : %s"),
    NOT_NULL_ENTITY(3, HttpStatus.BAD_REQUEST, "Entity %s is required."),
    NOT_NULL_FIELD(4, HttpStatus.BAD_REQUEST, "The following field is required: %s"),
    JUST_MESSAGE(5, HttpStatus.BAD_REQUEST, "%s"),
    NOT_NULL_FIELDS(6, HttpStatus.BAD_REQUEST, "The following fields are required : %s"),
    MUST_BE_NULL_FIELD(7, HttpStatus.BAD_REQUEST, "The following fields must be null : %s"),
    INCORRECT_LENGTH_FIELD(8, HttpStatus.BAD_REQUEST, "The following field is not the right size : %s"),
    MIN_FIELD(9, HttpStatus.BAD_REQUEST, "The following field does not respect the minimum value : %s"),
    MAX_FIELD(10, HttpStatus.BAD_REQUEST, "The following field does not respect the maximum value : %s"),
    PATTERN_FIELD(11, HttpStatus.BAD_REQUEST, "The following field does not respect the correct format : %s"),
    NOT_BLANK_OR_EMPTY_FIELD(12, HttpStatus.BAD_REQUEST, "The following field is empty : %s"),
    INVALID_JWT_TOKEN(13, HttpStatus.UNAUTHORIZED, "Token JWT expired ou invalid"),
    CREDENTIAL_ALREADY_USED(14, HttpStatus.BAD_REQUEST, "%s Already exist"),
    PROPERTY_ALREADY_USED(15, HttpStatus.BAD_REQUEST, "%s Already used"),
    WRONG_PASSWORD(16, HttpStatus.BAD_REQUEST, "Password incorrect"),
    AUTHORIZATION_FAILED(18, HttpStatus.FORBIDDEN, "Authorization failed for access token request."),
    EMAIL_ALREADY_EXIST_EXCEPTION(21, HttpStatus.BAD_REQUEST,"Email already exist in database."),
    WRONG_VALUE(30, HttpStatus.BAD_REQUEST, "Wrong value : %s"),
    NO_RESPONSE_URL(31, HttpStatus.BAD_REQUEST, "Enabe getting \"%s\" data from api."),
    WRONG_PASSWORD_FORMAT(36, HttpStatus.BAD_REQUEST, "Password wrong format."),
    ;


    private final String code;
    private final HttpStatus httpStatus;
    private final String messageTemplate;

    FunctionalErrorCode(int code, HttpStatus httpStatus, String messageTemplate) {
        this.code = format("%03d", code);
        this.httpStatus = httpStatus;
        this.messageTemplate = messageTemplate;
    }
}
