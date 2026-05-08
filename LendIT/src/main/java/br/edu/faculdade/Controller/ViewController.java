package br.edu.faculdade.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String telaLogin() {
        return "login"; // O Spring Boot vai procurar exatamente pelo login.html na pasta templates
    }
}
