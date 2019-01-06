package me.dolia.pmm.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.entity.Category;
import me.dolia.pmm.entity.Transaction;
import me.dolia.pmm.service.CategoryService;
import me.dolia.pmm.service.TransactionService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
  public Category createCategory() {
    return new Category();
  }

  @RequestMapping()
  public String categories(Model model, Principal principal) {
    String email = principal.getName();
    List<Category> categories = categoryService.findAllByUserEmail(email);
    model.addAttribute(CATEGORIES, categories);
    return CATEGORIES;
  }

  @RequestMapping(value = "/add_category", method = RequestMethod.POST)
  public String addCategory(Model model, @Valid @ModelAttribute Category category,
      BindingResult result,
      Principal principal) {
    if (result.hasErrors()) {
      return categories(model, principal);
    }
    String email = principal.getName();
    categoryService.save(category, email);
    return REDIRECT_TO_CATEGORIES;
  }

  @RequestMapping(value = "/{id}/remove")
  public String removeCategory(@PathVariable("id") int id, RedirectAttributes attr, Locale locale) {
    Category category = categoryService.findOne(id);
    List<Transaction> transactions = transactionService.findAllByCategory(category);
    if (!transactions.isEmpty()) {
      attr.addAttribute("id", id);
      attr.addFlashAttribute("message",
          context.getMessage("NotEmpty.AccountController.message", null, locale));
      return "redirect:/app/categories/{id}/edit";
    }
    categoryService.delete(category);
    return REDIRECT_TO_CATEGORIES;
  }

  @RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
  public String editCategory(@PathVariable("id") int id, Model model) {
    Category category = categoryService.findOne(id);
    long quantityOfTransactions = transactionService.countTransactionsByCategoryId(id);
    List<Category> categories = categoryService.findAllByUserEmail(category.getUser().getEmail());
    model.addAttribute(CATEGORIES, categories);
    model.addAttribute("category", category);
    model.addAttribute("quantityOfTransactions", quantityOfTransactions);
    return "categories_edit";
  }

  @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST)
  public String editCategory(@Valid @ModelAttribute Category category, @PathVariable("id") int id) {
    Category existingCategory = categoryService.findOne(id);
    categoryService.editCategory(existingCategory, category);
    return REDIRECT_TO_CATEGORIES;
  }

  @RequestMapping(value = "/transfer", method = RequestMethod.POST)
  public String transferTransactions(@RequestParam("toCategory") int toId,
      @RequestParam("fromCategory") int fromId,
      RedirectAttributes attr) {
    categoryService.transferTransactions(fromId, toId);
    attr.addAttribute("fromId", fromId);
    return "redirect:/app/categories/{fromId}/edit";
  }
}
