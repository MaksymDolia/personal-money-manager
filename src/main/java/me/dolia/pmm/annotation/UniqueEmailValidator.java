package me.dolia.pmm.annotation;

import me.dolia.pmm.repository.UserRepository;

import javax.inject.Inject;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Works along with {@code UniqueEmail} annotation. Checks whether the given string is email and it is unique, or not.
 *
 * @author Maksym Dolia
 */
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Inject
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
