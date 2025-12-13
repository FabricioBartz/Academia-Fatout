package com.academia.service;

import com.academia.model.AvaliacaoFisica;
import com.academia.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AvaliacaoService {
    
    private final AvaliacaoRepository avaliacaoRepository;
    
    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }
    
    // Criar nova avaliação
    public AvaliacaoFisica criarAvaliacao(AvaliacaoFisica avaliacao) {
        // Validar dados obrigatórios
        if (avaliacao.getAluno() == null) {
            throw new RuntimeException("Aluno é obrigatório para a avaliação!");
        }
        
        if (avaliacao.getInstrutor() == null) {
            throw new RuntimeException("Instrutor é obrigatório para a avaliação!");
        }
        
        // Definir data atual se não informada
        if (avaliacao.getData() == null) {
            avaliacao.setData(LocalDate.now());
        }
        
        // Calcular IMC automaticamente
        if (avaliacao.getPeso() != null && avaliacao.getAltura() != null) {
            avaliacao.setImc(avaliacao.getPeso() / (avaliacao.getAltura() * avaliacao.getAltura()));
        }
        
        return avaliacaoRepository.save(avaliacao);
    }
    
    // Buscar avaliação por ID
    public Optional<AvaliacaoFisica> buscarPorId(Long id) {
        return avaliacaoRepository.findById(id);
    }
    
    // Listar todas as avaliações de um aluno
    public List<AvaliacaoFisica> listarPorAluno(String cpfAluno) {
        return avaliacaoRepository.findByAlunoCpf(cpfAluno);
    }
    
    // Listar todas as avaliações de um instrutor
    public List<AvaliacaoFisica> listarPorInstrutor(String cpfInstrutor) {
        return avaliacaoRepository.findByInstrutorCpf(cpfInstrutor);
    }
    
    // Buscar última avaliação do aluno
    public Optional<AvaliacaoFisica> buscarUltimaAvaliacao(String cpfAluno) {
        List<AvaliacaoFisica> avaliacoes = avaliacaoRepository.findUltimaAvaliacaoByAluno(cpfAluno);
        return avaliacoes.isEmpty() ? Optional.empty() : Optional.of(avaliacoes.get(0));
    }
    
    // Listar avaliações por período
    public List<AvaliacaoFisica> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return avaliacaoRepository.findByDataBetween(inicio, fim);
    }
    
    // Atualizar avaliação
    public AvaliacaoFisica atualizarAvaliacao(Long id, AvaliacaoFisica avaliacaoAtualizada) {
        return avaliacaoRepository.findById(id)
            .map(avaliacao -> {
                avaliacao.setData(avaliacaoAtualizada.getData());
                avaliacao.setPeso(avaliacaoAtualizada.getPeso());
                avaliacao.setAltura(avaliacaoAtualizada.getAltura());
                avaliacao.setPeito(avaliacaoAtualizada.getPeito());
                avaliacao.setCintura(avaliacaoAtualizada.getCintura());
                avaliacao.setQuadril(avaliacaoAtualizada.getQuadril());
                avaliacao.setBicepsDireito(avaliacaoAtualizada.getBicepsDireito());
                avaliacao.setBicepsEsquerdo(avaliacaoAtualizada.getBicepsEsquerdo());
                avaliacao.setCoxaDireita(avaliacaoAtualizada.getCoxaDireita());
                avaliacao.setCoxaEsquerda(avaliacaoAtualizada.getCoxaEsquerda());
                avaliacao.setPanturrilhaDireita(avaliacaoAtualizada.getPanturrilhaDireita());
                avaliacao.setPanturrilhaEsquerda(avaliacaoAtualizada.getPanturrilhaEsquerda());
                avaliacao.setObservacoes(avaliacaoAtualizada.getObservacoes());
                
                // Recalcular IMC
                if (avaliacao.getPeso() != null && avaliacao.getAltura() != null) {
                    avaliacao.setImc(avaliacao.getPeso() / (avaliacao.getAltura() * avaliacao.getAltura()));
                }
                
                return avaliacaoRepository.save(avaliacao);
            })
            .orElseThrow(() -> new RuntimeException("Avaliação não encontrada!"));
    }
    
    // Deletar avaliação
    public void deletarAvaliacao(Long id) {
        if (avaliacaoRepository.existsById(id)) {
            avaliacaoRepository.deleteById(id);
        } else {
            throw new RuntimeException("Avaliação não encontrada!");
        }
    }
    
    // Contar avaliações do aluno
    public long contarAvaliacoesDoAluno(String cpfAluno) {
        return avaliacaoRepository.countByAlunoCpf(cpfAluno);
    }
    
    // Calcular evolução entre duas avaliações
    public String calcularEvolucao(AvaliacaoFisica antiga, AvaliacaoFisica nova) {
        if (antiga == null || nova == null) {
            return "Não há dados suficientes para comparar";
        }
        
        StringBuilder evolucao = new StringBuilder();
        
        if (nova.getPeso() != null && antiga.getPeso() != null) {
            double diferencaPeso = nova.getPeso() - antiga.getPeso();
            evolucao.append(String.format("Peso: %.1f kg (%.1f kg)", nova.getPeso(), diferencaPeso));
        }
        
        if (nova.getImc() != null && antiga.getImc() != null) {
            double diferencaImc = nova.getImc() - antiga.getImc();
            evolucao.append(String.format("\nIMC: %.2f (%.2f)", nova.getImc(), diferencaImc));
        }
        
        return evolucao.toString();
    }
}