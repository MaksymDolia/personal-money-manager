package me.dolia.pmm.role;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    private static final String ROLE_NAME = "SOME_TEST_ROLE";

    @Mock private RoleRepository repository;
    @InjectMocks private RoleService roleService;
    private Role role;

    @Before
    public void setUp() throws Exception {
        role = new Role();
        role.setName(ROLE_NAME);
    }

    @Test
    public void readSingleRole() throws Exception {
        when(repository.findByName(ROLE_NAME)).thenReturn(Optional.of(role));

        Role result = roleService.find(ROLE_NAME);

        assertThat(result, is(role));
    }

    @Test
    public void saveRole() throws Exception {
        Role savedRole = new Role();
        savedRole.setName(role.getName());
        savedRole.setId(1L);
        when(repository.save(role)).thenReturn(savedRole);

        Role result = roleService.save(role);

        assertThat(result, is(savedRole));
    }

    @Test
    public void deleteRole() throws Exception {
        roleService.delete(role);

        verify(repository, only()).delete(role);
    }
}