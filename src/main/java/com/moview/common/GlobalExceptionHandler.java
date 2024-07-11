package com.moview.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalStateException.class) // 보통 커스텀 익셉션으로 사용
	public ResponseEntity<String> handleGameIllegalStateException(IllegalStateException illegalStateException) {
		log.info("error = {}", illegalStateException.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(illegalStateException.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleGameIllegalArgumentException(
		IllegalArgumentException illegalArgumentException) {
		log.info("error = {}", illegalArgumentException.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(illegalArgumentException.getMessage());
	}
}