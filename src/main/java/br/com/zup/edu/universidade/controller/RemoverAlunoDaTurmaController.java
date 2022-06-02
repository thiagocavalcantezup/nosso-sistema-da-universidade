package br.com.zup.edu.universidade.controller;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import javax.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.zup.edu.universidade.model.Aluno;
import br.com.zup.edu.universidade.model.Turma;
import br.com.zup.edu.universidade.repository.AlunoRepository;
import br.com.zup.edu.universidade.repository.TurmaRepository;

@RestController
public class RemoverAlunoDaTurmaController {

    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;

    public RemoverAlunoDaTurmaController(AlunoRepository alunoRepository,
                                         TurmaRepository turmaRepository) {
        this.alunoRepository = alunoRepository;
        this.turmaRepository = turmaRepository;
    }

    @DeleteMapping("/turmas/{idTurma}/alunos/{idAluno}")
    @Transactional
    public ResponseEntity<?> matricular(@PathVariable Long idTurma, @PathVariable Long idAluno) {
        Turma turma = turmaRepository.findById(
            idTurma
        ).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Turma nao cadastrada"));

        Aluno aluno = alunoRepository.findById(
            idAluno
        ).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Aluno nao cadastrado"));

        if (!turma.isMatriculado(aluno)) {
            throw new ResponseStatusException(
                UNPROCESSABLE_ENTITY, "Não é posssivel desfazer uma matricula inexistente"
            );
        }

        turma.remover(aluno);

        return ResponseEntity.noContent().build();
    }

}
