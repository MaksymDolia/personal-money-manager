package me.dolia.pmm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

  private static final String EMAIL = "some.test@email.com";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Test
  public void getUserByEmail() {
    User user = createUser(EMAIL);
    when(userRepository.findOneByEmail(EMAIL)).thenReturn(Optional.of(user));

    User result = userService.find(EMAIL);

    assertNotNull(result);
    assertEquals(user, result);
  }

  @Test
  public void throwExceptionOnFindOneByEmailIfUserDoesNotExist() {
    thrown.expect(NotFoundException.class);
    thrown.expectMessage("User with email 'null' was not found");

    userService.find(null);
  }

  @Test
  public void deleteUser() {
    userService.delete(EMAIL);

    verify(userRepository, times(1)).deleteByEmail(EMAIL);
  }

  private User createUser(String email) {
    User user = new User();
    user.setEmail(email);
    user.setPassword("somePassword");
    return user;
  }
}
