package me.dolia.pmm.user;

import me.dolia.pmm.service.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to manage {@link User} instances.
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public User findBy(long id) {
        User user = repository.findOne(id);
        if (user == null) throw new NotFoundException(User.class, "id", Long.toString(id));
        return user;
    }

    @PreAuthorize("#email == authentication.name")
    public User findBy(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(User.class, "email", email));
    }

    public User save(User user) {
        user.encodePassword(encoder);
        return repository.save(user);
    }

    @PreAuthorize("#user.email == authentication.name")
    public void delete(User user) {
        repository.delete(user);
    }

    public boolean isAvailable(String email) {
        Optional<User> user = repository.findByEmail(email);
        return !user.isPresent();
    }
}