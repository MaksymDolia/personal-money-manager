package me.dolia.pmm.web;

import me.dolia.pmm.service.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity unprocessableEntity(MethodArgumentNotValidException ex) {

        StringBuilder sb = new StringBuilder();

        /* build message with all validation errors */
        List<String> messages = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.toList());

        for (String m : messages) {
            sb.append(m);
            sb.append(" ");
        }

        Map<String, String> error = new HashMap<>();
        error.put("error", sb.toString().trim());

        return ResponseEntity.unprocessableEntity().body(error);
    }

    @ExceptionHandler({NotFoundException.class, NoSuchElementException.class})
    public ResponseEntity notFound() {
        return ResponseEntity.notFound().build();
    }
}