package org.launchcode.whichwichcontactlist.controllers;


import org.launchcode.whichwichcontactlist.models.Employee;
import org.launchcode.whichwichcontactlist.models.RequestOff;
import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.launchcode.whichwichcontactlist.models.data.RequestOffDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;


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
        if (username.equals("none")) {
            model.addAttribute("username", "guest");
        }
        else {
            model.addAttribute("username",employeeDao.findByEmail(username).getFirstName());
        }

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
    public String processAddEmployeeForm(Model model, @ModelAttribute @Valid Employee employee, Errors errors,
                                         @RequestParam String verifyPassword, @RequestParam String storeId) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Create New Employee Profile");
            return "employee/add";
        }
        if (!verifyPassword.equals(employee.getPassword())) {
            model.addAttribute("title", "Create New Employee Profile");
            model.addAttribute("verifyError", "Passwords must match");
            return "employee/add";
        }

        if (!storeId.equals("chesterfield317")) {
            model.addAttribute("title", "Create New Employee Profile");
            model.addAttribute("storeIdError", "Incorrect store id");
            return "employee/add";
        }

        employee.setActive();

        employeeDao.save(employee);

        return "redirect:" + employee.getId();

    }

    @RequestMapping(value = "manage", method = RequestMethod.GET)
    public String displayManageEmployeesForm(Model model, HttpServletRequest request, HttpServletResponse response,
                                             @CookieValue(value = "user", defaultValue = "none") String username) {

        RedirectIfNotLoggedIn(username, response);

        Employee employee = employeeDao.findByEmail(username);

        if (!employee.getJobTitle().toLowerCase().equals("manager") &&
                !employee.getJobTitle().toLowerCase().equals("assistant manager")) {
            model.addAttribute("title", "Employee Profile");
            model.addAttribute("employee", employee);
            if (username.equals("none")) {
                model.addAttribute("username", "guest");
            }
            else {
                model.addAttribute("username", employee.getFirstName());
            }

            return "employee/index";
        }

        model.addAttribute("title", "Employee Profile");
        model.addAttribute("employee", employee);
        model.addAttribute("employees", employeeDao.findAll());
        if (username.equals("none")) {
            model.addAttribute("username", "guest");
        }
        else {
            model.addAttribute("username", employee.getFirstName());
        }

        return "employee/manage";

    }

    @RequestMapping(value = "remove-employee", method = RequestMethod.POST)
    public String processDeactivateEmployee(Model model, HttpServletRequest request, HttpServletResponse response,
                                             @RequestParam("employeeIdForRemoval") int employeeId,
                                             @CookieValue(value = "user", defaultValue = "none") String username) {

        Employee employee = employeeDao.findOne(employeeId);

        employee.setActiveToInactive();

        employeeDao.save(employee);

        Iterable<RequestOff> deactivatedEmployeeRequestOffs = requestOffDao.findByEmployeeId(employeeId);

        for (RequestOff requestOff : deactivatedEmployeeRequestOffs) {
            requestOff.setActiveToInactive();
            requestOffDao.save(requestOff);
        }

        model.addAttribute("title", "Employee Profile");
        model.addAttribute("employee", employeeDao.findByEmail(username));
        model.addAttribute("employees", employeeDao.findAll());
        if (username.equals("none")) {
            model.addAttribute("username", "guest");
        }
        else {
            model.addAttribute("username",employeeDao.findByEmail(username).getFirstName());
        }

        return "employee/manage";
    }

}
