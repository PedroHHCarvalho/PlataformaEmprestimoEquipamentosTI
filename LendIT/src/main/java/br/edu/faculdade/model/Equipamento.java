package br.edu.faculdade.model;

import br.edu.faculdade.model.enums.StatusEquipamento;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Equipamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String patrimonio;
    private String modelo;

    @Enumerated(EnumType.STRING)
    private StatusEquipamento status;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
