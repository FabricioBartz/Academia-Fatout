package com.academia.repository;

import com.academia.model.Instrutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstrutorRepository extends JpaRepository<Instrutor, String> {
    
    // Encontrar instrutor por email
    Optional<Instrutor> findByEmail(String email);
    
    // Encontrar instrutor por email e senha (para login)
    Optional<Instrutor> findByEmailAndSenha(String email, String senha);
    
    // Verificar se existe instrutor por email
    boolean existsByEmail(String email);
}