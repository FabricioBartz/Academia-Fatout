package com.academia.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "AvaliacaoFisica")
public class AvaliacaoFisica {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avaliacao")
    private Long id;
    
    private LocalDate data;
    
    @Column
    private Double altura;
    
    @Column
    private Double peso;
    
    @Column(name = "imc")
    private Double imc;
    
    // Medidas corporais em cm
    @Column
    private Double peito;
    
    @Column
    private Double cintura;
    
    @Column
    private Double quadril;
    
    @Column
    private Double bicepsDireito;
    
    @Column
    private Double bicepsEsquerdo;
    
    @Column
    private Double coxaDireita;
    
    @Column
    private Double coxaEsquerda;
    
    @Column
    private Double panturrilhaDireita;
    
    @Column
    private Double panturrilhaEsquerda;
    
    @Column(length = 500)
    private String observacoes;
    
    @ManyToOne
    @JoinColumn(name = "cpf_aluno")
    private Aluno aluno;
    
    @ManyToOne
    @JoinColumn(name = "cpf_instrutor")
    private Instrutor instrutor;
    
    // Construtor
    public AvaliacaoFisica() {}
    
    public AvaliacaoFisica(Aluno aluno, Instrutor instrutor, LocalDate data) {
        this.aluno = aluno;
        this.instrutor = instrutor;
        this.data = data;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    
    public Double getAltura() { return altura; }
    public void setAltura(Double altura) { this.altura = altura; }
    
    public Double getPeso() { return peso; }
    public void setPeso(Double peso) { this.peso = peso; }
    
    public Double getImc() { 
        if (altura != null && peso != null && altura > 0) {
            return peso / (altura * altura);
        }
        return imc; 
    }
    public void setImc(Double imc) { this.imc = imc; }
    
    public Double getPeito() { return peito; }
    public void setPeito(Double peito) { this.peito = peito; }
    
    public Double getCintura() { return cintura; }
    public void setCintura(Double cintura) { this.cintura = cintura; }
    
    public Double getQuadril() { return quadril; }
    public void setQuadril(Double quadril) { this.quadril = quadril; }
    
    public Double getBicepsDireito() { return bicepsDireito; }
    public void setBicepsDireito(Double bicepsDireito) { this.bicepsDireito = bicepsDireito; }
    
    public Double getBicepsEsquerdo() { return bicepsEsquerdo; }
    public void setBicepsEsquerdo(Double bicepsEsquerdo) { this.bicepsEsquerdo = bicepsEsquerdo; }
    
    public Double getCoxaDireita() { return coxaDireita; }
    public void setCoxaDireita(Double coxaDireita) { this.coxaDireita = coxaDireita; }
    
    public Double getCoxaEsquerda() { return coxaEsquerda; }
    public void setCoxaEsquerda(Double coxaEsquerda) { this.coxaEsquerda = coxaEsquerda; }
    
    public Double getPanturrilhaDireita() { return panturrilhaDireita; }
    public void setPanturrilhaDireita(Double panturrilhaDireita) { this.panturrilhaDireita = panturrilhaDireita; }
    
    public Double getPanturrilhaEsquerda() { return panturrilhaEsquerda; }
    public void setPanturrilhaEsquerda(Double panturrilhaEsquerda) { this.panturrilhaEsquerda = panturrilhaEsquerda; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
    
    public Instrutor getInstrutor() { return instrutor; }
    public void setInstrutor(Instrutor instrutor) { this.instrutor = instrutor; }
}