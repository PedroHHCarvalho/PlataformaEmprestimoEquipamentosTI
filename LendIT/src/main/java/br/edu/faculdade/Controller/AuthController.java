package br.edu.faculdade.Controller;

import br.edu.faculdade.model.Colaborador;
import br.edu.faculdade.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final ColaboradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String telaLogin() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String telaCadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String realizarCadastro(Colaborador colaborador) {
        if (repository.existsByMatricula(colaborador.getMatricula())) {
            return "redirect:/cadastro?error=matricula";
        }
        if (repository.existsByEmail(colaborador.getEmail())) {
            return "redirect:/cadastro?error=email";
        }

        // Criptografa a senha antes de salvar no banco
        colaborador.setSenha(passwordEncoder.encode(colaborador.getSenha()));
        colaborador.setBloqueado(false); // Usuário novo nasce desbloqueado
        repository.save(colaborador);

        // Redireciona para o login com uma mensagem de sucesso na URL
        return "redirect:/login?cadastrado=true";
    }
}
