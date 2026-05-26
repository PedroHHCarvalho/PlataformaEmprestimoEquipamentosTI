package br.edu.faculdade.Controller;

import br.edu.faculdade.model.Equipamento;
import br.edu.faculdade.model.enums.StatusEquipamento;
import br.edu.faculdade.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CatalogoController {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    @GetMapping("/catalogo")
    public String listarCatalogo(Model model) {
        // Busca apenas equipamentos com status DISPONIVEL
        List<Equipamento> disponiveis = equipamentoRepository.findByStatus(StatusEquipamento.DISPONIVEL);
        model.addAttribute("equipamentos", disponiveis);
        return "catalogo";
    }
}
