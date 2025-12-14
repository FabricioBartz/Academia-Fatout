package com.academia.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Exercicio")
public class Exercicio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_exercicio")
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    private String equipamento;
    
    @Column(name = "grupo_muscular")
    private String grupoMuscular;
    
    @Column(length = 1000)
    private String instrucoes;
    
    @Column(length = 1000)
    private String descricao;
    
    @OneToMany(mappedBy = "exercicio", cascade = CascadeType.ALL)
    private List<ExercicioPlano> planosAssociados = new ArrayList<>();
    
    // Construtor
    public Exercicio() {}
    
    public Exercicio(String nome, String grupoMuscular) {
        this.nome = nome;
        this.grupoMuscular = grupoMuscular;
    }
    
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEquipamento() { return equipamento; }
    public void setEquipamento(String equipamento) { this.equipamento = equipamento; }
    
    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }
    
    public String getInstrucoes() { return instrucoes; }
    public void setInstrucoes(String instrucoes) { this.instrucoes = instrucoes; }
    
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    
    public List<ExercicioPlano> getPlanosAssociados() { return planosAssociados; }
    public void setPlanosAssociados(List<ExercicioPlano> planosAssociados) { this.planosAssociados = planosAssociados; }
}