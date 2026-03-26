package com.nex.ticket.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/")
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class WebController {

    @GetMapping("/charges-ui")
    public String chargesUI() {
        return "redirect:/charges.html";
    }

    @GetMapping(value = { "/charge", "/charge/", "/charge/{path:[^\\.]*}" })
    public String forward(HttpServletRequest request) {
        // Forward to static index while preserving query string explicitly
        String qs = request.getQueryString();
        String target = "/share/index.html";
        return (qs != null && !qs.isEmpty())
                ? "forward:" + target + "?" + qs
                : "forward:" + target;
    }

}
