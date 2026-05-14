package br.edu.faculdade.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "A matrícula é obrigatória")
    @Column(unique = true)
    private String matricula;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    @Column(unique = true)
    private String email;

    private boolean bloqueado;

    @ManyToOne
    @JoinColumn(name = "departamento_id")
    private Departamento departamento;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;
}
