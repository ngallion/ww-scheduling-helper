package org.launchcode.whichwichcontactlist.utilities;

import org.launchcode.whichwichcontactlist.models.data.EmployeeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ControllerUtilities {

    @Autowired
    EmployeeDao employeeDao;

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

    private void ProvideUserNameInWelcomeMessage(Model model, String username) {

        if (username.equals("none")) {
            model.addAttribute("username", "guest");
        }
        else {
            model.addAttribute("username", employeeDao.findByEmail(username).getFirstName());
        }
    }

}
