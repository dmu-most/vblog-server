package com.example.vblogserver.domain.user.util;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
