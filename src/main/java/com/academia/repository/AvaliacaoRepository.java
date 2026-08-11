package com.academia.repository;

import com.academia.model.AvaliacaoFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<AvaliacaoFisica, Long> {
    
    // Encontrar avaliações por aluno
    List<AvaliacaoFisica> findByAlunoCpf(String cpfAluno);

    // Últimas 5 avaliações do aluno ordenadas por data desc
    List<AvaliacaoFisica> findTop5ByAlunoCpfOrderByDataDesc(String cpfAluno);
    
    // Encontrar avaliações por instrutor
    List<AvaliacaoFisica> findByInstrutorCpf(String cpfInstrutor);
    
    // Encontrar avaliações por data
    List<AvaliacaoFisica> findByData(LocalDate data);
    
    // Encontrar avaliações entre datas
    List<AvaliacaoFisica> findByDataBetween(LocalDate inicio, LocalDate fim);
    
    // Encontrar última avaliação do aluno
    @Query("SELECT a FROM AvaliacaoFisica a WHERE a.aluno.cpf = :cpfAluno ORDER BY a.data DESC")
    List<AvaliacaoFisica> findUltimaAvaliacaoByAluno(@Param("cpfAluno") String cpfAluno);
    
    // Contar avaliações por aluno
    long countByAlunoCpf(String cpfAluno);

    @Query(value = "SELECT * FROM avaliacao_fisica a WHERE a.cpf_aluno = :cpf AND " +
       "DATE_FORMAT(a.data, '%d/%m/%Y') LIKE %:q%", nativeQuery = true)
    List<AvaliacaoFisica> buscarPorDiaMesAno(@Param("cpf") String cpf, @Param("q") String q);
}
