package com.academia.repository;

import com.academia.model.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {
    
    // Encontrar exercícios por nome (pesquisa parcial)
    List<Exercicio> findByNomeContainingIgnoreCase(String nome);
    
    // Encontrar exercícios por grupo muscular
    List<Exercicio> findByGrupoMuscular(String grupoMuscular);
    
    // Encontrar exercícios por equipamento
    List<Exercicio> findByEquipamentoContainingIgnoreCase(String equipamento);
    
    // Busca avançada
    @Query("SELECT e FROM Exercicio e WHERE " +
           "LOWER(e.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(e.grupoMuscular) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(e.equipamento) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Exercicio> buscarPorTermo(@Param("termo") String termo);
}