package br.com.meuGasto.finControl.entity;

import jakarta.persistence.*;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PlanejamentoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Convert(converter = YearMonthConverter.class)
    private YearMonth mesAno;

    @Setter
    private BigDecimal valorPlanejadoTotal;

    @OneToMany(mappedBy = "planejamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanejamentoCategoria> categorias = new ArrayList<>();

    // getters e setters
    public Long getId() { return id; }
    public YearMonth getMesAno() { return mesAno; }

    public BigDecimal getValorPlanejadoTotal() { return valorPlanejadoTotal; }

    public List<PlanejamentoCategoria> getCategorias() { return categorias; }
}
