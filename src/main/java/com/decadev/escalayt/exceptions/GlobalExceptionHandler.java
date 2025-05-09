package com.decadev.escalayt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<String> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(UsernameAlreadyExistsException.class)
	public ResponseEntity<String> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
	}


	@ExceptionHandler(UserNotEnabledException.class)
	public ResponseEntity<String> handleUserNotEnabledException(UserNotEnabledException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(PasswordMismatchException.class)
	public ResponseEntity<String> handlePasswordMismatchException(PasswordMismatchException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(AlreadyExistsException.class)
	public ResponseEntity<String> handleAlreadyExistCategory(AlreadyExistsException e) {
		return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<String> handleTicketNotFoundException(NotFoundException e){
		return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
	}
}

