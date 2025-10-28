package br.com.meuGasto.finControl.dto;

import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.math.RoundingMode;

@Getter
public class PlanejamentoResumoDTO {
    private final YearMonth mesAno;
    private final BigDecimal valorPlanejado;
    private final BigDecimal valorGasto;
    private final BigDecimal saldo;
    private final BigDecimal percentualGasto;

    public PlanejamentoResumoDTO(PlanejamentoMensal planejamento, BigDecimal valorGasto) {
        this.mesAno = planejamento.getMesAno();
        this.valorPlanejado = planejamento.getValorPlanejadoTotal();
        this.valorGasto = valorGasto != null ? valorGasto : BigDecimal.ZERO;
        this.saldo = this.valorPlanejado.subtract(this.valorGasto);

        if (this.valorPlanejado.compareTo(BigDecimal.ZERO) > 0) {
            this.percentualGasto = this.valorGasto
                .multiply(new BigDecimal("100"))
                .divide(this.valorPlanejado, 2, RoundingMode.HALF_UP);
        } else {
            this.percentualGasto = BigDecimal.ZERO;
        }
    }
}
