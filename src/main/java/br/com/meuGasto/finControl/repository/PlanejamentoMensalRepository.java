package br.com.meuGasto.finControl.repository;

import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PlanejamentoMensalRepository extends JpaRepository<PlanejamentoMensal, Long> {
    Optional<PlanejamentoMensal> findByDataBetween(LocalDate dataInicio, LocalDate dataFim);
}
