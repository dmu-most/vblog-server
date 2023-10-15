package com.example.vblogserver.domain.user.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.vblogserver.global.jwt.util.InvalidTokenException;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}
}
