package com.academia.controller;

import com.academia.model.Aluno;
import com.academia.model.Instrutor;
import com.academia.model.Turma;
import com.academia.model.Exercicio;
import com.academia.service.InstrutorService;
import com.academia.service.TurmaService;
import com.academia.service.ExercicioService;
import com.academia.service.AlunoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/instrutor")
public class InstrutorController {
    
    private final InstrutorService instrutorService;
    private final TurmaService turmaService;
    private final ExercicioService exercicioService;
    private final AlunoService alunoService;
    
    public InstrutorController(InstrutorService instrutorService, TurmaService turmaService, ExercicioService exercicioService, AlunoService alunoService) {
        this.instrutorService = instrutorService;
        this.turmaService = turmaService;
        this.exercicioService = exercicioService;
        this.alunoService = alunoService;
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "instrutor/login-instrutor";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email,
                       @RequestParam String senha,
                       Model model) {
        try {
            Instrutor instrutor = instrutorService.login(email, senha);
            model.addAttribute("instrutor", instrutor);
            return "redirect:/instrutor/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "instrutor/login-instrutor";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Para demonstração, vamos usar o primeiro instrutor
        Instrutor instrutor = instrutorService.listarTodos().get(0);
        model.addAttribute("instrutor", instrutor);
        // Contagens dinâmicas
        long alunosCount = alunoService.contarAlunos();
        model.addAttribute("alunosCount", alunosCount);
        long turmasCount = turmaService.listarPorInstrutor(instrutor.getCpf()).size();
        model.addAttribute("turmasCount", turmasCount);
        long exerciciosCount = exercicioService.contarExercicios();
        model.addAttribute("exerciciosCount", exerciciosCount);
        return "instrutor/dashboard-instrutor";
    }
    
    @GetMapping("/alunos")
    public String alunos(Model model) {
        Instrutor instrutor = instrutorService.listarTodos().get(0);
        model.addAttribute("instrutor", instrutor);
        model.addAttribute("alunos", alunoService.listarTodos());
        return "instrutor/alunos-instrutor";
    }
    
    @GetMapping("/turmas")
    public String turmas(Model model) {
        Instrutor instrutor = instrutorService.listarTodos().get(0);
        model.addAttribute("instrutor", instrutor);
        model.addAttribute("turmas", turmaService.listarPorInstrutor(instrutor.getCpf()));
        return "instrutor/turmas-instrutor";
    }
    
    @PostMapping("/turmas/criar")
    public String criarTurma(@RequestParam String titulo,
                            @RequestParam String descricao,
                            @RequestParam String dataDaAula,
                            @RequestParam String horaAula,
                            @RequestParam Integer vagas) {
        Instrutor instrutor = instrutorService.listarTodos().get(0);
        Turma turma = new Turma();
        turma.setTitulo(titulo);
        turma.setDescricao(descricao);
        turma.setDataDaAula(LocalDate.parse(dataDaAula));
        turma.setHoraAula(LocalTime.parse(horaAula));
        turma.setVagas(vagas);
        turma.setInstrutor(instrutor);
        turmaService.criarTurma(turma);
        return "redirect:/instrutor/turmas";
    }
    
    @PostMapping("/turmas/editar")
    public String editarTurma(@RequestParam Long id,
                             @RequestParam String titulo,
                             @RequestParam String descricao,
                             @RequestParam String dataDaAula,
                             @RequestParam String horaAula,
                             @RequestParam Integer vagas) {
        Turma turma = new Turma();
        turma.setTitulo(titulo);
        turma.setDescricao(descricao);
        turma.setDataDaAula(LocalDate.parse(dataDaAula));
        turma.setHoraAula(LocalTime.parse(horaAula));
        turma.setVagas(vagas);
        turmaService.atualizarTurma(id, turma);
        return "redirect:/instrutor/turmas";
    }
    
    @PostMapping("/turmas/excluir/{id}")
    public String excluirTurma(@PathVariable Long id) {
        turmaService.deletarTurma(id);
        return "redirect:/instrutor/turmas";
    }
    
    @GetMapping("/exercicios")
    public String exercicios(Model model) {
        Instrutor instrutor = instrutorService.listarTodos().get(0);
        model.addAttribute("instrutor", instrutor);
        model.addAttribute("exercicios", exercicioService.listarTodos());
        return "instrutor/exercicios-instrutor";
    }
    
    @PostMapping("/exercicios/adicionar")
    public String adicionarExercicio(@RequestParam String nome,
                                    @RequestParam String equipamento,
                                    @RequestParam String grupoMuscular,
                                    @RequestParam String instrucoes,
                                    @RequestParam String descricao) {
        Exercicio exercicio = new Exercicio();
        exercicio.setNome(nome);
        exercicio.setEquipamento(equipamento);
        exercicio.setGrupoMuscular(grupoMuscular);
        exercicio.setInstrucoes(instrucoes);
        exercicio.setDescricao(descricao);
        exercicioService.cadastrarExercicio(exercicio);
        return "redirect:/instrutor/exercicios";
    }
    
    @PostMapping("/exercicios/editar")
    public String editarExercicio(@RequestParam Long id,
                                 @RequestParam String nome,
                                 @RequestParam String equipamento,
                                 @RequestParam String grupoMuscular,
                                 @RequestParam String instrucoes,
                                 @RequestParam String descricao) {
        Exercicio exercicio = new Exercicio();
        exercicio.setNome(nome);
        exercicio.setEquipamento(equipamento);
        exercicio.setGrupoMuscular(grupoMuscular);
        exercicio.setInstrucoes(instrucoes);
        exercicio.setDescricao(descricao);
        exercicioService.atualizarExercicio(id, exercicio);
        return "redirect:/instrutor/exercicios";
    }
    
    @PostMapping("/exercicios/excluir/{id}")
    public String excluirExercicio(@PathVariable Long id) {
        exercicioService.deletarExercicio(id);
        return "redirect:/instrutor/exercicios";
    }
    
    @PostMapping("/alunos/cadastrar")
    public String cadastrarAluno(@RequestParam String cpf,
                                @RequestParam String nome,
                                @RequestParam String email,
                                @RequestParam(required = false) String telefone,
                                @RequestParam String senha,
                                @RequestParam(required = false) String dataNascimento,
                                @RequestParam(required = false) String objetivo,
                                Model model) {
        try {
            Aluno aluno = new Aluno();
            aluno.setCpf(cpf);
            aluno.setNome(nome);
            aluno.setEmail(email);
            if (telefone != null && !telefone.trim().isEmpty()) {
                aluno.setTelefone(telefone);
            }
            aluno.setSenha(senha);
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                aluno.setDataNascimento(LocalDate.parse(dataNascimento));
            }
            if (objetivo != null && !objetivo.trim().isEmpty()) {
                aluno.setObjetivo(objetivo);
            }
            alunoService.cadastrarAluno(aluno);
            return "redirect:/instrutor/alunos";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao cadastrar aluno: " + e.getMessage());
            Instrutor instrutor = instrutorService.listarTodos().get(0);
            model.addAttribute("instrutor", instrutor);
            model.addAttribute("alunos", alunoService.listarTodos());
            return "instrutor/alunos-instrutor";
        }
    }
}