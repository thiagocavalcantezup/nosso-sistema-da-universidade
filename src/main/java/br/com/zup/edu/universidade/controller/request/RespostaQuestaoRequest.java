package br.com.zup.edu.universidade.controller.request;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.web.server.ResponseStatusException;

import br.com.zup.edu.universidade.model.Aluno;
import br.com.zup.edu.universidade.model.Questao;
import br.com.zup.edu.universidade.model.RespostaQuestao;

public class RespostaQuestaoRequest {

    @NotNull
    @Positive
    private Long idQuestao;

    @NotBlank
    private String resposta;

    public RespostaQuestaoRequest(Long idQuestao, String resposta) {
        this.idQuestao = idQuestao;
        this.resposta = resposta;
    }

    public RespostaQuestaoRequest() {}

    public RespostaQuestao paraRespostaQuestao(Aluno aluno, Set<Questao> questoes) {
        Questao questao = questoes.stream()
                                  .filter(q -> q.getId().equals(idQuestao))
                                  .findFirst()
                                  .orElseThrow(
                                      () -> new ResponseStatusException(
                                          UNPROCESSABLE_ENTITY,
                                          String.format(
                                              "Nao existe cadastro para questao com id %d",
                                              idQuestao
                                          )
                                      )
                                  );

        return new RespostaQuestao(aluno, questao, resposta);
    }

    public Long getIdQuestao() {
        return idQuestao;
    }

    public String getResposta() {
        return resposta;
    }

}
