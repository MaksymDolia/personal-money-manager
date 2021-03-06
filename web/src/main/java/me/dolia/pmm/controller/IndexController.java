package me.dolia.pmm.controller;

import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.dolia.pmm.controller.dto.UserDto;
import me.dolia.pmm.service.NotFoundException;
import me.dolia.pmm.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Welcome controller, handles requests to login/signin pages, index page, etc.
 *
 * @author Maksym Dolia
 */
@Controller
@RequiredArgsConstructor
public class IndexController {

  private static final String REDIRECT_TO_APP = "redirect:/app";

  private final UserService userService;

  @ModelAttribute("user")
  public UserDto createUser() {
    return new UserDto();
  }

  @GetMapping("/")
  public String index() {
    if (loggedInUser()) {
      return REDIRECT_TO_APP;
    }
    return "index";
  }

  @GetMapping("/login")
  public String login() {
    if (loggedInUser()) {
      return REDIRECT_TO_APP;
    }
    return "login";
  }

  @GetMapping(value = "/signin")
  public String signIn() {
    if (loggedInUser()) {
      return REDIRECT_TO_APP;
    }
    return "signin";
  }

  @PostMapping(value = "/signin")
  public String doSignin(@Valid @ModelAttribute("user") UserDto user,
      BindingResult result,
      RedirectAttributes attr) {
    if (result.hasErrors()) {
      return "signin";
    }
    userService.save(user.getEmail(), user.getPassword());
    attr.addFlashAttribute("success", true);
    return "redirect:/signin";
  }

  @GetMapping("/profile/delete_profile")
  public String deleteProfile(Principal principal, RedirectAttributes attr) {
    var email = principal.getName();
    userService.deleteByEmail(email);
    SecurityContextHolder.clearContext();
    attr.addFlashAttribute("message", "delete_profile_success");
    return "redirect:/";
  }

  /**
   * Service method for jQuery validation plugin - to check whether email is available or not.
   *
   * @param email email to check
   * @return {@code true} if email is not yet used, {@code false} otherwise
   */
  @GetMapping("/signin/available_email")
  @ResponseBody
  public boolean availableEmail(@RequestParam String email) {
    try {
      userService.findOneByEmail(email);
      return false;
    } catch (NotFoundException e) {
      return Boolean.TRUE;
    }
  }

  private boolean loggedInUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (!(auth instanceof AnonymousAuthenticationToken));
  }
}
