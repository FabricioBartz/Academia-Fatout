package com.academia.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aluno")
public class Aluno extends Pessoa {
    
    private String objetivo;
    
    @ManyToMany
    @JoinTable(
        name = "aluno_turma",
        joinColumns = @JoinColumn(name = "cpf_aluno"),
        inverseJoinColumns = @JoinColumn(name = "id_turma")
    )
    private List<Turma> turmas = new ArrayList<>();
    
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL)
    private List<AvaliacaoFisica> avaliacoes = new ArrayList<>();
    
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL)
    private List<PlanoTreino> planosTreino = new ArrayList<>();
    
    // Construtor
    public Aluno() {}
    
    public Aluno(String cpf, String nome, String email, String senha, String objetivo) {
        super(cpf, nome, email, senha);
        this.objetivo = objetivo;
    }
    
    // Getters e Setters
    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }
    
    public List<Turma> getTurmas() { return turmas; }
    public void setTurmas(List<Turma> turmas) { this.turmas = turmas; }
    
    public List<AvaliacaoFisica> getAvaliacoes() { return avaliacoes; }
    public void setAvaliacoes(List<AvaliacaoFisica> avaliacoes) { this.avaliacoes = avaliacoes; }
    
    public List<PlanoTreino> getPlanosTreino() { return planosTreino; }
    public void setPlanosTreino(List<PlanoTreino> planosTreino) { this.planosTreino = planosTreino; }
    
    // Métodos auxiliares
    public void matricularEmTurma(Turma turma) {
        this.turmas.add(turma);
        turma.getAlunos().add(this);
    }
    
    public void desmatricularDeTurma(Turma turma) {
        this.turmas.remove(turma);
        turma.getAlunos().remove(this);
    }
}