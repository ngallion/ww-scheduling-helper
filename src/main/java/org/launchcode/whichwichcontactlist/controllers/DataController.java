package org.launchcode.whichwichcontactlist.controllers;

import org.launchcode.whichwichcontactlist.models.JobTitle;
import org.launchcode.whichwichcontactlist.models.Store;
import org.launchcode.whichwichcontactlist.models.data.JobTitleDao;
import org.launchcode.whichwichcontactlist.models.data.StoreDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "data")
public class DataController {

    @Autowired
    private JobTitleDao jobTitleDao;

    @Autowired
    private StoreDao storeDao;

    @RequestMapping(value = "add-job-title", method = RequestMethod.GET)
    public String displayAddJobTitleForm(Model model){

        model.addAttribute("title", "Add Job Title");
        model.addAttribute("jobTitles", jobTitleDao.findAll());
        model.addAttribute(new JobTitle());

        return "data/add-job-title";

    }

    @RequestMapping(value = "add-job-title", method = RequestMethod.POST)
    public String processAddJobTitleForm(@ModelAttribute @Valid JobTitle jobTitle, Errors errors,
                                         Model model) {

        if(errors.hasErrors()) {
            model.addAttribute("title", "Add Job Title");

            return "data/add-job-title";
        }

        jobTitleDao.save(jobTitle);

        return "redirect:add-job-title";

    }

    @RequestMapping(value = "add-store", method = RequestMethod.GET)
    public String displayAddStoreForm(Model model) {

        model.addAttribute("title", "Add Store");
        model.addAttribute("stores", storeDao.findAll());
        model.addAttribute(new Store());

        return "data/add-store";
    }

    @RequestMapping(value = "add-store", method = RequestMethod.POST)
    public String processAddStoreForm(@ModelAttribute @Valid Store store, Errors errors,
                                         Model model) {

        if(errors.hasErrors()) {
            model.addAttribute("title", "Add Job Title");

            return "data/add-job-title";
        }

        storeDao.save(store);

        return "redirect:add-store";

    }

}
