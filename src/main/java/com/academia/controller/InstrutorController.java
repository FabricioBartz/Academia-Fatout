package com.academia.controller;

import com.academia.model.Instrutor;
import com.academia.service.InstrutorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/instrutor")
public class InstrutorController {
    
    private final InstrutorService instrutorService;
    
    public InstrutorController(InstrutorService instrutorService) {
        this.instrutorService = instrutorService;
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
        return "instrutor/turmas-instrutor";
    }
    
    @GetMapping("/exercicios")
    public String exercicios(Model model) {
        Instrutor instrutor = instrutorService.listarTodos().get(0);
        model.addAttribute("instrutor", instrutor);
        return "instrutor/exercicios-instrutor";
    }
}