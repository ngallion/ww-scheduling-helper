package org.launchcode.whichwichcontactlist.controllers;

import org.launchcode.whichwichcontactlist.models.Employee;
import org.launchcode.whichwichcontactlist.models.Login;
import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.launchcode.whichwichcontactlist.utilities.Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "login")
public class LoginController {

    @Autowired
    private EmployeeDao employeeDao;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String displayLoginForm(Model model){

        model.addAttribute("title", "Login");
        model.addAttribute(new Login());

        return "login/index";

    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public String processLoginForm(@ModelAttribute Login loginAttempt, Errors errors, Model model,
                                   HttpServletResponse response) {

        Employee attemptedEmployeeLogin = employeeDao.findByEmail(loginAttempt.getUsername());

        Password attemptedPassword = new Password();

        try {
            if (attemptedEmployeeLogin == null) {
                model.addAttribute("error", "Invalid username or password");
                return "login/index";
            }
            else {
                boolean isValidLogin = attemptedPassword.check(loginAttempt.getPassword(),
                        attemptedEmployeeLogin.getPassword());

                if (isValidLogin == false) {
                    model.addAttribute("error", "Invalid username or password");
                    return "login/index";
                }
            }
        }
        catch (java.lang.Exception e){
            System.out.println(e.getMessage());
        }

        Cookie cookie = new Cookie("user", loginAttempt.getUsername());
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:home";

    }

    @RequestMapping(value = "logout")
    public String logout(Model model, HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie: cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }

        return "redirect:/login";
    }

}
