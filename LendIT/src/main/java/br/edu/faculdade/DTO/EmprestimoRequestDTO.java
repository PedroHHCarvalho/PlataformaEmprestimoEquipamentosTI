package br.edu.faculdade.DTO;

import java.time.LocalDate;

public record EmprestimoRequestDTO(
        Long idColaborador,
        Long idEquipamento,
        LocalDate dataDevolucaoPrevista
) {}
