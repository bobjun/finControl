package br.com.meuGasto.finControl.repository;

import br.com.meuGasto.finControl.entity.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GastoPlanejamentoRepository extends JpaRepository<Gasto, Long> {

    @Query("SELECT COALESCE(SUM(g.valor), 0) FROM Gasto g WHERE DATE(g.dataGasto) BETWEEN :inicio AND :fim")
    BigDecimal sumByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT COUNT(g) > 0 FROM Gasto g WHERE DATE(g.dataGasto) BETWEEN :inicio AND :fim")
    boolean existsByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT g.categoria, COALESCE(SUM(g.valor), 0) FROM Gasto g " +
           "WHERE DATE(g.dataGasto) BETWEEN :inicio AND :fim " +
           "GROUP BY g.categoria")
    List<Object[]> sumByCategoriaPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT g FROM Gasto g WHERE DATE(g.dataGasto) BETWEEN :inicio AND :fim ORDER BY g.dataGasto DESC")
    List<Gasto> findByPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
}
