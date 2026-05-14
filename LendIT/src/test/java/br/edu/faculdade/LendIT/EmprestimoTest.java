package br.edu.faculdade.LendIT;

import br.edu.faculdade.model.Emprestimo;
import br.edu.faculdade.model.enums.StatusEmprestimo;
import br.edu.faculdade.model.enums.StatusEquipamento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {

    @Test
    @DisplayName("Deve retornar 0 dias de atraso se a devolução ainda estiver no prazo")
    void calcularDiasAtraso_NoPrazo() {
        // Arrange (Preparar)
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().plusDays(5)); // Prazo para o futuro

        // Act (Agir)
        int diasAtraso = emprestimo.calcularDiasAtraso();

        // Assert (Verificar)
        assertEquals(0, diasAtraso);
    }

    @Test
    @DisplayName("Deve calcular dias de atraso corretamente quando a data passar do prazo")
    void calcularDiasAtraso_ComAtraso() {
        // Arrange
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().minusDays(3)); // Prazo era há 3 dias

        // Act
        int diasAtraso = emprestimo.calcularDiasAtraso();

        // Assert
        assertEquals(3, diasAtraso);
    }
}