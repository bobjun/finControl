package br.com.meuGasto.finControl.repository;


import br.com.meuGasto.finControl.entity.GastoPlanejamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.LocalDate;

public interface GastoPlanejamentoRepository extends JpaRepository<GastoPlanejamento, Long> {

    @Query("SELECT COALESCE(SUM(g.valor), 0) " +
            "FROM GastoPlanejamento g " +
            "WHERE g.data BETWEEN :inicio AND :fim")
    BigDecimal sumByPeriodo(LocalDate inicio, LocalDate fim);

}

