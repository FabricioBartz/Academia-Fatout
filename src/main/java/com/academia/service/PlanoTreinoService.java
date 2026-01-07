package com.academia.service;

import com.academia.model.PlanoTreino;
import com.academia.repository.PlanoTreinoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PlanoTreinoService {
    
    private final PlanoTreinoRepository planoTreinoRepository;
    
    public PlanoTreinoService(PlanoTreinoRepository planoTreinoRepository) {
        this.planoTreinoRepository = planoTreinoRepository;
    }
    
    // Criar novo plano de treino
    public PlanoTreino criarPlanoTreino(PlanoTreino planoTreino) {
        // Validar dados obrigatórios
        if (planoTreino.getAluno() == null) {
            throw new RuntimeException("Aluno é obrigatório para o plano de treino!");
        }
        
        if (planoTreino.getInstrutor() == null) {
            throw new RuntimeException("Instrutor é obrigatório para o plano de treino!");
        }
        
        if (planoTreino.getNome() == null || planoTreino.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do plano de treino é obrigatório!");
        }
        
        // Definir data de criação se não informada
        if (planoTreino.getDataCriacao() == null) {
            planoTreino.setDataCriacao(LocalDate.now());
        }
        
        return planoTreinoRepository.save(planoTreino);
    }
    
    // Buscar plano por ID
    public Optional<PlanoTreino> buscarPorId(Long id) {
        return planoTreinoRepository.findById(id);
    }
    
    // Listar todos os planos de um aluno
    public List<PlanoTreino> listarPorAluno(String cpfAluno) {
        return planoTreinoRepository.findByAlunoCpf(cpfAluno);
    }
    
    // Listar todos os planos de um instrutor
    public List<PlanoTreino> listarPorInstrutor(String cpfInstrutor) {
        return planoTreinoRepository.findByInstrutorCpf(cpfInstrutor);
    }
    
    // Buscar plano ativo (mais recente) do aluno
    public Optional<PlanoTreino> buscarPlanoAtivo(String cpfAluno) {
        List<PlanoTreino> planos = planoTreinoRepository.findPlanoAtivoByAluno(cpfAluno);
        return planos.isEmpty() ? Optional.empty() : Optional.of(planos.get(0));
    }
    
    // Verificar se aluno tem plano ativo
    public boolean alunoTemPlanoAtivo(String cpfAluno) {
        return planoTreinoRepository.alunoTemPlanoAtivo(cpfAluno);
    }
    
    // Atualizar plano de treino
    public PlanoTreino atualizarPlanoTreino(Long id, PlanoTreino planoTreinoAtualizado) {
        return planoTreinoRepository.findById(id)
            .map(planoTreino -> {
                planoTreino.setNome(planoTreinoAtualizado.getNome());
                planoTreino.setTempo(planoTreinoAtualizado.getTempo());
                planoTreino.setObservacoes(planoTreinoAtualizado.getObservacoes());
                // Atualiza dias da semana se fornecido
                if (planoTreinoAtualizado.getDiasSemana() != null) {
                    planoTreino.setDiasSemana(planoTreinoAtualizado.getDiasSemana());
                }
                return planoTreinoRepository.save(planoTreino);
            })
            .orElseThrow(() -> new RuntimeException("Plano de treino não encontrado!"));
    }
    
    // Deletar plano de treino
    public void deletarPlanoTreino(Long id) {
        if (planoTreinoRepository.existsById(id)) {
            planoTreinoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Plano de treino não encontrado!");
        }
    }

    // Alias: excluir plano de treino (compatibilidade com controlador)
    public void excluirPlanoTreino(Long id) {
        deletarPlanoTreino(id);
    }
    
    // Adicionar exercício ao plano
    public PlanoTreino adicionarExercicio(Long idPlano, Long idExercicio, Integer series, Integer repeticoes, Double carga) {
        // Esta seria uma implementação mais complexa que requereria
        // um serviço para ExercicioPlano. Aqui está uma versão simplificada.
        
        return planoTreinoRepository.findById(idPlano)
            .map(planoTreino -> {
                // Em uma implementação real, você criaria um objeto ExercicioPlano
                // e o associaria ao plano
                System.out.println("Exercício " + idExercicio + " adicionado ao plano " + idPlano);
                return planoTreinoRepository.save(planoTreino);
            })
            .orElseThrow(() -> new RuntimeException("Plano de treino não encontrado!"));
    }
    
    // Remover exercício do plano
    public void removerExercicio(Long idPlano, Long idExercicioPlano) {
        // Similar ao método acima, requereria manipulação de ExercicioPlano
        System.out.println("Removendo exercício " + idExercicioPlano + " do plano " + idPlano);
    }
    
    // Listar histórico de planos do aluno (ordenado por data)
    public List<PlanoTreino> listarHistorico(String cpfAluno) {
        return planoTreinoRepository.findByAlunoCpf(cpfAluno);
    }

    // Método para buscar planos do aluno por termo (nome)


    public List<PlanoTreino> buscarPorAlunoETermo(String cpfAluno, String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return listarPorAluno(cpfAluno);
        }
        // Agora ele usa o método que você acabou de criar no Repository
        return planoTreinoRepository.findByAlunoCpfAndNomeContainingIgnoreCase(cpfAluno, termo.trim());
    }
}