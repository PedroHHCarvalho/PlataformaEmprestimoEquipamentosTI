package br.edu.faculdade.Controller;

import br.edu.faculdade.model.Categoria;
import br.edu.faculdade.model.Equipamento;
import br.edu.faculdade.model.enums.StatusEquipamento;
import br.edu.faculdade.repository.CategoriaRepository;
import br.edu.faculdade.repository.EquipamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    // --- Rotas para Categoria ---

    @GetMapping("/categorias/novo")
    public String formCategoria(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "admin/form-categoria";
    }

    @PostMapping("/categorias")
    public String salvarCategoria(@ModelAttribute Categoria categoria, RedirectAttributes attributes) {
        categoriaRepository.save(categoria);
        attributes.addFlashAttribute("mensagemSucesso", "Categoria cadastrada com sucesso!");
        return "redirect:/admin/categorias/novo";
    }

    // --- Rotas para Equipamento ---

    @GetMapping("/equipamentos/novo")
    public String formEquipamento(Model model) {
        model.addAttribute("equipamento", new Equipamento());
        model.addAttribute("categorias", categoriaRepository.findAll());
        model.addAttribute("statusList", StatusEquipamento.values());
        return "admin/form-equipamento";
    }

    @PostMapping("/equipamentos")
    public String salvarEquipamento(@ModelAttribute Equipamento equipamento, RedirectAttributes attributes) {
        equipamentoRepository.save(equipamento);
        attributes.addFlashAttribute("mensagemSucesso", "Equipamento cadastrado com sucesso!");
        return "redirect:/admin/equipamentos/novo";
    }
}
