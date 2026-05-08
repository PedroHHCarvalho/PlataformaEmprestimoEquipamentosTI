package br.edu.faculdade.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Colaborador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String matricula;
    private String email;
    private boolean bloqueado;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;
}
