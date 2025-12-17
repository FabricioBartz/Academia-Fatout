package com.academia.repository;

import com.academia.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TurmaRepository extends JpaRepository<Turma, Long> {
    
    // Encontrar turmas por instrutor
    List<Turma> findByInstrutorCpf(String cpfInstrutor);
    
    // Encontrar turmas por título (pesquisa parcial)
    List<Turma> findByTituloContainingIgnoreCase(String titulo);
    
    // Encontrar turmas por data
    List<Turma> findByDataDaAula(LocalDate data);
    
    // Encontrar turmas futuras
    List<Turma> findByDataDaAulaGreaterThanEqual(LocalDate data);
    
    // Encontrar turmas com vagas disponíveis
    @Query("SELECT t FROM Turma t WHERE t.vagas > SIZE(t.alunos) ORDER BY t.dataDaAula ASC, t.horaAula ASC")
    List<Turma> findTurmasComVagasDisponiveis();
    
    // Encontrar turmas onde aluno está matriculado
    @Query("SELECT t FROM Turma t JOIN t.alunos a WHERE a.cpf = :cpfAluno")
    List<Turma> findTurmasByAlunoCpf(@Param("cpfAluno") String cpfAluno);
}