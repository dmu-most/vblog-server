package com.example.vblogserver.global.jwt.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.vblogserver.domain.user.dto.ResponseDto;

@ControllerAdvice
class GlobalExceptionHandler {

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<ResponseDto> handleInvalidJwt(InvalidTokenException e) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("Content-Type", "application/json;charset=UTF-8");

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).headers(responseHeaders).body(
			new ResponseDto(false, e.getMessage())
		);
	}
}
