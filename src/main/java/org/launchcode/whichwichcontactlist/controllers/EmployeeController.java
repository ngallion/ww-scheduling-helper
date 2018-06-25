package org.launchcode.whichwichcontactlist.controllers;


import org.launchcode.whichwichcontactlist.models.Employee;
import org.launchcode.whichwichcontactlist.models.RequestOff;
import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("employee")
public class EmployeeController {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private RequestOff requestOff;


    @RequestMapping(value = "{employeeId}")
    public String index(Model model, @PathVariable("employeeId") int employeeId){

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
        if (verifyPassword != newEmployee.getPassword()) {
            model.addAttribute("verifyError", "Passwords must match");
            return "employee/add";
        }

        employeeDao.save(newEmployee);

        return "redirect:" + newEmployee.getId();

    }

    @RequestMapping(value = "/request-off-day", method = RequestMethod.GET)
    public String displayRequestOffDayForm(Model model) {

        model.addAttribute("title", "Request Off Day");
        model.addAttribute("employeeId", 1);
        model.addAttribute(new RequestOff());

        return "employee/request-off-day";

    }

    @RequestMapping(value = "/request-off-time", method = RequestMethod.GET)
    public String displayRequestOffTimeForm(Model model) {

        model.addAttribute("title", "Request Off Time");
        model.addAttribute("employeeId", 1);
        model.addAttribute(new RequestOff());

        return "employee/request-off-time";

    }

}
