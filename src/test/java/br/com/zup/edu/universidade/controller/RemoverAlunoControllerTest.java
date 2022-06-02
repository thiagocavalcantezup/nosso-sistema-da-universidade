package br.com.zup.edu.universidade.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.edu.universidade.model.Aluno;
import br.com.zup.edu.universidade.model.Avaliacao;
import br.com.zup.edu.universidade.model.Questao;
import br.com.zup.edu.universidade.model.RespostaAvaliacao;
import br.com.zup.edu.universidade.model.RespostaQuestao;
import br.com.zup.edu.universidade.repository.AlunoRepository;
import br.com.zup.edu.universidade.repository.AvaliacaoRepository;
import br.com.zup.edu.universidade.repository.QuestaoRepository;
import br.com.zup.edu.universidade.repository.RespostaAvaliacaoRepository;
import br.com.zup.edu.universidade.repository.RespostaQuestaoRepository;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
public class RemoverAlunoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private RespostaAvaliacaoRepository respostaAvaliacaoRepository;

    @Autowired
    private QuestaoRepository questaoRepository;

    @Autowired
    private RespostaQuestaoRepository respostaQuestaoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @BeforeEach
    void setUp() {
        respostaAvaliacaoRepository.deleteAll();
        respostaQuestaoRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        questaoRepository.deleteAll();
        alunoRepository.deleteAll();
    }

    @Test
    void naoDeveRemoverUmAlunoQueNaoEstaCadastrado() throws Exception {
        // cenário (given)
        //
        MockHttpServletRequestBuilder requestBuilder = delete(
            "/alunos/{id}", Long.MAX_VALUE
        ).contentType(APPLICATION_JSON);

        // ação (when) e corretude (then)
        //
        Exception resolvedException = mockMvc.perform(requestBuilder)
                                             .andExpect(status().isNotFound())
                                             .andReturn()
                                             .getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        ResponseStatusException responseStatusException = (ResponseStatusException) resolvedException;
        assertEquals("Aluno não cadastrado", responseStatusException.getReason());
    }

    @Test
    void deveRemoverUmAluno() throws Exception {
        // cenário (given)
        //
        Questao questao = new Questao("Quanto é 1 + 1?", "3", new BigDecimal("10.0"));
        Avaliacao avaliacao = new Avaliacao(Set.of(questao));
        avaliacaoRepository.save(avaliacao);

        Aluno aluno = new Aluno("Thiago", "123456", LocalDate.now().minusYears(30));
        RespostaQuestao respostaQuestao = new RespostaQuestao(aluno, questao, "2");
        RespostaAvaliacao respostaAvaliacao = new RespostaAvaliacao(
            aluno, avaliacao, Set.of(respostaQuestao)
        );
        aluno.adicionar(respostaAvaliacao);
        alunoRepository.save(aluno);

        MockHttpServletRequestBuilder requestBuilder = delete(
            "/alunos/{id}", aluno.getId()
        ).contentType(APPLICATION_JSON);

        // ação (when) e corretude (then)
        //
        mockMvc.perform(requestBuilder).andExpect(status().isNoContent());

        assertFalse(
            alunoRepository.existsById(aluno.getId()), "Não deveria existir um aluno para este id"
        );
        assertFalse(
            respostaQuestaoRepository.existsById(respostaQuestao.getId()),
            "Não deveria existir uma resposta de questão para este id"
        );
        assertFalse(
            respostaAvaliacaoRepository.existsById(respostaAvaliacao.getId()),
            "Não deveria existir uma resposta de avaliação para este id"
        );
    }

}
