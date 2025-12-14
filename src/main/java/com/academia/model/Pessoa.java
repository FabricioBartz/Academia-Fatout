package com.academia.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "pessoa")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pessoa {
    @Id
    @Column(name = "cpf", length = 11)
    private String cpf;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private String telefone;
    private String senha;
    
    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;
    
    private String endereco;
    private String usuario;
    
    // Construtor
    public Pessoa() {}
    
    public Pessoa(String cpf, String nome, String email, String senha) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
    
    // Getters e Setters
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}