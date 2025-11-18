package br.com.meuGasto.finControl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoDTO {
    private Long id;
    private String descricao;
    private String tipo;
    private BigDecimal valor;
    private String categoria;
    // Accept date-only values (e.g. "2025-11-14") from clients
    private LocalDate data;
    private String observacoes;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
}
