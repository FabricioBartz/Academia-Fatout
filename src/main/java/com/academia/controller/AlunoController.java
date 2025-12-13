package com.academia.controller;

import com.academia.model.Aluno;
import com.academia.service.AlunoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/aluno")
public class AlunoController {
    
    private final AlunoService alunoService;
    
    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "aluno/login-aluno";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email, 
                       @RequestParam String senha,
                       Model model) {
        try {
            Aluno aluno = alunoService.login(email, senha);
            model.addAttribute("aluno", aluno);
            return "redirect:/aluno/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "aluno/login-aluno";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Para demonstração, vamos usar o primeiro aluno
        Aluno aluno = alunoService.listarTodos().get(0);
        model.addAttribute("aluno", aluno);
        return "aluno/dashboard-aluno";
    }
    
    @GetMapping("/perfil")
    public String perfil(Model model) {
        Aluno aluno = alunoService.listarTodos().get(0);
        model.addAttribute("aluno", aluno);
        return "aluno/perfil-aluno";
    }
    
    @GetMapping("/treino")
    public String treino(Model model) {
        Aluno aluno = alunoService.listarTodos().get(0);
        model.addAttribute("aluno", aluno);
        return "aluno/treino-aluno";
    }
    
    @GetMapping("/turmas")
    public String turmas(Model model) {
        Aluno aluno = alunoService.listarTodos().get(0);
        model.addAttribute("aluno", aluno);
        return "aluno/turmas-aluno";
    }
    
    @GetMapping("/avaliacoes")
    public String avaliacoes(Model model) {
        Aluno aluno = alunoService.listarTodos().get(0);
        model.addAttribute("aluno", aluno);
        return "aluno/avaliacoes-aluno";
    }
}