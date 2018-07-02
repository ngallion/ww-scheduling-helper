package org.launchcode.whichwichcontactlist.controllers;

import org.launchcode.whichwichcontactlist.models.RequestOff;
import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.launchcode.whichwichcontactlist.models.data.RequestOffDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@Controller
@RequestMapping(value = "view-requests")
public class ViewRequestOffsController {

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    RequestOffDao requestOffDao;

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

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model, HttpServletResponse response, HttpServletRequest request,
                        @CookieValue(value = "user", defaultValue = "none") String username) {

        RedirectIfNotLoggedIn(username,response);

        Iterable<RequestOff> requestOffs = requestOffDao.findAllByOrderByDate();

        model.addAttribute("requestOffs", requestOffs);
        model.addAttribute("title", "View All Requests");
        if (username.equals("none")) {
            model.addAttribute("username", "guest");
        }
        else {
            model.addAttribute("username",employeeDao.findByEmail(username).getFirstName());
        }

        return "view-requests/index";

    }

    @RequestMapping(value = "by-day", method = RequestMethod.GET)
    public String viewRequestOffByDayForm(Model model, HttpServletResponse response, HttpServletRequest request,
                                          @CookieValue(value = "user", defaultValue = "none") String username) {

        RedirectIfNotLoggedIn(username,response);

        model.addAttribute("title", "View Requests by Day");
//
        if (username.equals("none")) {
            model.addAttribute("username", "guest");
        }
        else {
            model.addAttribute("username",employeeDao.findByEmail(username).getFirstName());
        }

        return "view-requests/by-day";

    }

    @RequestMapping(value = "by-day", method = RequestMethod.POST)
    public String processRequestOffByDayForm(Model model, @RequestParam("dateToView") String date,
                                             @CookieValue(value = "user", defaultValue = "none") String username) {

        if (date.equals("")) {
            model.addAttribute("title", "View Requests by Day");
            model.addAttribute("username",employeeDao.findByEmail(username).getFirstName());
            return "view-requests/by-day";
        }

        LocalDate localDate = LocalDate.parse(date);

        Iterable<RequestOff> requestOffs = requestOffDao.findByDate(Date.valueOf(localDate));
        ArrayList<RequestOff> activeRequestOffs = new ArrayList<>();

        for (RequestOff requestOff : requestOffs) {
            if (requestOff.isActive()) {
                activeRequestOffs.add(requestOff);
            }
        }

        model.addAttribute("requestOffs", requestOffDao.findByDate(Date.valueOf(localDate)));
        model.addAttribute("title", "Requests for " + localDate.getDayOfWeek()
                + " " +localDate.getMonth() + " " + localDate.getDayOfMonth());

        model.addAttribute("username",employeeDao.findByEmail(username).getFirstName());


        return "view-requests/by-day";

    }

}
