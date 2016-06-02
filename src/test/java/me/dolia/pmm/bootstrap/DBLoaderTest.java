package me.dolia.pmm.bootstrap;

import me.dolia.pmm.account.Account;
import me.dolia.pmm.account.AccountRepository;
import me.dolia.pmm.category.Category;
import me.dolia.pmm.category.CategoryRepository;
import me.dolia.pmm.role.RoleService;
import me.dolia.pmm.user.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DBLoaderTest {

    private static final String ADMIN_EMAIL = "some_admin@email.com";

    @Mock private RoleService roleService;
    @Mock private UserService userService;
    @Mock private CategoryRepository categoryRepository;
    @Mock private AccountRepository accountRepository;
    private DBLoader loader;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        loader = new DBLoader(
                ADMIN_EMAIL,
                null,
                new String[]{"test"},
                new String[]{"test"},
                new String[]{"test"},
                roleService,
                userService,
                categoryRepository,
                accountRepository);
    }

    @Test
    public void fillDbWhenThereIsNoAdmin() throws Exception {
        when(userService.isAvailable(ADMIN_EMAIL)).thenReturn(true);

        loader.onApplicationEvent(null);

        verify(roleService, atLeastOnce()).save(any());
        verify(userService, atLeastOnce()).save(any());
        verify(categoryRepository, atLeastOnce()).save(any(Category.class));
        verify(accountRepository, atLeastOnce()).save(any(Account.class));
    }

    @Test
    public void doNotFillDbWhenAdminIsCreated() throws Exception {
        when(userService.isAvailable(ADMIN_EMAIL)).thenReturn(false);

        loader.onApplicationEvent(null);

        verify(userService, only()).isAvailable(ADMIN_EMAIL);
        verifyZeroInteractions(roleService, categoryRepository, accountRepository);
    }
}