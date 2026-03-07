package com.nex.ticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/charges-ui")
    public String chargesUI() {
        return "redirect:/charges.html";
    }
}
