package org.launchcode.whichwichcontactlist.controllers;


import org.launchcode.whichwichcontactlist.models.Employee;
import org.launchcode.whichwichcontactlist.models.JobTitle;
import org.launchcode.whichwichcontactlist.models.RequestOff;
import org.launchcode.whichwichcontactlist.models.Store;
import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.launchcode.whichwichcontactlist.models.data.JobTitleDao;
import org.launchcode.whichwichcontactlist.models.data.RequestOffDao;
import org.launchcode.whichwichcontactlist.models.data.StoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("employee")
public class EmployeeController {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private RequestOffDao requestOffDao;

    @Autowired
    private StoreDao storeDao;

    @Autowired
    private JobTitleDao jobTitleDao;

    private boolean userIsLoggedIn(String username) {
        if (username.equals("none")){
            return false;
        }
        return true;
    }

    public void ProvideUserNameInWelcomeMessage(Model model, String username) {

        if (username.equals("none")) {
            model.addAttribute("username", "guest");
        }
        else {
            model.addAttribute("username", employeeDao.findByEmail(username).getFirstName());
        }
    }

    public boolean IsValidStoreCode(Employee employee, String storeCode, Iterable<Store> stores) {

        boolean isValid = false;

        for (Store store : stores){
            if (store.getCode().equals(storeCode)) {
                isValid = true;
            }
        }

        return isValid;
    }

    @RequestMapping(value = "")
    public String index(Model model, HttpServletRequest request, HttpServletResponse response,
                        @CookieValue(value = "user", defaultValue = "none") String username){

        if (userIsLoggedIn(username) == false) {
            return "redirect:login";
        }
        ProvideUserNameInWelcomeMessage(model, username);
        model.addAttribute("title", "Employee Profile");
        model.addAttribute("employee", employeeDao.findByEmail(username));


        return "employee/index";

    }

    @RequestMapping(value = "{employeeId}")
    public String index(Model model, @PathVariable("employeeId") int employeeId,
                        HttpServletResponse response, HttpServletRequest request,
                        @CookieValue(value = "user", defaultValue = "none") String username){

        if (userIsLoggedIn(username) == false) {
            return "redirect:login";
        }

        model.addAttribute("title", "Which Wich Contact List");
        model.addAttribute("employee", employeeDao.findOne(employeeId));

        return "employee/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String displayAddEmployeeForm(Model model) {

        model.addAttribute("title", "Create New Employee Profile");
        model.addAttribute("jobTitles", jobTitleDao.findAll());
        model.addAttribute(new Employee());


        return "employee/add";

    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String processAddEmployeeForm(Model model, @ModelAttribute @Valid Employee employee, Errors errors,
                                         @RequestParam String verifyPassword, @RequestParam String storeCode,
                                         @RequestParam int jobTitleId, HttpServletResponse response) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Create New Employee Profile");
            return "employee/add";
        }
        if (!verifyPassword.equals(employee.getPassword())) {
            model.addAttribute("title", "Create New Employee Profile");
            model.addAttribute("verifyError", "Passwords must match");
            return "employee/add";
        }

        Iterable<Store> stores = storeDao.findAll();

        if (IsValidStoreCode(employee, storeCode, stores)) {
            employee.setStore(storeDao.findOneByCode(storeCode));
        }
        else {
            model.addAttribute("title", "Create New Employee Profile");
            model.addAttribute("storeCodeError", "Incorrect store code");
            return "employee/add";
        }

        JobTitle jobTitle = jobTitleDao.findOne(jobTitleId);

        employee.setJobTitle(jobTitle);

        employee.setActive();

        employeeDao.save(employee);

        Cookie cookie = new Cookie("user", employee.getEmail());
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:employee";

    }

    @RequestMapping(value = "manage", method = RequestMethod.GET)
    public String displayManageEmployeesForm(Model model, HttpServletRequest request, HttpServletResponse response,
                                             @CookieValue(value = "user", defaultValue = "none") String username) {

        if (userIsLoggedIn(username) == false) {
            return "redirect:login";
        }

        Employee employee = employeeDao.findByEmail(username);

        if (!employee.getJobTitle().getName().toLowerCase().equals("manager") &&
                !employee.getJobTitle().getName().toLowerCase().equals("assistant manager")) {
            model.addAttribute("title", "Employee Profile");
            model.addAttribute("employee", employee);
            ProvideUserNameInWelcomeMessage(model,username);

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
