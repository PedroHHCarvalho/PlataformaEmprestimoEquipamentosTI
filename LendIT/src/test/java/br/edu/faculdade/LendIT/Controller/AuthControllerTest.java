package br.edu.faculdade.LendIT.Controller;

import br.edu.faculdade.Controller.AuthController;
import br.edu.faculdade.model.Colaborador;
import br.edu.faculdade.repository.ColaboradorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ColaboradorRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void deveRetornarTelaDeLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void deveRetornarTelaDeCadastro() throws Exception {
        mockMvc.perform(get("/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"));
    }

    @Test
    void deveCadastrarNovoColaboradorComSucesso() throws Exception {
        when(repository.existsByMatricula("123")).thenReturn(false);
        when(repository.existsByEmail("teste@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");

        mockMvc.perform(post("/cadastro")
                        .param("nome", "Teste Nome")
                        .param("matricula", "123")
                        .param("email", "teste@teste.com")
                        .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?cadastrado=true"));

        verify(repository, times(1)).save(any(Colaborador.class));
    }

    @Test
    void naoDeveCadastrarColaboradorQuandoMatriculaJaExiste() throws Exception {
        when(repository.existsByMatricula("123")).thenReturn(true);

        mockMvc.perform(post("/cadastro")
                        .param("nome", "Teste")
                        .param("matricula", "123")
                        .param("email", "teste@teste.com")
                        .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cadastro?error=matricula"));

        verify(repository, never()).save(any(Colaborador.class));
    }

    @Test
    void naoDeveCadastrarColaboradorQuandoEmailJaExiste() throws Exception {
        when(repository.existsByMatricula("123")).thenReturn(false);
        when(repository.existsByEmail("teste@teste.com")).thenReturn(true);

        mockMvc.perform(post("/cadastro")
                        .param("nome", "Teste")
                        .param("matricula", "123")
                        .param("email", "teste@teste.com")
                        .param("senha", "senha123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cadastro?error=email"));

        verify(repository, never()).save(any(Colaborador.class));
    }

    @Test
    void naoDeveCadastrarQuandoCamposEstiveremInvalidos() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .param("nome", "")
                        .param("matricula", "")
                        .param("email", "email_invalido")
                        .param("senha", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("colaborador", "nome", "matricula", "email", "senha"));
        
        verify(repository, never()).save(any(Colaborador.class));
    }
}
