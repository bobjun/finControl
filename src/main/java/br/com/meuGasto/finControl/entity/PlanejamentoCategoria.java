package br.com.meuGasto.finControl.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Entity
@Getter
@Setter
public class PlanejamentoCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A categoria é obrigatória")
    private String categoria;

    @NotNull(message = "O valor planejado é obrigatório")
    @PositiveOrZero(message = "O valor planejado deve ser maior ou igual a zero")
    @Column(name = "valor_planejado")
    private BigDecimal valorPlanejado;

    @Column(name = "alerta_limite")
    private Integer alertaLimite = 80; // Valor padrão de 80%

    @Column(length = 1000)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planejamento_mensal_id")
    private PlanejamentoMensal planejamentoMensal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planejamento_id")
    @Deprecated
    private Planejamento planejamento;

    @OneToMany(mappedBy = "planejamentoCategoria")
    private List<Gasto> gastos;

    public void setPlanejamentoMensal(PlanejamentoMensal planejamentoMensal) {
        this.planejamentoMensal = planejamentoMensal;
    }

    @Deprecated
    public void setPlanejamento(Planejamento planejamento) {
        this.planejamento = planejamento;
    }

    public BigDecimal getValorRealizado() {
        if (gastos == null || gastos.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return gastos.stream()
                .map(Gasto::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public double getPercentualRealizado() {
        if (valorPlanejado == null || valorPlanejado.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return getValorRealizado()
                .multiply(new BigDecimal("100"))
                .divide(valorPlanejado, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public boolean isAlertaAtivado() {
        return getPercentualRealizado() >= alertaLimite;
    }
}
