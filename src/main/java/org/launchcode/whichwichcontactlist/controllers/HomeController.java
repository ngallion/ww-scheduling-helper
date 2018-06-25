package org.launchcode.whichwichcontactlist.controllers;

import org.launchcode.whichwichcontactlist.models.Employee;
import org.launchcode.whichwichcontactlist.models.Login;
import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "home")
public class HomeController {

    @Autowired
    private EmployeeDao employeeDao;

    @RequestMapping(value = "")
    public String displayHomePage(Model model){

        model.addAttribute("title", "Which Wich Contact List");
        model.addAttribute("employees", employeeDao.findAll());

        return "home/index";

    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String displayLoginForm(Model model){

        model.addAttribute("title", "Login");
        model.addAttribute(new Login());

        return "home/login";

    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String processLoginForm(@ModelAttribute Login loginAttempt, Errors errors, Model model) {

        Employee attemptedEmployeeLogin = employeeDao.findByEmail(loginAttempt.getUsername());

        if (attemptedEmployeeLogin == null || !attemptedEmployeeLogin.getPassword().equals(loginAttempt.getPassword())) {
            model.addAttribute("error", "Invalid username or password");
            return "home/login";
        }

        return "redirect:/home";

    }

}
