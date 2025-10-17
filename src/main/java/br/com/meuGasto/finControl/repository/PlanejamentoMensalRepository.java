package br.com.meuGasto.finControl.repository;


import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Optional;

public interface PlanejamentoMensalRepository extends JpaRepository<PlanejamentoMensal, Long> {
    Optional<PlanejamentoMensal> findByMesAno(YearMonth mesAno);
}

