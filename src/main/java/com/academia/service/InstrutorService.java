package com.academia.service;

import com.academia.model.Instrutor;
import com.academia.repository.InstrutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstrutorService {
    
    private final InstrutorRepository instrutorRepository;
    
    public InstrutorService(InstrutorRepository instrutorRepository) {
        this.instrutorRepository = instrutorRepository;
    }
    
    // Cadastrar novo instrutor
    public Instrutor cadastrarInstrutor(Instrutor instrutor) {
        // Verificar se email já existe
        if (instrutorRepository.existsByEmail(instrutor.getEmail())) {
            throw new RuntimeException("Email já cadastrado!");
        }
        return instrutorRepository.save(instrutor);
    }
    
    // Buscar instrutor por CPF
    public Optional<Instrutor> buscarPorCpf(String cpf) {
        return instrutorRepository.findById(cpf);
    }
    
    // Buscar instrutor por email
    public Optional<Instrutor> buscarPorEmail(String email) {
        return instrutorRepository.findByEmail(email);
    }
    
    // Listar todos os instrutores
    public List<Instrutor> listarTodos() {
        return instrutorRepository.findAll();
    }

    // Listar todos os instrutores, exceto o CPF informado
    public List<Instrutor> listarTodosMenos(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return listarTodos();
        }
        return instrutorRepository.findAllByCpfNot(cpf);
    }
    
    // Atualizar instrutor
    public Instrutor atualizarInstrutor(String cpf, Instrutor instrutorAtualizado) {
        return instrutorRepository.findById(cpf)
            .map(instrutor -> {
                instrutor.setNome(instrutorAtualizado.getNome());
                instrutor.setEmail(instrutorAtualizado.getEmail());
                instrutor.setTelefone(instrutorAtualizado.getTelefone());
                instrutor.setDiaQueComecouTrabalhar(instrutorAtualizado.getDiaQueComecouTrabalhar());
                if (instrutorAtualizado.getDataCadastro() != null) {
                    instrutor.setDataCadastro(instrutorAtualizado.getDataCadastro());
                }
                if (instrutorAtualizado.getDataNascimento() != null) {
                    instrutor.setDataNascimento(instrutorAtualizado.getDataNascimento());
                }
                    // Corrigido: sempre atualizar o campo fotoPerfil (permitindo null para remoção)
                    instrutor.setFotoPerfil(instrutorAtualizado.getFotoPerfil());
                instrutor.setAdmin(instrutorAtualizado.isAdmin());
                return instrutorRepository.save(instrutor);
            })
            .orElseThrow(() -> new RuntimeException("Instrutor não encontrado!"));
    }
    
    // Deletar instrutor
    public void deletarInstrutor(String cpf) {
        if (instrutorRepository.existsById(cpf)) {
            instrutorRepository.deleteById(cpf);
        } else {
            throw new RuntimeException("Instrutor não encontrado!");
        }
    }
    
    // Login do instrutor
    public Instrutor login(String email, String senha) {
        return instrutorRepository.findByEmailAndSenha(email, senha)
            .orElseThrow(() -> new RuntimeException("Credenciais inválidas!"));
    }
}