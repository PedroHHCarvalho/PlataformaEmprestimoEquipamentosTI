package br.edu.faculdade.Controller;

import br.edu.faculdade.model.Colaborador;
import br.edu.faculdade.model.Emprestimo;
import br.edu.faculdade.model.Equipamento;
import br.edu.faculdade.model.enums.StatusEmprestimo;
import br.edu.faculdade.model.enums.StatusEquipamento;
import br.edu.faculdade.repository.ColaboradorRepository;
import br.edu.faculdade.repository.EmprestimoRepository;
import br.edu.faculdade.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/emprestimo")
public class EmprestimoController {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @GetMapping("/novo/{idEquipamento}")
    public String formEmprestimo(@PathVariable Long idEquipamento, Model model, RedirectAttributes attributes) {
        Equipamento equipamento = equipamentoRepository.findById(idEquipamento)
                .orElse(null);

        if (equipamento == null || equipamento.getStatus() != StatusEquipamento.DISPONIVEL) {
            attributes.addFlashAttribute("mensagemErro", "Equipamento não encontrado ou indisponível para empréstimo.");
            return "redirect:/catalogo";
        }

        model.addAttribute("equipamento", equipamento);
        // Sugere a devolução para daqui a 7 dias
        model.addAttribute("dataPrevista", LocalDate.now().plusDays(7));
        return "form-emprestimo";
    }

    @PostMapping("/salvar")
    public String salvarEmprestimo(
            @RequestParam Long equipamentoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataDevolucaoPrevista,
            Principal principal,
            RedirectAttributes attributes) {

        Colaborador colaborador = colaboradorRepository.findByMatricula(principal.getName())
                .orElse(null);

        if (colaborador == null) {
            attributes.addFlashAttribute("mensagemErro", "Erro ao identificar o usuário logado.");
            return "redirect:/login";
        }

        Equipamento equipamento = equipamentoRepository.findById(equipamentoId)
                .orElse(null);

        if (equipamento == null || equipamento.getStatus() != StatusEquipamento.DISPONIVEL) {
            attributes.addFlashAttribute("mensagemErro", "Este equipamento não está mais disponível.");
            return "redirect:/catalogo";
        }

        if (dataDevolucaoPrevista.isBefore(LocalDate.now())) {
            attributes.addFlashAttribute("mensagemErro", "A data de devolução não pode ser no passado.");
            return "redirect:/emprestimo/novo/" + equipamentoId;
        }

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setColaborador(colaborador);
        emprestimo.setEquipamento(equipamento);
        emprestimo.setDataRetirada(LocalDate.now());
        emprestimo.setDataDevolucaoPrevista(dataDevolucaoPrevista);
        emprestimo.setStatus(StatusEmprestimo.ATIVO);

        equipamento.setStatus(StatusEquipamento.EMPRESTADO);

        emprestimoRepository.save(emprestimo);
        equipamentoRepository.save(equipamento);

        attributes.addFlashAttribute("mensagemSucesso", "Empréstimo realizado com sucesso! Retire o equipamento no setor de TI.");
        return "redirect:/dashboard";
    }

    @GetMapping("/meus-equipamentos")
    public String meusEquipamentos(Principal principal, Model model) {
        java.util.List<Emprestimo> emprestimos = emprestimoRepository.findByColaboradorMatriculaAndStatus(
                principal.getName(), StatusEmprestimo.ATIVO);
        model.addAttribute("emprestimos", emprestimos);
        return "meus-equipamentos";
    }

    @PostMapping("/devolver/{id}")
    public String devolverEquipamento(@PathVariable Long id, Principal principal, RedirectAttributes attributes) {
        Emprestimo emprestimo = emprestimoRepository.findById(id).orElse(null);

        if (emprestimo == null || !emprestimo.getColaborador().getMatricula().equals(principal.getName())) {
            attributes.addFlashAttribute("mensagemErro", "Empréstimo inválido ou não pertence a você.");
            return "redirect:/emprestimo/meus-equipamentos";
        }

        if (emprestimo.getStatus() == StatusEmprestimo.CONCLUIDO) {
            attributes.addFlashAttribute("mensagemErro", "Este equipamento já foi devolvido.");
            return "redirect:/emprestimo/meus-equipamentos";
        }

        // A regra de devolução e troca de status do equipamento já existe dentro da entidade Emprestimo!
        emprestimo.finalizarEmprestimo();
        
        // Salvamos no banco
        emprestimoRepository.save(emprestimo);
        
        attributes.addFlashAttribute("mensagemSucesso", "Devolução registrada com sucesso! Obrigado.");
        return "redirect:/dashboard";
    }
}