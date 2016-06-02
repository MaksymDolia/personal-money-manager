package me.dolia.pmm.service;

/**
 * @author Maksym Dolia
 * @since 14.02.2016
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(Class<?> aClass, String paramName, String param) {
        super(String.format("The %s with %s:%s was not found.", aClass.getSimpleName(), paramName, param));
    }
}