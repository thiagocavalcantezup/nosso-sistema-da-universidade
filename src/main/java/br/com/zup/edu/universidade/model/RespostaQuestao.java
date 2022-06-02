package br.com.zup.edu.universidade.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class RespostaQuestao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Aluno aluno;

    @ManyToOne(optional = false)
    private Questao questao;

    @Column(nullable = false)
    private String resposta;

    public RespostaQuestao(Aluno aluno, Questao questao, String resposta) {
        this.aluno = aluno;
        this.questao = questao;
        this.resposta = resposta;
    }

    @Deprecated
    public RespostaQuestao() {}

    public Long getId() {
        return id;
    }

    public Questao getQuestao() {
        return questao;
    }

    public String getResposta() {
        return resposta;
    }

    public Aluno getAluno() {
        return aluno;
    }

    @Override
    public String toString() {
        return "RespostaQuestao{" + "id=" + id + '}';
    }

}
