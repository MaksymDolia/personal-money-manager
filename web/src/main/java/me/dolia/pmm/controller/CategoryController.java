package me.dolia.pmm.controller;

import static java.util.stream.Collectors.toList;

import java.security.Principal;
import java.util.Locale;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.controller.dto.CategoryDto;
import me.dolia.pmm.service.CategoryService;
import me.dolia.pmm.service.TransactionService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller, handles requests about categories.
 *
 * @author Maksym Dolia
 */
@Controller
@RequestMapping("/app/categories")
@RequiredArgsConstructor
public class CategoryController {

  private static final String REDIRECT_TO_CATEGORIES = "redirect:/app/categories";
  private static final String CATEGORIES = "categories";

  private final CategoryService categoryService;
  private final TransactionService transactionService;
  private final ApplicationContext context;

  @ModelAttribute("category")
  public CategoryDto createCategory() {
    return new CategoryDto();
  }

  @RequestMapping()
  public String categories(Model model, Principal principal) {
    var email = principal.getName();
    var categories = categoryService.findAllByUserEmail(email)
        .stream()
        .map(CategoryDto::fromCategory)
        .collect(toList());
    model.addAttribute(CATEGORIES, categories);
    return CATEGORIES;
  }

  @PostMapping(value = "/add_category")
  public String addCategory(Model model, @Valid @ModelAttribute("category") CategoryDto dto,
      BindingResult result,
      Principal principal) {
    if (result.hasErrors()) {
      return categories(model, principal);
    }
    var email = principal.getName();
    var category = dto.toCategory();
    categoryService.save(category, email);
    return REDIRECT_TO_CATEGORIES;
  }

  @RequestMapping(value = "/{id}/remove")
  public String removeCategory(@PathVariable("id") int id, RedirectAttributes attr, Locale locale) {
    var category = categoryService.findOne(id);
    var transactions = transactionService.findAllByCategory(category);
    if (!transactions.isEmpty()) {
      attr.addAttribute("id", id);
      attr.addFlashAttribute("message",
          context.getMessage("NotEmpty.AccountController.message", null, locale));
      return "redirect:/app/categories/{id}/edit";
    }
    categoryService.delete(category);
    return REDIRECT_TO_CATEGORIES;
  }

  @GetMapping(value = "/{id}/edit")
  public String editCategory(@PathVariable("id") int id, Model model) {
    var category = categoryService.findOne(id);
    var quantityOfTransactions = transactionService.countTransactionsByCategoryId(id);
    var categories = categoryService.findAllByUserEmail(category.getUser().getEmail())
        .stream()
        .map(CategoryDto::fromCategory)
        .collect(toList());
    model.addAttribute(CATEGORIES, categories);
    model.addAttribute("category", CategoryDto.fromCategory(category));
    model.addAttribute("quantityOfTransactions", quantityOfTransactions);
    return "categories_edit";
  }

  @PostMapping(value = "/{id}/edit")
  public String editCategory(@Valid @ModelAttribute("category") CategoryDto dto,
      @PathVariable("id") int id) {
    var existingCategory = categoryService.findOne(id);
    var category = dto.toCategory();
    categoryService.editCategory(existingCategory, category);
    return REDIRECT_TO_CATEGORIES;
  }

  @PostMapping(value = "/transfer")
  public String transferTransactions(@RequestParam("toCategory") int toId,
      @RequestParam("fromCategory") int fromId,
      RedirectAttributes attr) {
    categoryService.transferTransactions(fromId, toId);
    attr.addAttribute("fromId", fromId);
    return "redirect:/app/categories/{fromId}/edit";
  }
}
