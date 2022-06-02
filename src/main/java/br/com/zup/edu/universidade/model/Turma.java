package br.com.zup.edu.universidade.model;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import br.com.zup.edu.universidade.exception.MatriculaAlunoException;

@Entity
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Disciplina disciplina;

    @Column(nullable = false)
    private LocalDate inicio;

    @Column(nullable = false)
    private LocalDate fim;

    @ManyToOne(optional = false)
    private Professor professor;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Aluno> alunos = new LinkedHashSet<>();

    public Turma(Disciplina disciplina, LocalDate inicio, LocalDate fim, Professor professor) {
        this.disciplina = disciplina;
        this.inicio = inicio;
        this.fim = fim;
        this.professor = professor;
        professor.adicionar(this);
        disciplina.adicionar(this);
    }

    @Deprecated
    public Turma() {}

    public Long getId() {
        return id;
    }

    public void adicionar(Aluno aluno) {
        if (isMatriculado(aluno)) {
            throw new MatriculaAlunoException("Aluno já matriculado");
        }

        this.alunos.add(aluno);
        aluno.adicionar(this);
    }

    public boolean isMatriculado(Aluno aluno) {
        return alunos.contains(aluno);
    }

    public void trocar(Professor novoProfessor) {
        this.professor.remover(this);
        this.professor = novoProfessor;
        novoProfessor.adicionar(this);
    }

    public void remover(Aluno aluno) {
        if (!isMatriculado(aluno)) {
            throw new MatriculaAlunoException("Não é possivel desfazer uma matricula inexistente");
        }

        this.alunos.remove(aluno);
        aluno.remover(this);
    }

}
