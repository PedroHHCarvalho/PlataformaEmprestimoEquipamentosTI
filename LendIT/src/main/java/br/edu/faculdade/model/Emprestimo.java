package br.edu.faculdade.model;

import br.edu.faculdade.model.enums.StatusEquipamento;
import br.edu.faculdade.model.enums.StatusEmprestimo;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaborador colaborador;

    @ManyToOne
    @JoinColumn(name = "equipamento_id")
    private Equipamento equipamento;

    private LocalDate dataRetirada;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoReal;

    @Enumerated(EnumType.STRING)
    private StatusEmprestimo status;

    // Regras de negócio encapsuladas na entidade
    public int calcularDiasAtraso() {
        if (dataDevolucaoReal == null && LocalDate.now().isAfter(dataDevolucaoPrevista)) {
            return (int) ChronoUnit.DAYS.between(dataDevolucaoPrevista, LocalDate.now());
        }
        return 0;
    }

    public void finalizarEmprestimo() {
        this.dataDevolucaoReal = LocalDate.now();
        this.status = StatusEmprestimo.CONCLUIDO;
        this.equipamento.setStatus(StatusEquipamento.DISPONIVEL);
    }
}
