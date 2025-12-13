package com.academia.service;

import com.academia.model.Aluno;
import com.academia.model.Turma;
import com.academia.repository.TurmaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TurmaService {
    
    private final TurmaRepository turmaRepository;
    private final AlunoService alunoService;
    
    public TurmaService(TurmaRepository turmaRepository, AlunoService alunoService) {
        this.turmaRepository = turmaRepository;
        this.alunoService = alunoService;
    }
    
    // Criar nova turma
    public Turma criarTurma(Turma turma) {
        // Validar se a turma tem vagas
        if (turma.getVagas() <= 0) {
            throw new RuntimeException("A turma deve ter pelo menos 1 vaga!");
        }
        
        // Validar se a data é futura
        if (turma.getDataDaAula().isBefore(LocalDate.now())) {
            throw new RuntimeException("A data da aula deve ser futura!");
        }
        
        return turmaRepository.save(turma);
    }
    
    // Buscar turma por ID
    public Optional<Turma> buscarPorId(Long id) {
        return turmaRepository.findById(id);
    }
    
    // Listar todas as turmas
    public List<Turma> listarTodas() {
        return turmaRepository.findAll();
    }
    
    // Listar turmas por instrutor
    public List<Turma> listarPorInstrutor(String cpfInstrutor) {
        return turmaRepository.findByInstrutorCpf(cpfInstrutor);
    }
    
    // Listar turmas com vagas disponíveis
    public List<Turma> listarComVagasDisponiveis() {
        return turmaRepository.findTurmasComVagasDisponiveis();
    }
    
    // Listar turmas de um aluno
    public List<Turma> listarTurmasDoAluno(String cpfAluno) {
        return turmaRepository.findTurmasByAlunoCpf(cpfAluno);
    }
    
    // Buscar turmas por título
    public List<Turma> buscarPorTitulo(String titulo) {
        return turmaRepository.findByTituloContainingIgnoreCase(titulo);
    }
    
    // Atualizar turma
    public Turma atualizarTurma(Long id, Turma turmaAtualizada) {
        return turmaRepository.findById(id)
            .map(turma -> {
                turma.setTitulo(turmaAtualizada.getTitulo());
                turma.setDescricao(turmaAtualizada.getDescricao());
                turma.setDataDaAula(turmaAtualizada.getDataDaAula());
                turma.setHoraAula(turmaAtualizada.getHoraAula());
                turma.setVagas(turmaAtualizada.getVagas());
                return turmaRepository.save(turma);
            })
            .orElseThrow(() -> new RuntimeException("Turma não encontrada!"));
    }
    
    // Deletar turma
    public void deletarTurma(Long id) {
        if (turmaRepository.existsById(id)) {
            turmaRepository.deleteById(id);
        } else {
            throw new RuntimeException("Turma não encontrada!");
        }
    }
    
    // Matricular aluno em turma
    public void matricularAluno(Long idTurma, String cpfAluno) {
        Turma turma = turmaRepository.findById(idTurma)
            .orElseThrow(() -> new RuntimeException("Turma não encontrada!"));
        
        Aluno aluno = alunoService.buscarPorCpf(cpfAluno)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado!"));
        
        // Verificar se turma está lotada
        if (turma.isLotada()) {
            throw new RuntimeException("Turma lotada! Não há vagas disponíveis.");
        }
        
        // Verificar se aluno já está matriculado
        if (turma.getAlunos().contains(aluno)) {
            throw new RuntimeException("Aluno já matriculado nesta turma!");
        }
        
        // Matricular aluno
        aluno.matricularEmTurma(turma);
        turmaRepository.save(turma);
    }
    
    // Desmatricular aluno de turma
    public void desmatricularAluno(Long idTurma, String cpfAluno) {
        Turma turma = turmaRepository.findById(idTurma)
            .orElseThrow(() -> new RuntimeException("Turma não encontrada!"));
        
        Aluno aluno = alunoService.buscarPorCpf(cpfAluno)
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado!"));
        
        // Verificar se aluno está matriculado
        if (!turma.getAlunos().contains(aluno)) {
            throw new RuntimeException("Aluno não está matriculado nesta turma!");
        }
        
        // Desmatricular aluno
        aluno.desmatricularDeTurma(turma);
        turmaRepository.save(turma);
    }
    
    // Verificar vagas disponíveis
    public Integer vagasDisponiveis(Long idTurma) {
        return turmaRepository.findById(idTurma)
            .map(Turma::getVagasDisponiveis)
            .orElseThrow(() -> new RuntimeException("Turma não encontrada!"));
    }
}