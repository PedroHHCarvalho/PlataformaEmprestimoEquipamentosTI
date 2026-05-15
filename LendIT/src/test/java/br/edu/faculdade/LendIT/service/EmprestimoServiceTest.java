package br.edu.faculdade.LendIT.service;

import br.edu.faculdade.DTO.EmprestimoRequestDTO;
import br.edu.faculdade.DTO.EmprestimoResponseDTO;
import br.edu.faculdade.Service.EmprestimoService;
import br.edu.faculdade.model.Colaborador;
import br.edu.faculdade.model.Emprestimo;
import br.edu.faculdade.model.Equipamento;
import br.edu.faculdade.model.enums.StatusEmprestimo;
import br.edu.faculdade.model.enums.StatusEquipamento;
import br.edu.faculdade.repository.ColaboradorRepository;
import br.edu.faculdade.repository.EmprestimoRepository;
import br.edu.faculdade.repository.EquipamentoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Liga os "Poderes" do Mockito nesta classe
class EmprestimoServiceTest {

    // 1. Criamos os Dublês (Mocks) dos repositórios
    @Mock
    private ColaboradorRepository colaboradorRepository;

    @Mock
    private EquipamentoRepository equipamentoRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    // 2. Injetamos os dublês no nosso Serviço real
    @InjectMocks
    private EmprestimoService emprestimoService;

    @Test
    @DisplayName("Regra 1: Deve impedir empréstimo se o colaborador estiver bloqueado")
    void criarEmprestimo_ColaboradorBloqueado() {
        // Arrange (Preparar o cenário)
        Colaborador colabBloqueado = new Colaborador();
        colabBloqueado.setId(1L);
        colabBloqueado.setBloqueado(true); // O problema está aqui!

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(1L, 10L, LocalDate.now().plusDays(5));

        // Ensinando o dublê: "Quando procurarem o ID 1, devolva o colabBloqueado"
        when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(colabBloqueado));

        // Act & Assert (Agir e Verificar se explodiu o erro certo)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.criarEmprestimo(request);
        });

        assertEquals("Colaborador está bloqueado e não pode realizar empréstimos.", exception.getMessage());

        // Verifica que o sistema parou e NÃO tentou salvar nada no banco
        verify(emprestimoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Regra 2: Deve impedir empréstimo se o equipamento não estiver disponível")
    void criarEmprestimo_EquipamentoIndisponivel() {
        // Arrange
        Colaborador colabValido = new Colaborador();
        colabValido.setId(1L);
        colabValido.setBloqueado(false);

        Equipamento eqpManutencao = new Equipamento();
        eqpManutencao.setId(10L);
        eqpManutencao.setStatus(StatusEquipamento.EM_MANUTENCAO); // O problema está aqui!

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(1L, 10L, LocalDate.now().plusDays(5));

        when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(colabValido));
        when(equipamentoRepository.findById(10L)).thenReturn(Optional.of(eqpManutencao));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emprestimoService.criarEmprestimo(request);
        });

        assertEquals("Equipamento não está disponível para empréstimo.", exception.getMessage());
    }

    @Test
    @DisplayName("Sucesso: Deve criar o empréstimo quando tudo estiver correto")
    void criarEmprestimo_ComSucesso() {
        // Arrange
        Colaborador colabValido = new Colaborador();
        colabValido.setId(1L);
        colabValido.setNome("João");
        colabValido.setBloqueado(false);

        Equipamento eqpDisponivel = new Equipamento();
        eqpDisponivel.setId(10L);
        eqpDisponivel.setModelo("Dell Inspiron");
        eqpDisponivel.setStatus(StatusEquipamento.DISPONIVEL);

        // O empréstimo que esperamos que seja salvo
        Emprestimo emprestimoSalvo = new Emprestimo();
        emprestimoSalvo.setId(99L); // ID gerado pelo banco
        emprestimoSalvo.setStatus(StatusEmprestimo.ATIVO);

        EmprestimoRequestDTO request = new EmprestimoRequestDTO(1L, 10L, LocalDate.now().plusDays(5));

        // Treinando os dublês
        when(colaboradorRepository.findById(1L)).thenReturn(Optional.of(colabValido));
        when(equipamentoRepository.findById(10L)).thenReturn(Optional.of(eqpDisponivel));
        when(emprestimoRepository.save(any(Emprestimo.class))).thenReturn(emprestimoSalvo);

        // Act
        EmprestimoResponseDTO response = emprestimoService.criarEmprestimo(request);

        // Assert
        assertNotNull(response);
        assertEquals("João", response.nomeColaborador());
        assertEquals("Dell Inspiron", response.modeloEquipamento());
        assertEquals("ATIVO", response.status());

        // Verifica se o status do equipamento mudou para EMPRESTADO
        assertEquals(StatusEquipamento.EMPRESTADO, eqpDisponivel.getStatus());

        // Verifica se o repositório foi chamado para salvar
        verify(emprestimoRepository, times(1)).save(any(Emprestimo.class));
    }
}