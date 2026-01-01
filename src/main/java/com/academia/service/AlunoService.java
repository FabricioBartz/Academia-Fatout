package com.academia.service;

import com.academia.model.Aluno;
import com.academia.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {
    
    private final AlunoRepository alunoRepository;
    
    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }
    
    // Cadastrar novo aluno
    public Aluno cadastrarAluno(Aluno aluno) {
        // Verificar se email já existe
        if (alunoRepository.existsByEmail(aluno.getEmail())) {
            throw new RuntimeException("Email já cadastrado!");
        }
        // Definir data de cadastro no sistema, se não estiver preenchida
        if (aluno.getDataCadastro() == null) {
            aluno.setDataCadastro(java.time.LocalDate.now());
        }
        return alunoRepository.save(aluno);
    }
    
    // Buscar aluno por CPF
    public Optional<Aluno> buscarPorCpf(String cpf) {
        return alunoRepository.findById(cpf);
    }
    
    // Buscar aluno por email
    public Optional<Aluno> buscarPorEmail(String email) {
        return alunoRepository.findByEmail(email);
    }
    
    // Listar todos os alunos
    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }
    
    // Listar alunos por nome (pesquisa)
    public List<Aluno> buscarPorNome(String nome) {
        return alunoRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    // Atualizar aluno
    public Aluno atualizarAluno(String cpf, Aluno alunoAtualizado) {
        return alunoRepository.findById(cpf)
            .map(aluno -> {
                aluno.setNome(alunoAtualizado.getNome());
                aluno.setEmail(alunoAtualizado.getEmail());
                aluno.setTelefone(alunoAtualizado.getTelefone());
                aluno.setDataNascimento(alunoAtualizado.getDataNascimento());
                if (alunoAtualizado.getDataCadastro() != null) {
                    aluno.setDataCadastro(alunoAtualizado.getDataCadastro());
                }
                aluno.setEndereco(alunoAtualizado.getEndereco());
                aluno.setObjetivo(alunoAtualizado.getObjetivo());
                    // Corrigido: sempre atualizar o campo fotoPerfil (permitindo null para remoção)
                    aluno.setFotoPerfil(alunoAtualizado.getFotoPerfil());
                return alunoRepository.save(aluno);
            })
            .orElseThrow(() -> new RuntimeException("Aluno não encontrado!"));
    }
    
    // Deletar aluno
    public void deletarAluno(String cpf) {
        if (alunoRepository.existsById(cpf)) {
            alunoRepository.deleteById(cpf);
        } else {
            throw new RuntimeException("Aluno não encontrado!");
        }
    }
    
    // Login do aluno
    public Aluno login(String email, String senha) {
        return alunoRepository.findByEmailAndSenha(email, senha)
            .orElseThrow(() -> new RuntimeException("Credenciais inválidas!"));
    }
    
    // Contar total de alunos
    public long contarAlunos() {
        return alunoRepository.count();
    }
}