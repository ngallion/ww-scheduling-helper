package org.launchcode.whichwichcontactlist.controllers;


import org.launchcode.whichwichcontactlist.models.Employee;
import org.launchcode.whichwichcontactlist.models.RequestOff;
import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.launchcode.whichwichcontactlist.models.data.RequestOffDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.Date;

import static java.util.Calendar.AM;

@Controller
@RequestMapping("employee")
public class EmployeeController {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private RequestOffDao requestOffDao;


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

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response,
                        @CookieValue(value = "user", defaultValue = "none") String username){

        RedirectIfNotLoggedIn(username, response);

        model.addAttribute("title", "Employee Profile");
        model.addAttribute("employee", employeeDao.findByEmail(username));

        return "employee/index";

    }

    @RequestMapping(value = "{employeeId}")
    public String index(Model model, @PathVariable("employeeId") int employeeId,
                        HttpServletResponse response, HttpServletRequest request,
                        @CookieValue(value = "user", defaultValue = "none") String username){



        model.addAttribute("title", "Which Wich Contact List");
        model.addAttribute("employee", employeeDao.findOne(employeeId));

        return "employee/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddEmployeeForm(Model model) {

        model.addAttribute("title", "Create New Employee Profile");
        model.addAttribute(new Employee());

        return "employee/add";

    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddEmployeeForm(Model model, @ModelAttribute @Valid Employee newEmployee,
                                         @RequestParam String verifyPassword, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Create New Employee Profile");
            return "employee/add";
        }
        if (!verifyPassword.equals(newEmployee.getPassword())) {
            model.addAttribute("verifyError", "Passwords must match");
            return "employee/add";
        }

        employeeDao.save(newEmployee);

        return "redirect:" + newEmployee.getId();

    }

}
