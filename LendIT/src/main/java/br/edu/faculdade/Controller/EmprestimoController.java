package br.edu.faculdade.Controller;

import br.edu.faculdade.DTO.*;
import br.edu.faculdade.Service.EmprestimoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
@RequiredArgsConstructor // Injeção de dependência via construtor gerada pelo Lombok
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> solicitarEmprestimo(@RequestBody EmprestimoRequestDTO dto) {
        EmprestimoResponseDTO response = emprestimoService.criarEmprestimo(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/devolucao")
    public ResponseEntity<Void> devolverEquipamento(@PathVariable Long id) {
        emprestimoService.devolverEquipamento(id);
        return ResponseEntity.ok().build();
    }
}