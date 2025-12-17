package com.academia.controller;

import com.academia.model.Aluno;
import com.academia.service.AlunoService;
import com.academia.service.AvaliacaoService;
import com.academia.model.PlanoTreino;
import com.academia.service.PlanoTreinoService;
import com.academia.service.TurmaService;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Arrays;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/aluno")
public class AlunoController {
    
    private final AlunoService alunoService;
    private final AvaliacaoService avaliacaoService;
    private final PlanoTreinoService planoTreinoService;
    private final TurmaService turmaService;
    
    public AlunoController(AlunoService alunoService, AvaliacaoService avaliacaoService, PlanoTreinoService planoTreinoService, TurmaService turmaService) {
        this.alunoService = alunoService;
        this.avaliacaoService = avaliacaoService;
        this.planoTreinoService = planoTreinoService;
        this.turmaService = turmaService;
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
            var planoAtivo = planoTreinoService.buscarPlanoAtivo(aluno.getCpf());
            String diaHoje = codigoDia(LocalDate.now().getDayOfWeek());
            var planosAluno = planoTreinoService.listarPorAluno(aluno.getCpf());
            PlanoTreino planoDoDia = null;
            for (PlanoTreino p : planosAluno) {
                String dias = p.getDiasSemana();
                if (dias != null && Arrays.stream(dias.toUpperCase().split(",")).map(String::trim).anyMatch(d -> d.equals(diaHoje))) {
                    planoDoDia = p;
                    break;
                }
            }
            if (planoDoDia == null) {
                planoDoDia = planoAtivo.orElse(null);
            }
            model.addAttribute("planoHoje", planoDoDia);
            boolean ativoHoje = planoDoDia != null && planoDoDia.getDiasSemana() != null &&
                Arrays.stream(planoDoDia.getDiasSemana().toUpperCase().split(",")).map(String::trim).anyMatch(d -> d.equals(diaHoje));
            model.addAttribute("diaHoje", diaHoje);
            model.addAttribute("dataHoje", LocalDate.now());
            model.addAttribute("dataHojeStr", java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDate.now()));
            model.addAttribute("planoHojeAtivoHoje", ativoHoje);
        }
        
        return "aluno/dashboard-aluno";
    }
    // Alias amigável para Painel Principal
    @GetMapping("/painel_principal")
    public String painelPrincipalAluno(Model model, HttpSession session) {
        return dashboard(model, session);
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
        if (aluno != null) {
            var planoAtivo = planoTreinoService.buscarPlanoAtivo(aluno.getCpf());
            model.addAttribute("plano", planoAtivo.orElse(null));
            model.addAttribute("planos", planoTreinoService.listarPorAluno(aluno.getCpf()));
        }
        return "aluno/treino-aluno";
    }

    // Detalhe do plano para aluno
    @GetMapping("/treino/{id}")
    public String detalhePlanoAluno(@PathVariable Long id, Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        model.addAttribute("aluno", aluno);
        var plano = planoTreinoService.buscarPorId(id).orElse(null);
        if (plano == null || plano.getAluno() == null || (aluno != null && !aluno.getCpf().equals(plano.getAluno().getCpf()))) {
            return "redirect:/aluno/treino";
        }
        model.addAttribute("plano", plano);
        return "aluno/plano-detalhe";
    }
    
    private String codigoDia(DayOfWeek dow) {
        return switch (dow) {
            case MONDAY -> "SEG";
            case TUESDAY -> "TER";
            case WEDNESDAY -> "QUA";
            case THURSDAY -> "QUI";
            case FRIDAY -> "SEX";
            case SATURDAY -> "SAB";
            case SUNDAY -> "DOM";
        };
    }
    
    @GetMapping("/turmas")
    public String turmas(Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        model.addAttribute("aluno", aluno);
        if (aluno != null) {
            // Turmas matriculadas
            var turmasMatriculadas = turmaService.listarTurmasDoAluno(aluno.getCpf());
            model.addAttribute("turmasMatriculadas", turmasMatriculadas);
        }
        return "aluno/turmas-aluno";
    }

    // Lista todas as turmas com vagas disponíveis (independente do instrutor)
    @GetMapping("/turmas/disponiveis")
    public String turmasDisponiveis(Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        model.addAttribute("aluno", aluno);
        var turmas = turmaService.listarComVagasDisponiveis();
        model.addAttribute("turmasDisponiveis", turmas);
        return "aluno/turmas-disponiveis";
    }

    // Matricular aluno em turma
    @PostMapping("/turmas/matricular/{id}")
    public String matricularEmTurma(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        try {
            if (aluno != null) {
                turmaService.matricularAluno(id, aluno.getCpf());
                ra.addFlashAttribute("msgSucesso", "Matrícula realizada com sucesso!");
            } else {
                ra.addFlashAttribute("msgErro", "Aluno não encontrado na sessão.");
            }
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/aluno/turmas";
    }

    // Desmatricular aluno de turma
    @PostMapping("/turmas/desmatricular/{id}")
    public String desmatricularDaTurma(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        try {
            if (aluno != null) {
                turmaService.desmatricularAluno(id, aluno.getCpf());
                ra.addFlashAttribute("msgSucesso", "Desmatrícula realizada com sucesso!");
            } else {
                ra.addFlashAttribute("msgErro", "Aluno não encontrado na sessão.");
            }
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/aluno/turmas";
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

    // Aliases amigáveis para URLs do aluno
    @GetMapping("/meus_treinos")
    public String meusTreinos(Model model, HttpSession session) {
        return treino(model, session);
    }

    @GetMapping("/minhas_turmas")
    public String minhasTurmas(Model model, HttpSession session) {
        return turmas(model, session);
    }

    @GetMapping("/minhas_avaliacoes")
    public String minhasAvaliacoes(Model model, HttpSession session) {
        return avaliacoes(model, session);
    }

    @GetMapping("/meu_perfil")
    public String meuPerfil(Model model, HttpSession session) {
        return perfil(model, session);
    }
}