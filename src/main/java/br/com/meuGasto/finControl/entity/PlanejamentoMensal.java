package br.com.meuGasto.finControl.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class PlanejamentoMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    @OneToMany(mappedBy = "planejamentoMensal", cascade = CascadeType.ALL)
    private List<Gasto> gastos = new ArrayList<>();

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "meta_economia")
    private BigDecimal metaEconomia;

    @OneToMany(mappedBy = "planejamentoMensal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanejamentoCategoria> categorias = new ArrayList<>();

    private YearMonth mesAno;

    @PrePersist
    @PreUpdate
    protected void calcularValorTotal() {
        this.valorTotal = gastos.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addCategoria(PlanejamentoCategoria categoria) {
        categorias.add(categoria);
        categoria.setPlanejamentoMensal(this);
    }

    public YearMonth getMesAno() {
        return mesAno != null ? mesAno : (data != null ? YearMonth.from(data) : null);
    }

    public void setMesAno(YearMonth mesAno) {
        this.mesAno = mesAno;
    }

    public BigDecimal getValorPlanejadoTotal() {
        return categorias.stream()
                .map(PlanejamentoCategoria::getValorPlanejado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getValorRealizadoTotal() {
        return gastos.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public double getPercentualGasto() {
        BigDecimal planejado = getValorPlanejadoTotal();
        if (planejado.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return getValorRealizadoTotal()
                .multiply(new BigDecimal("100"))
                .divide(planejado, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
