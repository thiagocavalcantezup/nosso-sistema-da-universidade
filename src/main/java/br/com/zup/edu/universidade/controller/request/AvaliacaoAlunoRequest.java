package br.com.zup.edu.universidade.controller.request;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import br.com.zup.edu.universidade.model.Aluno;
import br.com.zup.edu.universidade.model.Avaliacao;
import br.com.zup.edu.universidade.model.RespostaAvaliacao;
import br.com.zup.edu.universidade.model.RespostaQuestao;

public class AvaliacaoAlunoRequest {

    @NotNull
    @Valid
    private List<RespostaQuestaoRequest> respostas;

    public AvaliacaoAlunoRequest(List<RespostaQuestaoRequest> respostas) {
        this.respostas = respostas;
    }

    public AvaliacaoAlunoRequest() {}

    public RespostaAvaliacao paraRespostaAvaliacao(Aluno aluno, Avaliacao avaliacao) {

        Set<RespostaQuestao> respostasQuestoes = respostas.stream()
                                                          .map(
                                                              request -> request.paraRespostaQuestao(
                                                                  aluno, avaliacao.getQuestoes()
                                                              )
                                                          )
                                                          .collect(Collectors.toSet());

        return new RespostaAvaliacao(aluno, avaliacao, respostasQuestoes);
    }

    public List<RespostaQuestaoRequest> getRespostas() {
        return respostas;
    }

}
