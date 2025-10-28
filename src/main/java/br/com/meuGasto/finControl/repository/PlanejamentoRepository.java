package br.com.meuGasto.finControl.repository;

import br.com.meuGasto.finControl.entity.Planejamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.YearMonth;
import java.util.Optional;

public interface PlanejamentoRepository extends JpaRepository<Planejamento, Long> {
    Optional<Planejamento> findByMesAno(YearMonth mesAno);
}
