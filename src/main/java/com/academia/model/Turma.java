package com.academia.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "turma")
public class Turma {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turma")
    private Long id;
    
    private String titulo;
    private String descricao;
    
    @Column(name = "data_da_aula")
    private LocalDate dataDaAula;
    
    @Column(name = "hora_aula")
    private LocalTime horaAula;
    
    private Integer vagas;
    
    @ManyToOne
    @JoinColumn(name = "cpf_instrutor")
    private Instrutor instrutor;
    
    @ManyToMany(mappedBy = "turmas")
    private List<Aluno> alunos = new ArrayList<>();
    
    // Construtor
    public Turma() {}
    
    public Turma(String titulo, String descricao, LocalDate dataDaAula, Integer vagas, Instrutor instrutor) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataDaAula = dataDaAula;
        this.vagas = vagas;
        this.instrutor = instrutor;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public LocalDate getDataDaAula() { return dataDaAula; }
    public void setDataDaAula(LocalDate dataDaAula) { this.dataDaAula = dataDaAula; }
    
    public LocalTime getHoraAula() { return horaAula; }
    public void setHoraAula(LocalTime horaAula) { this.horaAula = horaAula; }
    
    public Integer getVagas() { return vagas; }
    public void setVagas(Integer vagas) { this.vagas = vagas; }
    
    public Instrutor getInstrutor() { return instrutor; }
    public void setInstrutor(Instrutor instrutor) { this.instrutor = instrutor; }
    
    public List<Aluno> getAlunos() { return alunos; }
    public void setAlunos(List<Aluno> alunos) { this.alunos = alunos; }
    
    // Métodos auxiliares
    public Integer getVagasDisponiveis() {
        return vagas - alunos.size();
    }
    
    public boolean isLotada() {
        return alunos.size() >= vagas;
    }
}