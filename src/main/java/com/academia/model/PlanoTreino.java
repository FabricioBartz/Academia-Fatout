package com.academia.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "plano_de_treino")
public class PlanoTreino {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plano")
    private Long id;
    
    @Column(name = "data_criacao")
    private LocalDate dataCriacao;
    
    private LocalTime tempo;
    
    private String nome;
    
    @Column(length = 500)
    private String observacoes;

    // Dias da semana em que o plano deve ser executado, separados por vírgula (SEG,TER,QUA,QUI,SEX,SAB,DOM)
    @Column(name = "dias_semana", length = 50)
    private String diasSemana;
    
    @ManyToOne
    @JoinColumn(name = "cpf_aluno")
    private Aluno aluno;
    
    @ManyToOne
    @JoinColumn(name = "cpf_instrutor")
    private Instrutor instrutor;
    
    @OneToMany(mappedBy = "planoTreino", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExercicioPlano> exercicios = new ArrayList<>();
    
    // Construtor
    public PlanoTreino() {
        this.dataCriacao = LocalDate.now();
    }
    
    public PlanoTreino(Aluno aluno, Instrutor instrutor, String nome) {
        this();
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.nome = nome;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    
    public LocalTime getTempo() { return tempo; }
    public void setTempo(LocalTime tempo) { this.tempo = tempo; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getDiasSemana() { return diasSemana; }
    public void setDiasSemana(String diasSemana) { this.diasSemana = diasSemana; }
    
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
    
    public Instrutor getInstrutor() { return instrutor; }
    public void setInstrutor(Instrutor instrutor) { this.instrutor = instrutor; }
    
    public List<ExercicioPlano> getExercicios() { return exercicios; }
    public void setExercicios(List<ExercicioPlano> exercicios) { this.exercicios = exercicios; }
    
    // Método para adicionar exercício
    public void adicionarExercicio(Exercicio exercicio, Integer series, Integer repeticoes, Double carga) {
        ExercicioPlano exercicioPlano = new ExercicioPlano(this, exercicio, series, repeticoes, carga);
        this.exercicios.add(exercicioPlano);
    }
}