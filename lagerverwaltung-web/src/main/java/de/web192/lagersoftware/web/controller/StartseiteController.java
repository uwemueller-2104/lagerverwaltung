package de.web192.lagersoftware.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StartseiteController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
