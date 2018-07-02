package org.launchcode.whichwichcontactlist.controllers;

import org.launchcode.whichwichcontactlist.models.Employee;
import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "home")
public class HomeController {

    @Autowired
    private EmployeeDao employeeDao;

    private void RedirectIfNotLoggedIn(String username, HttpServletResponse response) {
        try{
            if (username.equals("none")) {
                response.sendRedirect("/login");
            }
        }
        catch (IOException e) {
            e.getMessage();
        }

    }

    private boolean userIsLoggedIn(String username) {
        if (username.equals("none")){
            return false;
        }
        return true;
    }

    @RequestMapping(value = "")
    public String displayHomePage(Model model, @CookieValue(value = "user", defaultValue = "none") String username,
                                  HttpServletResponse response){


        if (!userIsLoggedIn(username)) {
            return "redirect:login";
        }

        Iterable<Employee> employees = employeeDao.findAll();

        ArrayList<Employee> activeEmployees = new ArrayList<>();

        for (Employee employee : employees) {
            if (employee.isActive()) {
                activeEmployees.add(employee);
            }
        }

        model.addAttribute("title", "Contact List");
        model.addAttribute("employees", activeEmployees);
        if (username.equals("none")) {
            model.addAttribute("username", "guest");
        }
        else {
            model.addAttribute("username",employeeDao.findByEmail(username).getFirstName());
        }


        return "home/index";

    }

}
