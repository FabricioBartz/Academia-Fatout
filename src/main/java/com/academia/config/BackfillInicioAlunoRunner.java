package com.academia.config;

import com.academia.model.Aluno;
import com.academia.model.AvaliacaoFisica;
import com.academia.repository.AlunoRepository;
import com.academia.repository.AvaliacaoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class BackfillInicioAlunoRunner implements CommandLineRunner {

    private final AlunoRepository alunoRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public BackfillInicioAlunoRunner(AlunoRepository alunoRepository, AvaliacaoRepository avaliacaoRepository) {
        this.alunoRepository = alunoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Override
    public void run(String... args) {
        List<Aluno> alunos = alunoRepository.findAll();
        int atualizados = 0;
        for (Aluno a : alunos) {
            if (a.getDataCadastro() == null) {
                List<AvaliacaoFisica> avals = avaliacaoRepository.findByAlunoCpf(a.getCpf());
                LocalDate primeiraData = null;
                for (AvaliacaoFisica av : avals) {
                    if (av.getData() != null) {
                        primeiraData = (primeiraData == null || av.getData().isBefore(primeiraData)) ? av.getData() : primeiraData;
                    }
                }
                if (primeiraData != null) {
                    a.setDataCadastro(primeiraData);
                    alunoRepository.save(a);
                    atualizados++;
                }
            }
        }
        if (atualizados > 0) {
            System.out.println("[Backfill] Data de início preenchida para " + atualizados + " aluno(s) com base na primeira avaliação.");
        }
    }
}
