package com.academia.repository;

import com.academia.model.PlanoTreino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlanoTreinoRepository extends JpaRepository<PlanoTreino, Long> {
    
    // Encontrar planos por aluno
    List<PlanoTreino> findByAlunoCpf(String cpfAluno);
    
    // Encontrar planos por instrutor
    List<PlanoTreino> findByInstrutorCpf(String cpfInstrutor);
    
    // Encontrar plano ativo (mais recente) do aluno
    @Query("SELECT p FROM PlanoTreino p WHERE p.aluno.cpf = :cpfAluno ORDER BY p.dataCriacao DESC")
    List<PlanoTreino> findPlanoAtivoByAluno(@Param("cpfAluno") String cpfAluno);
    
    // Encontrar planos por data de criação
    List<PlanoTreino> findByDataCriacao(LocalDate data);
    
    // Verificar se aluno tem plano ativo
    @Query("SELECT COUNT(p) > 0 FROM PlanoTreino p WHERE p.aluno.cpf = :cpfAluno")
    boolean alunoTemPlanoAtivo(@Param("cpfAluno") String cpfAluno);

    
    // O campo deve ser 'Nome' (com N maiúsculo) se na sua classe PlanoTreino a variável for 'nome'
    List<PlanoTreino> findByAlunoCpfAndNomeContainingIgnoreCase(String cpfAluno, String nome);
    }