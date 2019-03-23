package me.dolia.pmm.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import me.dolia.pmm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Works along with {@code UniqueEmail} validation. Checks whether the given string is email and it
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
    return !userRepository.findById(email).isPresent();
  }
}
