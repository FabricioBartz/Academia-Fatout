package com.academia.controller;

import com.academia.model.Instrutor;
import com.academia.model.Turma;
import com.academia.model.Exercicio;
import com.academia.service.InstrutorService;
import com.academia.service.TurmaService;
import com.academia.service.ExercicioService;
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
    
    public InstrutorController(InstrutorService instrutorService, TurmaService turmaService, ExercicioService exercicioService) {
        this.instrutorService = instrutorService;
        this.turmaService = turmaService;
        this.exercicioService = exercicioService;
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
        return "instrutor/dashboard-instrutor";
    }
    
    @GetMapping("/alunos")
    public String alunos(Model model) {
        Instrutor instrutor = instrutorService.listarTodos().get(0);
        model.addAttribute("instrutor", instrutor);
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
        turmaService.salvar(turma);
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
        exercicioService.salvar(exercicio);
        return "redirect:/instrutor/exercicios";
    }
}