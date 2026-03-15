package edu.itmo.club.management.app.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс для перехвата и обработки ошибок в контроллерах.
 */
@RestControllerAdvice
public class CommonExceptionHandler {

	/**
	 * Обрабатывает исключение {@link MethodArgumentNotValidException}, которое выбрасывается
	 * при неудачной валидации входящих параметров запроса.
	 *
	 * @param ex Исключение.
	 * @return Ответ с сообщениями об ошибках.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getFieldErrors().forEach(error -> {
			String fieldName = error.getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return ResponseEntity.badRequest().body(errors);
	}
}
