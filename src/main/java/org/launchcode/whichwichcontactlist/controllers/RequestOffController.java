package org.launchcode.whichwichcontactlist.controllers;

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
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;

@Controller
@RequestMapping("employee/request-off")
public class RequestOffController {

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
    public String index(Model model, HttpServletResponse response, HttpServletRequest request,
                        @CookieValue(value = "user") String username) {

        RedirectIfNotLoggedIn(username, response);

        Integer employeeId = employeeDao.findByEmail(username).getId();

        model.addAttribute("title", "Request Off");
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("requestsOff", requestOffDao.findByEmployeeId(employeeId));

        return "employee/request-off/index";

    }

    @RequestMapping(value = "day", method = RequestMethod.GET)
    public String displayRequestOffDayForm(Model model, HttpServletResponse response, HttpServletRequest request,
                                           @CookieValue(value = "user") String username) {

        RedirectIfNotLoggedIn(username, response);

        model.addAttribute("title", "Request Off Day");
        model.addAttribute("employeeId", employeeDao.findByEmail(username).getId());
        model.addAttribute(new RequestOff());

        return "employee/request-off/day";

    }

    @RequestMapping(value = "time", method = RequestMethod.GET)
    public String displayRequestOffTimeForm(Model model, HttpServletResponse response, HttpServletRequest request,
                                            @CookieValue(value = "user", defaultValue = "none") String username) {

        RedirectIfNotLoggedIn(username, response);

        model.addAttribute("title", "Request Off Time");
        model.addAttribute("employeeId", employeeDao.findByEmail(username).getId());
        model.addAttribute(new RequestOff());

        return "employee/request-off/time";

    }

    @RequestMapping(value = "day", method = RequestMethod.POST)
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

        return "redirect:/employee/request-off";
    }

    @RequestMapping(value = "time", method = RequestMethod.POST)
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

        return "redirect:/employee/request-off";

    }

}
