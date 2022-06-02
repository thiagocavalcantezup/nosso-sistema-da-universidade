package br.com.zup.edu.universidade.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import br.com.zup.edu.universidade.controller.request.AvaliacaoAlunoRequest;
import br.com.zup.edu.universidade.controller.request.RespostaQuestaoRequest;
import br.com.zup.edu.universidade.model.Aluno;
import br.com.zup.edu.universidade.model.Avaliacao;
import br.com.zup.edu.universidade.model.Questao;
import br.com.zup.edu.universidade.repository.AlunoRepository;
import br.com.zup.edu.universidade.repository.AvaliacaoRepository;
import br.com.zup.edu.universidade.repository.QuestaoRepository;
import br.com.zup.edu.universidade.repository.RespostaAvaliacaoRepository;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@ActiveProfiles("test")
public class FazerAvaliacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private QuestaoRepository questaoRepository;

    @Autowired
    private RespostaAvaliacaoRepository respostaAvaliacaoRepository;

    private Aluno aluno;
    private Questao questao1;
    private Questao questao2;
    private Questao questao3;
    private Avaliacao avaliacao;

    @BeforeEach
    void setUp() {
        alunoRepository.deleteAll();
        avaliacaoRepository.deleteAll();
        questaoRepository.deleteAll();
        respostaAvaliacaoRepository.deleteAll();

        aluno = new Aluno("Thiago", "123456", LocalDate.now().minusYears(30));
        aluno = alunoRepository.save(aluno);

        questao1 = new Questao("Quanto é 4 + 4?", "5", new BigDecimal("2.0"));
        questao2 = new Questao("Quanto é 11 + 6?", "16", new BigDecimal("2.0"));
        questao3 = new Questao("Quanto é 7 + 7?", "7", new BigDecimal("2.0"));
        avaliacao = new Avaliacao(Set.of(questao1, questao2, questao3));
        avaliacaoRepository.save(avaliacao);
    }

    @Test
    void naoDeveAvaliarUmConjuntoDeRespostasNulo() throws Exception {
        // cenário (given)
        //
        AvaliacaoAlunoRequest avaliacaoAlunoRequest = new AvaliacaoAlunoRequest(null);
        String requestPayload = objectMapper.writeValueAsString(avaliacaoAlunoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            "/alunos/{alunoId}/avaliacoes/{avaliacaoId}/respostas", aluno.getId(), avaliacao.getId()
        ).contentType(APPLICATION_JSON).content(requestPayload).header("Accept-Language", "pt-br");

        // ação (when) e corretude (then)
        //
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<String> mensagens = objectMapper.readValue(
            responsePayload, typeFactory.constructCollectionType(List.class, String.class)
        );

        assertThat(mensagens).hasSize(1).contains("O campo respostas não deve ser nulo");
    }

    @Test
    void naoDeveAvaliarUmConjuntoDeRespostasComPeloMenosUmIdNulo() throws Exception {
        // cenário (given)
        //
        RespostaQuestaoRequest respostaQuestaoRequest1 = new RespostaQuestaoRequest(null, "A");
        RespostaQuestaoRequest respostaQuestaoRequest2 = new RespostaQuestaoRequest(
            questao2.getId(), "B"
        );
        RespostaQuestaoRequest respostaQuestaoRequest3 = new RespostaQuestaoRequest(
            questao3.getId(), "C"
        );
        AvaliacaoAlunoRequest avaliacaoAlunoRequest = new AvaliacaoAlunoRequest(
            List.of(respostaQuestaoRequest1, respostaQuestaoRequest2, respostaQuestaoRequest3)
        );
        String requestPayload = objectMapper.writeValueAsString(avaliacaoAlunoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            "/alunos/{alunoId}/avaliacoes/{avaliacaoId}/respostas", aluno.getId(), avaliacao.getId()
        ).contentType(APPLICATION_JSON).content(requestPayload).header("Accept-Language", "pt-br");

        // ação (when) e corretude (then)
        //
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<String> mensagens = objectMapper.readValue(
            responsePayload, typeFactory.constructCollectionType(List.class, String.class)
        );

        assertThat(mensagens).hasSize(1)
                             .contains("O campo respostas[0].idQuestao não deve ser nulo");
    }

    @Test
    void naoDeveAvaliarUmConjuntoDeRespostasComPeloMenosUmIdNegativo() throws Exception {
        // cenário (given)
        //
        RespostaQuestaoRequest respostaQuestaoRequest1 = new RespostaQuestaoRequest(-1L, "A");
        RespostaQuestaoRequest respostaQuestaoRequest2 = new RespostaQuestaoRequest(
            questao2.getId(), "B"
        );
        RespostaQuestaoRequest respostaQuestaoRequest3 = new RespostaQuestaoRequest(
            questao3.getId(), "C"
        );
        AvaliacaoAlunoRequest avaliacaoAlunoRequest = new AvaliacaoAlunoRequest(
            List.of(respostaQuestaoRequest1, respostaQuestaoRequest2, respostaQuestaoRequest3)
        );
        String requestPayload = objectMapper.writeValueAsString(avaliacaoAlunoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            "/alunos/{alunoId}/avaliacoes/{avaliacaoId}/respostas", aluno.getId(), avaliacao.getId()
        ).contentType(APPLICATION_JSON).content(requestPayload).header("Accept-Language", "pt-br");

        // ação (when) e corretude (then)
        //
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<String> mensagens = objectMapper.readValue(
            responsePayload, typeFactory.constructCollectionType(List.class, String.class)
        );

        assertThat(mensagens).hasSize(1)
                             .contains("O campo respostas[0].idQuestao deve ser maior que 0");
    }

    @Test
    void naoDeveAvaliarUmConjuntoDeRespostasComPeloMenosUmaRespostaNulaOuEmBranco() throws Exception {
        // cenário (given)
        //
        RespostaQuestaoRequest respostaQuestaoRequest1 = new RespostaQuestaoRequest(
            questao1.getId(), null
        );
        RespostaQuestaoRequest respostaQuestaoRequest2 = new RespostaQuestaoRequest(
            questao2.getId(), "B"
        );
        RespostaQuestaoRequest respostaQuestaoRequest3 = new RespostaQuestaoRequest(
            questao3.getId(), "C"
        );
        AvaliacaoAlunoRequest avaliacaoAlunoRequest = new AvaliacaoAlunoRequest(
            List.of(respostaQuestaoRequest1, respostaQuestaoRequest2, respostaQuestaoRequest3)
        );
        String requestPayload = objectMapper.writeValueAsString(avaliacaoAlunoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            "/alunos/{alunoId}/avaliacoes/{avaliacaoId}/respostas", aluno.getId(), avaliacao.getId()
        ).contentType(APPLICATION_JSON).content(requestPayload).header("Accept-Language", "pt-br");

        // ação (when) e corretude (then)
        //
        String responsePayload = mockMvc.perform(requestBuilder)
                                        .andExpect(status().isBadRequest())
                                        .andReturn()
                                        .getResponse()
                                        .getContentAsString(UTF_8);

        TypeFactory typeFactory = objectMapper.getTypeFactory();
        List<String> mensagens = objectMapper.readValue(
            responsePayload, typeFactory.constructCollectionType(List.class, String.class)
        );

        assertThat(mensagens).hasSize(1)
                             .contains("O campo respostas[0].resposta não deve estar em branco");
    }

    @Test
    void naoDeveAvaliarAsRespostasDeUmAlunoNaoCadastrado() throws Exception {
        // cenário (given)
        //
        RespostaQuestaoRequest respostaQuestaoRequest1 = new RespostaQuestaoRequest(
            questao1.getId(), "A"
        );
        AvaliacaoAlunoRequest avaliacaoAlunoRequest = new AvaliacaoAlunoRequest(
            List.of(respostaQuestaoRequest1)
        );
        String requestPayload = objectMapper.writeValueAsString(avaliacaoAlunoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            "/alunos/{alunoId}/avaliacoes/{avaliacaoId}/respostas", Long.MAX_VALUE,
            avaliacao.getId()
        ).contentType(APPLICATION_JSON).content(requestPayload);

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
    void naoDeveAvaliarAsRespostasDeUmaAvaliacaoNaoCadastrada() throws Exception {
        // cenário (given)
        //
        RespostaQuestaoRequest respostaQuestaoRequest1 = new RespostaQuestaoRequest(
            questao1.getId(), "A"
        );
        AvaliacaoAlunoRequest avaliacaoAlunoRequest = new AvaliacaoAlunoRequest(
            List.of(respostaQuestaoRequest1)
        );
        String requestPayload = objectMapper.writeValueAsString(avaliacaoAlunoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            "/alunos/{alunoId}/avaliacoes/{avaliacaoId}/respostas", aluno.getId(), Long.MAX_VALUE
        ).contentType(APPLICATION_JSON).content(requestPayload);

        // ação (when) e corretude (then)
        //
        Exception resolvedException = mockMvc.perform(requestBuilder)
                                             .andExpect(status().isNotFound())
                                             .andReturn()
                                             .getResolvedException();

        assertNotNull(resolvedException);
        assertEquals(ResponseStatusException.class, resolvedException.getClass());
        ResponseStatusException responseStatusException = (ResponseStatusException) resolvedException;
        assertEquals("Avaliação não cadastrada", responseStatusException.getReason());
    }

    @Test
    void deveAvaliarAsRespostasDeUmAluno() throws Exception {
        // cenário (given)
        //
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();

        RespostaQuestaoRequest respostaQuestaoRequest1 = new RespostaQuestaoRequest(
            questao1.getId(), "A"
        );
        RespostaQuestaoRequest respostaQuestaoRequest2 = new RespostaQuestaoRequest(
            questao2.getId(), "B"
        );
        RespostaQuestaoRequest respostaQuestaoRequest3 = new RespostaQuestaoRequest(
            questao3.getId(), "C"
        );
        AvaliacaoAlunoRequest avaliacaoAlunoRequest = new AvaliacaoAlunoRequest(
            List.of(respostaQuestaoRequest1, respostaQuestaoRequest2, respostaQuestaoRequest3)
        );
        String requestPayload = objectMapper.writeValueAsString(avaliacaoAlunoRequest);

        MockHttpServletRequestBuilder requestBuilder = post(
            "/alunos/{alunoId}/avaliacoes/{avaliacaoId}/respostas", aluno.getId(), avaliacao.getId()
        ).contentType(APPLICATION_JSON).content(requestPayload).header("Accept-Language", "pt-br");

        // ação (when) e corretude (then)
        //
        String responseLocation = mockMvc.perform(requestBuilder)
                                         .andExpect(status().isCreated())
                                         .andExpect(
                                             redirectedUrlPattern(
                                                 baseUrl + "/alunos/*/avaliacoes/*/respostas/*"
                                             )
                                         )
                                         .andReturn()
                                         .getResponse()
                                         .getHeader("location");

        Integer respostaAvaliacaoIdIndex = responseLocation.lastIndexOf("/") + 1;
        Long respostaAvaliacaoId = Long.valueOf(
            responseLocation.substring(respostaAvaliacaoIdIndex)
        );

        assertTrue(
            respostaAvaliacaoRepository.existsById(respostaAvaliacaoId),
            "Deveria existir uma resposta de avaliação para este id"
        );
    }

}
