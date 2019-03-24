package me.dolia.pmm.validation;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.dolia.pmm.persistence.entity.User;
import me.dolia.pmm.persistence.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UniqueEmailValidatorTest {

  private static final String EMAIL = "test@test.com";

  @Mock
  private UserRepository repository;

  @InjectMocks
  private UniqueEmailValidator validator;

  @Test
  public void emailIsValid() {
    when(repository.findById(EMAIL)).thenReturn(Optional.empty());

    assertThat(validator.isValid(EMAIL, null)).isTrue();
  }

  @Test
  public void emailIsNotValid() {
    when(repository.findById(EMAIL)).thenReturn(Optional.of(new User()));

    assertThat(validator.isValid(EMAIL, null)).isFalse();
  }
}