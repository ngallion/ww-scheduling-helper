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

    @RequestMapping(value = "/request-off-day", method = RequestMethod.GET)
    public String displayRequestOffDayForm(Model model, HttpServletResponse response, HttpServletRequest request,
                                           @CookieValue(value = "user") String username) {

        RedirectIfNotLoggedIn(username, response);

        model.addAttribute("title", "Request Off Day");
        model.addAttribute("employeeId", employeeDao.findByEmail(username).getId());
        model.addAttribute(new RequestOff());

        return "employee/request-off-day";

    }

    @RequestMapping(value = "/request-off-time", method = RequestMethod.GET)
    public String displayRequestOffTimeForm(Model model, HttpServletResponse response, HttpServletRequest request,
                                            @CookieValue(value = "user", defaultValue = "none") String username) {

        RedirectIfNotLoggedIn(username, response);

        model.addAttribute("title", "Request Off Time");
        model.addAttribute("employeeId", employeeDao.findByEmail(username).getId());
        model.addAttribute(new RequestOff());

        return "employee/request-off-time";

    }

    @RequestMapping(value = "/request-off-day", method = RequestMethod.POST)
    public String processRequestOffDayForm(Model model, @ModelAttribute RequestOff requestOff,
                                           @RequestParam("endDate") String endDate, @RequestParam int employeeId,
                                           Errors errors) {


        Period period = Period.between(requestOff.getDate().toLocalDate(), LocalDate.parse(endDate));

        LocalTime startTime = LocalTime.parse("08:00:00");
        LocalTime endTime = LocalTime.parse("21:00:00");

        for (int i = 0; i < period.getDays() + 1; i++) {
            RequestOff requestOffPeriod = new RequestOff();

            requestOffPeriod.setDate(java.sql.Date.valueOf(requestOff.getDate().toLocalDate().plusDays(i)));
            requestOffPeriod.setStartTime(java.sql.Time.valueOf(startTime));
            requestOffPeriod.setEndTime(java.sql.Time.valueOf(endTime));
            requestOffPeriod.setEmployee(employeeDao.findOne(employeeId));

            requestOffDao.save(requestOffPeriod);
        }

        return "redirect:";
    }

    @RequestMapping(value = "/request-off-time", method = RequestMethod.POST)
    public String processRequestOffTimeForm(Model model, @RequestParam("date") String date,
                                            @RequestParam String startTime, @RequestParam String endTime,
                                            @RequestParam int employeeId) {

        RequestOff requestOff = new RequestOff();

//        LocalDate localDate = LocalDate.parse(date);
        LocalTime localStartTime = LocalTime.parse(startTime);
        LocalTime localEndTime = LocalTime.parse(endTime);

        requestOff.setDate(java.sql.Date.valueOf(date));
        requestOff.setStartTime(java.sql.Time.valueOf(localStartTime));
        requestOff.setEndTime(java.sql.Time.valueOf(localEndTime));
        requestOff.setEmployee(employeeDao.findOne(employeeId));

        requestOffDao.save(requestOff);

        return "redirect:";

    }

}
