package br.com.meuGasto.finControl.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Use {@link PlanejamentoMensal} instead. This class will be removed in a future version.
 */
@Deprecated
@Entity
public class Planejamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private YearMonth mesAno;

    @OneToMany(mappedBy = "planejamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanejamentoCategoria> categorias = new ArrayList<>();

    private BigDecimal valorPlanejadoTotal = BigDecimal.ZERO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public YearMonth getMesAno() {
        return mesAno;
    }

    public void setMesAno(YearMonth mesAno) {
        this.mesAno = mesAno;
    }

    public List<PlanejamentoCategoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<PlanejamentoCategoria> categorias) {
        this.categorias = categorias;
    }

    public BigDecimal getValorPlanejadoTotal() {
        return valorPlanejadoTotal;
    }

    public void setValorPlanejadoTotal(BigDecimal valorPlanejadoTotal) {
        this.valorPlanejadoTotal = valorPlanejadoTotal;
    }
}
