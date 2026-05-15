package br.edu.faculdade.LendIT.security;

import br.edu.faculdade.model.Colaborador;
import br.edu.faculdade.repository.ColaboradorRepository;
import br.edu.faculdade.security.AutenticacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutenticacaoServiceTest {

    @Mock
    private ColaboradorRepository repository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    private Colaborador colaborador;

    @BeforeEach
    void setUp() {
        colaborador = new Colaborador();
        colaborador.setMatricula("12345");
        colaborador.setSenha("senha123");
    }

    @Test
    void deveCarregarUsuarioPorMatriculaComSucesso() {
        when(repository.findByMatricula("12345")).thenReturn(Optional.of(colaborador));

        UserDetails userDetails = autenticacaoService.loadUserByUsername("12345");

        assertNotNull(userDetails);
        assertEquals("12345", userDetails.getUsername());
        assertEquals("senha123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        
        verify(repository, times(1)).findByMatricula("12345");
    }

    @Test
    void deveLancarExcecaoQuandoMatriculaNaoExistir() {
        when(repository.findByMatricula("invalida")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            autenticacaoService.loadUserByUsername("invalida");
        });

        verify(repository, times(1)).findByMatricula("invalida");
    }
}
