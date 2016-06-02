package me.dolia.pmm.web;

import me.dolia.pmm.category.Category;
import me.dolia.pmm.category.CategoryService;
import me.dolia.pmm.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Optional;

import static me.dolia.pmm.web.CategoryController.CATEGORIES_API_URL;
import static org.springframework.web.bind.annotation.RequestMethod.*;


@RestController
@RequestMapping(value = CATEGORIES_API_URL)
public class CategoryController {

    static final String CATEGORIES_API_URL = "/categories";

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ModelAttribute
    public Category loadCategory(@PathVariable Optional<Long> id) {
        if (id.isPresent()) return categoryService.find(id.get());
        return null;
    }

    @RequestMapping(method = GET)
    public List<Category> categories(@AuthenticationPrincipal User user) {
        return categoryService.findAll(user);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public Category category(@ModelAttribute Category category) {
        return category;
    }

    @RequestMapping(method = POST)
    public ResponseEntity createCategory(@RequestBody Category data, @AuthenticationPrincipal User user) {
        data.setUser(user);
        Category category = categoryService.save(data);
        return ResponseEntity.created(ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path(category.getId().toString())
                .build()
                .toUri())
                .body(category);
    }

    @RequestMapping(value = "/{id}", method = PUT)
    public void updateCategory(@PathVariable long id, @RequestBody Category data) {
        data.setId(id);
        categoryService.update(data);
    }

    @RequestMapping(value = "/{id}", method = DELETE)
    public ResponseEntity deleteCategory(@ModelAttribute Category category) {
        categoryService.delete(category);
        return ResponseEntity.noContent().build();
    }
}