package br.edu.faculdade.security;

import br.edu.faculdade.model.Colaborador;
import br.edu.faculdade.repository.ColaboradorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacaoService implements UserDetailsService {

    private final ColaboradorRepository repository;

    @Override
    public UserDetails loadUserByUsername(String matricula) throws UsernameNotFoundException {
        Colaborador colab = repository.findByMatricula(matricula)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        // Converte o nosso Colaborador para o formato de Usuário que o Spring Security entende
        return User.builder()
                .username(colab.getMatricula())
                .password(colab.getSenha()) // A senha já estará criptografada no banco
                .roles("USER")
                .build();
    }
}