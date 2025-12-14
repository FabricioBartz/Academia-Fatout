package com.academia.controller;

import com.academia.model.Aluno;
import com.academia.service.AlunoService;
import com.academia.service.AvaliacaoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/aluno")
public class AlunoController {
    
    private final AlunoService alunoService;
    private final AvaliacaoService avaliacaoService;
    
    public AlunoController(AlunoService alunoService, AvaliacaoService avaliacaoService) {
        this.alunoService = alunoService;
        this.avaliacaoService = avaliacaoService;
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "aluno/login-aluno";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email, 
                        @RequestParam String senha,
                        Model model,
                        HttpSession session) {
        try {
            Aluno aluno = alunoService.login(email, senha);
            session.setAttribute("aluno", aluno);
            return "redirect:/aluno/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "aluno/login-aluno";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        model.addAttribute("aluno", aluno);
        
        // Carregar última avaliação
        if (aluno != null) {
            var ultimaAvaliacao = avaliacaoService.buscarUltimaAvaliacao(aluno.getCpf());
            model.addAttribute("ultimaAvaliacao", ultimaAvaliacao.orElse(null));
        }
        
        return "aluno/dashboard-aluno";
    }
    
    @GetMapping("/perfil")
    public String perfil(Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        model.addAttribute("aluno", aluno);
        return "aluno/perfil-aluno";
    }
    
    @GetMapping("/treino")
    public String treino(Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        model.addAttribute("aluno", aluno);
        return "aluno/treino-aluno";
    }
    
    @GetMapping("/turmas")
    public String turmas(Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        model.addAttribute("aluno", aluno);
        return "aluno/turmas-aluno";
    }
    
    @GetMapping("/avaliacoes")
    public String avaliacoes(Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        if (aluno == null) {
            return "redirect:/aluno/login";
        }
        model.addAttribute("aluno", aluno);
        model.addAttribute("avaliacoes", avaliacaoService.listarPorAluno(aluno.getCpf()));
        return "aluno/avaliacoes-aluno";
    }
}