package de.web192.lagersoftware.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Zeigt nur die Login-Seite an. Die eigentliche Anmeldung (POST /login)
 * verarbeitet Spring Security selbst (siehe SecurityConfig), dafuer ist
 * kein eigener Controller-Code noetig.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
