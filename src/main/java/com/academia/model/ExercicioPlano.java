package com.academia.model;

import jakarta.persistence.*;
import java.time.Duration;

@Entity
@Table(name = "exercicio_plano")
public class ExercicioPlano {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_plano")
    private PlanoTreino planoTreino;
    
    @ManyToOne
    @JoinColumn(name = "id_exercicio")
    private Exercicio exercicio;
    
    private Integer series;
    private Integer repeticoes;
    
    @Column
    private Double carga;
    
    private Duration descanso;
    
    @Column(length = 500)
    private String observacoes;
    
    // Construtor
    public ExercicioPlano() {}
    
    public ExercicioPlano(PlanoTreino planoTreino, Exercicio exercicio, Integer series, Integer repeticoes, Double carga) {
        this.planoTreino = planoTreino;
        this.exercicio = exercicio;
        this.series = series;
        this.repeticoes = repeticoes;
        this.carga = carga;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public PlanoTreino getPlanoTreino() { return planoTreino; }
    public void setPlanoTreino(PlanoTreino planoTreino) { this.planoTreino = planoTreino; }
    
    public Exercicio getExercicio() { return exercicio; }
    public void setExercicio(Exercicio exercicio) { this.exercicio = exercicio; }
    
    public Integer getSeries() { return series; }
    public void setSeries(Integer series) { this.series = series; }
    
    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
    
    public Double getCarga() { return carga; }
    public void setCarga(Double carga) { this.carga = carga; }
    
    public Duration getDescanso() { return descanso; }
    public void setDescanso(Duration descanso) { this.descanso = descanso; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}