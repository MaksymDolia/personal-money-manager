package me.dolia.pmm.category;

import me.dolia.pmm.service.NotFoundException;
import me.dolia.pmm.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service to manage {@link Category} instances.
 */
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository repository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.repository = categoryRepository;
    }

    @PreAuthorize("#user.email == authentication.name")
    public List<Category> findAll(User user) {
        return repository.findAllByUser(user);
    }

    @PostAuthorize("returnObject.user.email == authentication.name")
    public Category find(long id) {
        Category category = repository.findOne(id);
        if (category == null) throw new NotFoundException(Category.class, "id", String.valueOf(id));
        return category;
    }

    @PreAuthorize("#category.user.email == authentication.name")
    public Category save(Category category) {
        return repository.save(category);
    }

    @PostAuthorize("returnObject.user.email == authentication.name")
    public Category update(Category data) {
        Category category = find(data.getId());
        category.setName(data.getName());
        category.setOperation(data.getOperation());
        return save(category);
    }

    @PreAuthorize("#category.user.email == authentication.name")
    public void delete(Category category) {
        repository.delete(category);
    }
}