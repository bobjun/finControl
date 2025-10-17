package br.com.meuGasto.finControl.repository;

import br.com.meuGasto.finControl.entity.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByDataGastoBetween(LocalDateTime inicio, LocalDateTime fim);
    List<Gasto> findByCategoria(String categoria);
    List<Gasto> findByDescricaoContainingIgnoreCase(String descricao);
    List<Gasto> findByValorGreaterThan(BigDecimal valor);
    List<Gasto> findByValorLessThan(BigDecimal valor);
    List<Gasto> findByValorBetween(BigDecimal minimo, BigDecimal maximo);
    List<Gasto> findByCategoriaAndValorGreaterThan(String categoria, BigDecimal valor);
    List<Gasto> findByOrderByDataGastoDesc();
    List<Gasto> findByOrderByValorDesc();
    List<Gasto> findByOrderByCategoriaAsc();

    @Query("SELECT g FROM Gasto g WHERE LOWER(g.descricao) LIKE LOWER(CONCAT('%', :palavraChave, '%'))")
    List<Gasto> findByPalavraChave(@Param("palavraChave") String palavraChave);

    Long countByCategoria(String categoria);

    @Query("SELECT SUM(g.valor) FROM Gasto g WHERE g.categoria = :categoria")
    BigDecimal sumValorByCategoria(@Param("categoria") String categoria);

    @Query("SELECT SUM(g.valor) FROM Gasto g")
    BigDecimal sumTotalValor();

    @Query("SELECT g FROM Gasto g WHERE g.valor > (SELECT AVG(g2.valor) FROM Gasto g2)")
    List<Gasto> findGastosAcimaDaMedia();

    @Query("SELECT g FROM Gasto g ORDER BY g.valor DESC")
    List<Gasto> findTop5GastosMaisCaros();

    List<Gasto> findByCategoriaAndDataGastoBetween(String categoria, LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT g FROM Gasto g WHERE g.categoria = :categoria AND g.dataGasto BETWEEN :inicio AND :fim")
    List<Gasto> findByCategoriaAndPeriodo(@Param("categoria") String categoria,
                                         @Param("inicio") LocalDateTime inicio,
                                         @Param("fim") LocalDateTime fim);

    @Query("SELECT SUM(g.valor) FROM Gasto g WHERE g.dataGasto BETWEEN :inicio AND :fim")
    BigDecimal calcularTotalGastoNoPeriodo(@Param("inicio") LocalDateTime inicio,
                                          @Param("fim") LocalDateTime fim);

    @Query("SELECT g.categoria, COUNT(g) as quantidade, SUM(g.valor) as total " +
           "FROM Gasto g " +
           "WHERE g.dataGasto BETWEEN :inicio AND :fim " +
           "GROUP BY g.categoria " +
           "ORDER BY total DESC")
    List<Object[]> findCategoriasSumarioPorPeriodo(@Param("inicio") LocalDateTime inicio,
                                                  @Param("fim") LocalDateTime fim);


}
