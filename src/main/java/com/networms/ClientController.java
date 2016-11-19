package com.networms;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Hunter on 11/18/16.
 */
@Controller
public class ClientController {

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public ModelAndView newDocument() {
        // TODO: Establish connection to server and get new docId
        String docId = "abcd1234";
        return new ModelAndView("redirect:/editor?id=" + docId);
    }

    @RequestMapping(value="editor", method = RequestMethod.GET)
    public void edit(@RequestParam(value="id") String docId,
                     Model model) {
        // TODO: Establish connection to server with given docId
        model.addAttribute("docId", docId);
    }
}
