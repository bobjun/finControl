package br.com.meuGasto.finControl.repository;


import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface PlanejamentoMensalRepository extends JpaRepository<PlanejamentoMensal, Long> {
    Optional<PlanejamentoMensal> findByMesAno(YearMonth mesAno);

    // Retorna todos os planejamentos para um dado mes/ano (pode haver duplicatas no DB)
    List<PlanejamentoMensal> findAllByMesAno(YearMonth mesAno);

    // Retorna o planejamento mais recente (maior mesAno) — usado como fallback
    Optional<PlanejamentoMensal> findTopByOrderByMesAnoDesc();
}
