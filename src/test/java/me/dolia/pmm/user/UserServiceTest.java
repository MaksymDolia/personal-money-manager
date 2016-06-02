package me.dolia.pmm.user;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static me.dolia.pmm.util.TestUtils.createUser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final String EMAIL = "some_test@email.com";
    private static final long ID = 15L;

    @Mock private UserRepository repository;
    @Mock private PasswordEncoder encoder;
    @InjectMocks private UserService userService;
    private User user;

    @Before
    public void setUp() throws Exception {
        user = createUser(EMAIL);
    }

    @Test
    public void findSingleUserById() throws Exception {
        when(repository.findOne(ID)).thenReturn(user);

        User result = userService.findBy(ID);

        assertThat(result, is(user));
    }

    @Test
    public void findSingleUserByEmail() throws Exception {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        User result = userService.findBy(EMAIL);

        assertThat(result, is(user));
    }

    @Test
    public void saveUser() throws Exception {
        String raw = user.getPassword();

        userService.save(user);
        String hashed = user.getPassword();

        verify(repository, only()).save(user);
        assertThat(hashed, not(raw));
    }

    @Test
    public void deleteUser() throws Exception {
        userService.delete(user);

        verify(repository, only()).delete(user);
    }

    @Test
    public void checkFreeEmail() throws Exception {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        boolean available = userService.isAvailable(EMAIL);

        assertTrue(available);
    }

    @Test
    public void checkBusyEmail() throws Exception {
        when(repository.findByEmail(EMAIL)).thenReturn(Optional.of(user));

        boolean available = userService.isAvailable(EMAIL);

        assertFalse(available);
    }
}