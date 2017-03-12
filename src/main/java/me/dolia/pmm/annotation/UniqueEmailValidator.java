package me.dolia.pmm.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import me.dolia.pmm.repository.UserRepository;

/**
 * Works along with {@code UniqueEmail} annotation. Checks whether the given string is email and it
 * is unique, or not.
 *
 * @author Maksym Dolia
 */
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UniqueEmail constraintAnnotation) {
        //NOP
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return userRepository == null || userRepository.findOneByEmail(email) == null;
    }

}
