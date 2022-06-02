package br.com.zup.edu.universidade.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Questao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String resposta;

    @Column(nullable = false)
    private BigDecimal valor;

    public Questao(String descricao, String resposta, BigDecimal valor) {
        this.descricao = descricao;
        this.resposta = resposta;
        this.valor = valor;
    }

    @Deprecated
    public Questao() {}

    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Questao{" + "id=" + id + ", descricao='" + descricao + '\'' + ", resposta='"
                + resposta + '\'' + ", valor=" + valor + '}';
    }

}
