package br.com.meuGasto.finControl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvolucaoFinanceiraDTO {
    private String data;
    private BigDecimal receitas;
    private BigDecimal despesas;
    private BigDecimal saldo;
}

