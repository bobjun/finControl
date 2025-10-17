package br.com.meuGasto.finControl.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class PlanejamentoCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoria;
    private BigDecimal valorPlanejado;

    @ManyToOne
    @JoinColumn(name = "planejamento_id")
    private PlanejamentoMensal planejamento;

    // getters e setters
    public Long getId() { return id; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public BigDecimal getValorPlanejado() { return valorPlanejado; }
    public void setValorPlanejado(BigDecimal valorPlanejado) { this.valorPlanejado = valorPlanejado; }
}
