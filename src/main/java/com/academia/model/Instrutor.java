package com.academia.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "instrutor")
@PrimaryKeyJoinColumn(name = "cpf")
public class Instrutor extends Pessoa {
    
    @Column(name = "dia_que_comecou_trabalhar")
    private LocalDate diaQueComecouTrabalhar;

    @Column(name = "is_admin")
    private boolean isAdmin = false;
    
    @OneToMany(mappedBy = "instrutor", cascade = CascadeType.ALL)
    private List<Turma> turmas = new ArrayList<>();
    
    @OneToMany(mappedBy = "instrutor", cascade = CascadeType.ALL)
    private List<PlanoTreino> planosCriados = new ArrayList<>();
    
    // Construtor
    public Instrutor() {}
    
    public Instrutor(String cpf, String nome, String email, String senha) {
        super(cpf, nome, email, senha);
    }
    
    // Getters e Setters
    public LocalDate getDiaQueComecouTrabalhar() { return diaQueComecouTrabalhar; }
    public void setDiaQueComecouTrabalhar(LocalDate diaQueComecouTrabalhar) { 
        this.diaQueComecouTrabalhar = diaQueComecouTrabalhar; 
    }
    
    public List<Turma> getTurmas() { return turmas; }
    public void setTurmas(List<Turma> turmas) { this.turmas = turmas; }
    
    public List<PlanoTreino> getPlanosCriados() { return planosCriados; }
    public void setPlanosCriados(List<PlanoTreino> planosCriados) { this.planosCriados = planosCriados; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { this.isAdmin = admin; }
    
    // Método para cadastrar aluno (simulação)
    public void cadastrarAluno(Aluno aluno) {
        System.out.println("Aluno " + aluno.getNome() + " cadastrado pelo instrutor " + this.getNome());
    }
}