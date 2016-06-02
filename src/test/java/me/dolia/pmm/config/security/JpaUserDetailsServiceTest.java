package me.dolia.pmm.config.security;

import me.dolia.pmm.user.User;
import me.dolia.pmm.user.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static me.dolia.pmm.util.TestUtils.createUser;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JpaUserDetailsServiceTest {

    private String TEST_EMAIL = "someTest@email.com";

    @Mock private UserRepository repository;
    @InjectMocks private JpaUserDetailsService service;

    @Test
    public void loadUserByUsername() throws Exception {
        User user = createUser(TEST_EMAIL);
        when(repository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername(TEST_EMAIL);

        assertThat(result, is(user));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void usernameNotFound() throws Exception {
        when(repository.findByEmail(TEST_EMAIL)).thenReturn(Optional.empty());
        service.loadUserByUsername(TEST_EMAIL);
    }
}