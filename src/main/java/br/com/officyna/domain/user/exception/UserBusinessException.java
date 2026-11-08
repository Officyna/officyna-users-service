package br.com.officyna.domain.user.exception;

public class UserBusinessException extends RuntimeException {

    public UserBusinessException(String message) {
        super(message);
    }
}

