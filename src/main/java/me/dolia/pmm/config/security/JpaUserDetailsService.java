package me.dolia.pmm.config.security;

import me.dolia.pmm.user.User;
import me.dolia.pmm.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public JpaUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Could not find user with email '%s'", email)));
        return new CustomUserDetails(user);
    }
}