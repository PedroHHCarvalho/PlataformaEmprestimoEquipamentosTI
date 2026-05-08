package br.edu.faculdade.DTO;

public record EmprestimoResponseDTO(
        Long id,
        String nomeColaborador,
        String modeloEquipamento,
        String status
) {}
