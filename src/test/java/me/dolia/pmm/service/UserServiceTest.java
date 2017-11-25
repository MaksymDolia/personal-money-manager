package me.dolia.pmm.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import me.dolia.pmm.entity.User;
import me.dolia.pmm.repository.UserRepository;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
  public void shouldGetUserByEmail() throws Exception {
    User user = createUser(EMAIL);
    when(userRepository.findOneByEmail(EMAIL)).thenReturn(user);

    User result = userService.findOneByEmail(EMAIL);

    assertNotNull(result);
    assertEquals(user, result);
  }

  @Test
  public void shouldThrowExceptionOnFindOneBIfEmailIsNull() throws Exception {
    thrown.expect(NullPointerException.class);
    thrown.expectMessage("email is null");

    userService.findOneByEmail(null);
  }

  @Test
  public void shouldDeleteUser() throws Exception {
    userService.deleteByEmail(EMAIL);

    verify(userRepository, times(1)).deleteByEmail(EMAIL);
  }

  private User createUser(String email) {
    User user = new User();
    user.setEmail(email);
    user.setPassword("somePassword");
    return user;
  }
}