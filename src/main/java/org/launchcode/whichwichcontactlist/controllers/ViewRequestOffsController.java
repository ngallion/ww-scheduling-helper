package org.launchcode.whichwichcontactlist.controllers;

import org.launchcode.whichwichcontactlist.models.Employee;
import org.launchcode.whichwichcontactlist.models.RequestOff;
import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.launchcode.whichwichcontactlist.models.data.RequestOffDao;
import org.launchcode.whichwichcontactlist.utilities.ThisLocalizedWeek;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;

@Controller
@RequestMapping(value = "view-requests")
public class ViewRequestOffsController {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private RequestOffDao requestOffDao;

    private boolean userIsLoggedIn(String username) {
        if (username.equals("none")){
            return false;
        }
        return true;
    }

    private void ProvideUserNameInWelcomeMessage(Model model, String username) {

        if (username.equals("none")) {
            model.addAttribute("username", "guest");
        }
        else {
            model.addAttribute("username", employeeDao.findByEmail(username).getFirstName());
        }
    }

    private ArrayList<String> getViewableWeeks(){

        ArrayList<String> viewableWeeks = new ArrayList<>();

        ThisLocalizedWeek thisWeek = new ThisLocalizedWeek(Locale.FRANCE);

        for (int i = 0; i < 6; i++){
            String dateOfMonday = thisWeek.getFirstDay().plusWeeks(i).getMonth().getValue() + "/" +
                    thisWeek.getFirstDay().plusWeeks(i).getDayOfMonth();

            String dateOfSunday = thisWeek.getLastDay().plusWeeks(i).getMonth().getValue() + "/" +
                    thisWeek.getLastDay().plusWeeks(i).getDayOfMonth();

            viewableWeeks.add(dateOfMonday + "-" + dateOfSunday);
        }

        return viewableWeeks;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model, HttpServletResponse response, HttpServletRequest request,
                        @CookieValue(value = "user", defaultValue = "none") String username) {

        if (userIsLoggedIn(username) == false) {
            return "redirect:login";
        }
        ProvideUserNameInWelcomeMessage(model, username);

        Employee loggedInEmployee = employeeDao.findByEmail(username);

        Iterable<RequestOff> requestOffs = requestOffDao.findAllByOrderByDate();

        ArrayList<RequestOff> requestOffsFromUserStore = new ArrayList<>();

        ArrayList<RequestOff> requestOffsToDelete = new ArrayList<>();

        for (RequestOff requestOff : requestOffs){
            if (requestOff.getDate().before(Date.valueOf(LocalDate.now().minusWeeks(1))) || requestOff.isActive() == false){
                requestOffsToDelete.add(requestOff);
            }
            if (requestOff.getEmployee().getStore().equals(loggedInEmployee.getStore()) &&
                    requestOff.isActive() && requestOff.getDate().after(Date.valueOf(LocalDate.now()))){
                requestOffsFromUserStore.add(requestOff);
            }
        }

        requestOffDao.delete(requestOffsToDelete);

        model.addAttribute("requestOffs", requestOffsFromUserStore);
        model.addAttribute("title", "View All Requests");

        return "view-requests/index";

    }

    @RequestMapping(value = "by-day", method = RequestMethod.GET)
    public String viewRequestOffByDayForm(Model model, HttpServletResponse response, HttpServletRequest request,
                                          @CookieValue(value = "user", defaultValue = "none") String username) {

        if (userIsLoggedIn(username) == false) {
            return "redirect:login";
        }
        ProvideUserNameInWelcomeMessage(model, username);

        model.addAttribute("title", "View Requests by Day");

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

        Employee loggedInEmployee = employeeDao.findByEmail(username);

        LocalDate localDate = LocalDate.parse(date);

        Iterable<RequestOff> requestOffs = requestOffDao.findByDate(Date.valueOf(localDate));
        ArrayList<RequestOff> activeRequestOffs = new ArrayList<>();
        ArrayList<RequestOff> requestOffsToDelete = new ArrayList<>();

        for (RequestOff requestOff : requestOffs){
            if (requestOff.getDate().before(Date.valueOf(LocalDate.now().minusWeeks(1))) || requestOff.isActive() == false){
                requestOffsToDelete.add(requestOff);
            }
            if (requestOff.isActive() && requestOff.getEmployee().getStore().equals(loggedInEmployee.getStore())) {
                activeRequestOffs.add(requestOff);
            }
        }

        requestOffDao.delete(requestOffsToDelete);

        model.addAttribute("requestOffs", activeRequestOffs);
        model.addAttribute("title", "Requests for " + localDate.getDayOfWeek()
                + " " +localDate.getMonth() + " " + localDate.getDayOfMonth());

        model.addAttribute("username",employeeDao.findByEmail(username).getFirstName());

        return "view-requests/by-day";

    }

    @RequestMapping(value = "by-week", method = RequestMethod.GET)
    public String viewRequestOffByWeekForm(Model model, HttpServletResponse response, HttpServletRequest request,
                                          @CookieValue(value = "user", defaultValue = "none") String username) {

        if (userIsLoggedIn(username) == false) {
            return "redirect:login";
        }
        ProvideUserNameInWelcomeMessage(model, username);

        model.addAttribute("viewableWeeks", getViewableWeeks());
        model.addAttribute("title", "View Requests by Week");

        return "view-requests/by-week";

    }

    @RequestMapping(value = "by-week", method = RequestMethod.POST)
    public String processRequestOffByWeekForm(Model model, @RequestParam("weekToView") int weekToView,
                                             @CookieValue(value = "user", defaultValue = "none") String username) {

        Employee loggedInEmployee = employeeDao.findByEmail(username);

        ThisLocalizedWeek thisWeek = new ThisLocalizedWeek(Locale.FRANCE);



        LocalDate monday = thisWeek.getFirstDay().plusWeeks(weekToView);
        LocalDate sunday = thisWeek.getLastDay().plusWeeks(weekToView);

        Iterable<RequestOff> requestOffs = requestOffDao.findAll();
        ArrayList<RequestOff> activeRequestOffsToBeViewed = new ArrayList<>();

        for (RequestOff requestOff : requestOffs){
            if (requestOff.isActive() && requestOff.getEmployee().getStore().equals(loggedInEmployee.getStore()) &&
                    requestOff.getDate().after(Date.valueOf(monday.minusDays(1))) &&
                    requestOff.getDate().before(Date.valueOf(sunday.plusDays(1)))) {
                activeRequestOffsToBeViewed.add(requestOff);
            }
        }

        String title = "Requests for " + monday.getMonth().getValue() + "/" + monday.getDayOfMonth() + " thru " +
                sunday.getMonth().getValue() + "/" + sunday.getDayOfMonth();

        model.addAttribute("viewableWeeks", getViewableWeeks());
        model.addAttribute("requestOffs", activeRequestOffsToBeViewed);
        model.addAttribute("title", title);

        model.addAttribute("username",employeeDao.findByEmail(username).getFirstName());

        return "view-requests/by-week";

    }

}
