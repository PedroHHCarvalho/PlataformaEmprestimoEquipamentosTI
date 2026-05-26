package br.edu.faculdade.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    // Essa é a rota que o Spring Security procura após o login dar certo!
    @GetMapping("/dashboard")
    public String telaDashboard() {
        return "dashboard"; // O Spring vai procurar o arquivo dashboard.html
    }
}