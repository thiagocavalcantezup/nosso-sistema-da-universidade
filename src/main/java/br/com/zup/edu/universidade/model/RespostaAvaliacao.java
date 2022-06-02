package br.com.zup.edu.universidade.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class RespostaAvaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Aluno aluno;

    @ManyToOne(optional = false)
    private Avaliacao avaliacao;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    private Set<RespostaQuestao> respostas = new LinkedHashSet<>();

    public RespostaAvaliacao(Aluno aluno, Avaliacao avaliacao, Set<RespostaQuestao> respostas) {
        this.aluno = aluno;
        this.avaliacao = avaliacao;
        this.respostas = respostas;
    }

    @Deprecated
    public RespostaAvaliacao() {}

    public Set<RespostaQuestao> getRespostas() {
        return respostas;
    }

    public Long getId() {
        return id;
    }

    public boolean pertence(Aluno aluno) {
        return this.aluno.equals(aluno);
    }

}
