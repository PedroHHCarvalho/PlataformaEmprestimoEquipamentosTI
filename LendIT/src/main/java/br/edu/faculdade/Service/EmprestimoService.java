package br.edu.faculdade.Service;

import br.edu.faculdade.DTO.*;
import br.edu.faculdade.model.enums.*;
import br.edu.faculdade.model.*;
import br.edu.faculdade.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor // O Lombok gera o construtor automaticamente para os atributos 'final'
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final ColaboradorRepository colaboradorRepository;
    private final EquipamentoRepository equipamentoRepository;

    @Transactional
    public EmprestimoResponseDTO criarEmprestimo(EmprestimoRequestDTO request) {

        Colaborador colaborador = colaboradorRepository.findById(request.idColaborador())
                .orElseThrow(() -> new RuntimeException("Colaborador não encontrado"));

        if (colaborador.isBloqueado()) {
            throw new RuntimeException("Colaborador está bloqueado e não pode realizar empréstimos.");
        }

        Equipamento equipamento = equipamentoRepository.findById(request.idEquipamento())
                .orElseThrow(() -> new RuntimeException("Equipamento não encontrado"));

        if (equipamento.getStatus() != StatusEquipamento.DISPONIVEL) {
            throw new RuntimeException("Equipamento não está disponível para empréstimo.");
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setColaborador(colaborador);
        emprestimo.setEquipamento(equipamento);
        emprestimo.setDataRetirada(LocalDate.now());
        emprestimo.setDataDevolucaoPrevista(request.dataDevolucaoPrevista());
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        equipamento.setStatus(StatusEquipamento.EMPRESTADO);
        equipamentoRepository.save(equipamento);

        Emprestimo salvo = emprestimoRepository.save(emprestimo);

        return new EmprestimoResponseDTO(
                salvo.getId(),
                colaborador.getNome(),
                equipamento.getModelo(),
                salvo.getStatus().name()
        );
    }

    @Transactional
    public void devolverEquipamento(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));

        emprestimo.finalizarEmprestimo();
        emprestimoRepository.save(emprestimo);
    }
}