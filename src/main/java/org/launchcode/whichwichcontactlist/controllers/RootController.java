package org.launchcode.whichwichcontactlist.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "")
public class RootController {

    @RequestMapping(value = "")
    public String sendToHomePage(Model model) {

        return "redirect:home";

    }

}
