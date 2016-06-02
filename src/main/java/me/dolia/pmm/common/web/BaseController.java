package me.dolia.pmm.common.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Web controller - handles request to login page, register etc.
 *
 * @author Maksym Dolia
 * @since 13.03.2016
 */
@Controller
public class BaseController {

    @RequestMapping(value = "/login", method = GET)
    public String showLoginPage(Principal principal) {
        return principal == null ? "login" : "redirect:/app";
    }

    @RequestMapping(value = "/register", method = GET)
    public String showRegisterPage(Principal principal) {
        return principal == null ? "register" : "redirect:/app";
    }
}