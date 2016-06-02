package me.dolia.pmm.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import static me.dolia.pmm.util.TestUtils.createUser;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    private static final String HASHED_PASSWORD = "ThisIsHashedPassword";

    @Mock private PasswordEncoder encoder;
    private User user = createUser();

    @Test
    public void encodePassword() throws Exception {
        when(encoder.encode(anyString())).thenReturn(HASHED_PASSWORD);

        user.encodePassword(encoder);
        String resultPassword = user.getPassword();

        assertThat(resultPassword, is(HASHED_PASSWORD));
        verify(encoder, only()).encode(anyString());
    }
}