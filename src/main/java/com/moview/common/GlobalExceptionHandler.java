package com.moview.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class}) // 보통 커스텀 익셉션으로 사용
	public ResponseEntity<Map<String, String>> handleRuntimeExceptions(
		RuntimeException runtimeException) {

		logError(runtimeException);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
			.body(Map.of("message", runtimeException.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidExceptions(
		MethodArgumentNotValidException methodArgumentNotValidException
	) {

		logError(methodArgumentNotValidException);

		Map<String, String> errors = new HashMap<>();

		methodArgumentNotValidException.getBindingResult()
			.getAllErrors()
			.forEach(error -> errors.put(((FieldError)error).getField(), error.getDefaultMessage()));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	private static void logError(Exception exception) {
		log.error("error = {} - {}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
	}

}