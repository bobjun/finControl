package br.com.meuGasto.finControl.dto;


import br.com.meuGasto.finControl.entity.PlanejamentoMensal;
import java.math.BigDecimal;
import java.time.YearMonth;

public class PlanejamentoResumoDTO {
    private YearMonth mesAno;
    private BigDecimal planejado;
    private BigDecimal gasto;
    private BigDecimal saldo;

    public PlanejamentoResumoDTO(PlanejamentoMensal p, BigDecimal gasto) {
        this.mesAno = p.getMesAno();
        this.planejado = p.getValorPlanejadoTotal();
        this.gasto = gasto;
        this.saldo = p.getValorPlanejadoTotal().subtract(gasto);
    }

    public YearMonth getMesAno() { return mesAno; }
    public BigDecimal getPlanejado() { return planejado; }
    public BigDecimal getGasto() { return gasto; }
    public BigDecimal getSaldo() { return saldo; }
}

