package br.com.meuGasto.finControl.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "A data é obrigatória")
    private LocalDateTime dataGasto;

    @NotNull(message = "O valor é obrigatório")
    @PositiveOrZero(message = "O valor deve ser maior ou igual a zero")
    private BigDecimal valor;

    @NotNull(message = "A descrição é obrigatória")
    private String descricao;

    private String observacoes;

    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private PlanejamentoCategoria planejamentoCategoria;

    // Backing persistent categoria column to preserve compatibility with existing
    // queries and Spring Data derived methods that expect a String 'categoria' property.
    @Column(name = "categoria")
    private String categoria;

    @ManyToOne
    @JoinColumn(name = "planejamento_mensal_id")
    private PlanejamentoMensal planejamentoMensal;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        if (dataGasto == null) {
            dataGasto = LocalDateTime.now();
        }
        // ensure categoria string is synced from planejamentoCategoria when creating
        if (this.planejamentoCategoria != null && (this.categoria == null || this.categoria.isBlank())) {
            this.categoria = this.planejamentoCategoria.getCategoria();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
        // keep categoria string in sync with planejamentoCategoria if present
        if (this.planejamentoCategoria != null) {
            this.categoria = this.planejamentoCategoria.getCategoria();
        }
    }

    public LocalDate getData() {
        return dataGasto != null ? dataGasto.toLocalDate() : null;
    }

    public void setData(LocalDate data) {
        if (data != null) {
            this.dataGasto = data.atStartOfDay();
        }
    }

    public void setPlanejamentoMensal(PlanejamentoMensal planejamentoMensal) {
        this.planejamentoMensal = planejamentoMensal;
    }

    // Convenience accessors for legacy code that expects a `categoria` String on Gasto
    // These map to the PlanejamentoCategoria relationship. The getter returns the
    // categoria name (or null) and the setter will update the persistent `categoria`
    // field and attempt to update the planejamentoCategoria name if present.
    public String getCategoria() {
        if (this.categoria != null && !this.categoria.isBlank()) {
            return this.categoria;
        }
        return planejamentoCategoria != null ? planejamentoCategoria.getCategoria() : null;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
        if (categoria == null) {
            return;
        }
        if (this.planejamentoCategoria != null) {
            this.planejamentoCategoria.setCategoria(categoria);
        }
    }
}
