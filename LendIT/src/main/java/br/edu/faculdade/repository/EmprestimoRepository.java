package br.edu.faculdade.repository;

import br.edu.faculdade.model.*;
import br.edu.faculdade.model.enums.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    List<Emprestimo> findByColaboradorMatriculaAndStatus(String matricula, StatusEmprestimo status);
}
