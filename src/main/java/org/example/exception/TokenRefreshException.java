package org.example.exception;


import java.text.MessageFormat;

public class TokenRefreshException extends RuntimeException{
    public TokenRefreshException(String token,String message) {
        super(MessageFormat.format("Error trying to refresh by token: {0} : {1}", token, message));
    }

    public TokenRefreshException(String message) {
        super(message);
    }
}
