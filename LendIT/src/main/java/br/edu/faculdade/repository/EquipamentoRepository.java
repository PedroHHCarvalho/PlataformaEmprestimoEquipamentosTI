package br.edu.faculdade.repository;

import br.edu.faculdade.model.Equipamento;
import br.edu.faculdade.model.enums.StatusEquipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
    List<Equipamento> findByStatus(StatusEquipamento status);
}
