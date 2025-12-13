package com.academia.repository;

import com.academia.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, String> {
    
    // Encontrar aluno por email
    Optional<Aluno> findByEmail(String email);
    
    // Encontrar alunos por nome (pesquisa parcial)
    List<Aluno> findByNomeContainingIgnoreCase(String nome);
    
    // Verificar se existe aluno por email
    boolean existsByEmail(String email);
    
    // Encontrar aluno por email e senha (para login)
    Optional<Aluno> findByEmailAndSenha(String email, String senha);
}