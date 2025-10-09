package br.com.meuGasto.finControl.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GastoDTO {
    private Long id;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(min = 3, max = 255, message = "A descrição deve ter entre 3 e 255 caracteres")
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valor;

    @NotBlank(message = "A categoria é obrigatória")
    @Size(min = 3, max = 100, message = "A categoria deve ter entre 3 e 100 caracteres")
    private String categoria;

    private LocalDateTime dataGasto;

    @Size(max = 500, message = "As observações não podem ter mais que 500 caracteres")
    private String observacoes;
}
