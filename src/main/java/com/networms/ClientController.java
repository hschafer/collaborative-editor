package com.networms;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Hunter on 11/18/16.
 */
@Controller
@RequestMapping("/editor")
public class ClientController {

    @RequestMapping({"", "/"})
    public void landingPage(Model model) {
    }
}
