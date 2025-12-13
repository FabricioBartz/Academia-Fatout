package com.academia.service;

import com.academia.model.Exercicio;
import com.academia.repository.ExercicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExercicioService {
    
    private final ExercicioRepository exercicioRepository;
    
    public ExercicioService(ExercicioRepository exercicioRepository) {
        this.exercicioRepository = exercicioRepository;
    }
    
    // Cadastrar novo exercício
    public Exercicio cadastrarExercicio(Exercicio exercicio) {
        // Validar dados obrigatórios
        if (exercicio.getNome() == null || exercicio.getNome().trim().isEmpty()) {
            throw new RuntimeException("Nome do exercício é obrigatório!");
        }
        
        return exercicioRepository.save(exercicio);
    }
    
    // Buscar exercício por ID
    public Optional<Exercicio> buscarPorId(Long id) {
        return exercicioRepository.findById(id);
    }
    
    // Listar todos os exercícios
    public List<Exercicio> listarTodos() {
        return exercicioRepository.findAll();
    }
    
    // Buscar exercícios por nome
    public List<Exercicio> buscarPorNome(String nome) {
        return exercicioRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    // Buscar exercícios por grupo muscular
    public List<Exercicio> buscarPorGrupoMuscular(String grupoMuscular) {
        return exercicioRepository.findByGrupoMuscular(grupoMuscular);
    }
    
    // Busca avançada por termo
    public List<Exercicio> buscarPorTermo(String termo) {
        return exercicioRepository.buscarPorTermo(termo);
    }
    
    // Atualizar exercício
    public Exercicio atualizarExercicio(Long id, Exercicio exercicioAtualizado) {
        return exercicioRepository.findById(id)
            .map(exercicio -> {
                exercicio.setNome(exercicioAtualizado.getNome());
                exercicio.setEquipamento(exercicioAtualizado.getEquipamento());
                exercicio.setGrupoMuscular(exercicioAtualizado.getGrupoMuscular());
                exercicio.setInstrucoes(exercicioAtualizado.getInstrucoes());
                exercicio.setDescricao(exercicioAtualizado.getDescricao());
                return exercicioRepository.save(exercicio);
            })
            .orElseThrow(() -> new RuntimeException("Exercício não encontrado!"));
    }
    
    // Deletar exercício
    public void deletarExercicio(Long id) {
        if (exercicioRepository.existsById(id)) {
            exercicioRepository.deleteById(id);
        } else {
            throw new RuntimeException("Exercício não encontrado!");
        }
    }
    
    // Contar total de exercícios
    public long contarExercicios() {
        return exercicioRepository.count();
    }
    
    // Listar grupos musculares disponíveis
    public List<String> listarGruposMusculares() {
        return List.of(
            "Peito", "Costas", "Ombros", "Biceps", "Triceps",
            "Pernas", "Abdômen", "Glúteos", "Panturrilhas", "Full Body"
        );
    }
}