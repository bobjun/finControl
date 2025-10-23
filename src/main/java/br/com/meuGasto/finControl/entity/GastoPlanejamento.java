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

    public Long getId() {
        return id;
    }   
    public void setId(Long id) {
        this.id = id;
    }   
    public String getDescricao() {
        return descricao;
    }   
    public void setDescricao(String descricao) {
        this.descricao = descricao; 
    }
    public String getCategoria() {
        return categoria;
    }   
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }   
    public BigDecimal getValor() {
        return valor;
    }
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    } 
    public LocalDate getData() {
        return data;
    }      
}

