package br.com.meuGasto.finControl.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class GastoPlanejamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;
    private String categoria;
    private BigDecimal valor;
    private LocalDate data;

    // getters e setters
}

