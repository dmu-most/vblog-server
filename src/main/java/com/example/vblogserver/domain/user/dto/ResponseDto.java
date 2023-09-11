package com.example.vblogserver.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ResponseDto {
	private boolean result;
	private String message;

	public ResponseDto(boolean result, String message) {
		this.result = result;
		this.message = message;
	}
}