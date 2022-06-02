package br.com.zup.edu.universidade.model;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade = {CascadeType.PERSIST})
    private Set<Questao> questoes = new LinkedHashSet<>();

    @Column(nullable = false)
    private LocalDateTime criadoEm = LocalDateTime.now();

    public Avaliacao(Set<Questao> questoes) {
        this.questoes = questoes;
    }

    public Avaliacao() {}

    public Long getId() {
        return id;
    }

    public Set<Questao> getQuestoes() {
        return questoes;
    }

}
