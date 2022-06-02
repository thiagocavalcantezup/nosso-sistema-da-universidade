package br.com.zup.edu.universidade.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String matricula;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @ManyToMany(mappedBy = "alunos")
    private Set<Turma> turmas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "aluno", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<RespostaAvaliacao> avaliacoes = new ArrayList<>();

    public Aluno(String nome, String matricula, LocalDate dataNascimento) {
        this.nome = nome;
        this.matricula = matricula;
        this.dataNascimento = dataNascimento;
    }

    @Deprecated
    public Aluno() {}

    public Long getId() {
        return id;
    }

    public void adicionar(Turma turma) {
        this.turmas.add(turma);
    }

    public void remover(Turma turma) {
        this.turmas.remove(turma);
    }

    public void adicionar(RespostaAvaliacao respostaAvaliacao) {
        this.avaliacoes.add(respostaAvaliacao);
    }

}
