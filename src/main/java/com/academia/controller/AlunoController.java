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
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;

@Controller
@RequestMapping("/aluno")
public class AlunoController {
    
    private final AlunoService alunoService;
    private final AvaliacaoService avaliacaoService;
    private final PlanoTreinoService planoTreinoService;
    private final TurmaService turmaService;
    @Value("${app.upload.base-dir:${user.home}/academia/uploads}")
    private String uploadBaseDir;
    
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
        // Data de início = data de cadastro no sistema; fallback: primeira atividade
        if (aluno != null) {
            java.time.LocalDate inicioAluno = aluno.getDataCadastro();
            var planos = planoTreinoService.listarPorAluno(aluno.getCpf());
            for (var p : planos) {
                var d = p.getDataCriacao();
                if (d != null) {
                    inicioAluno = (inicioAluno == null || d.isBefore(inicioAluno)) ? d : inicioAluno;
                }
            }
            var avals = avaliacaoService.listarPorAluno(aluno.getCpf());
            for (var a : avals) {
                var d = a.getData();
                if (d != null) {
                    inicioAluno = (inicioAluno == null || d.isBefore(inicioAluno)) ? d : inicioAluno;
                }
            }
            model.addAttribute("inicioAluno", inicioAluno);
        } else {
            model.addAttribute("inicioAluno", null);
        }
        return "aluno/perfil-aluno";
    }

    // Formulário para o aluno editar o próprio perfil
    @GetMapping("/perfil/editar")
    public String editarPerfilForm(Model model, HttpSession session, RedirectAttributes ra) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            ra.addFlashAttribute("msgErro", "Faça login para editar seu perfil.");
            return "redirect:/aluno/login";
        }
        model.addAttribute("aluno", aluno);
        return "aluno/editar-perfil";
    }

    // Submit da edição do perfil do próprio aluno
    @PostMapping("/perfil/editar")
    public String editarPerfilSubmit(@RequestParam String nome,
                                     @RequestParam String email,
                                     @RequestParam(required = false) String telefone,
                                     @RequestParam(required = false) String dataNascimento,
                                     @RequestParam(required = false) String objetivo,
                                     @RequestParam(required = false) String endereco,
                                     @RequestParam(value = "file", required = false) MultipartFile file,
                                     @RequestParam(value = "removerFoto", required = false) String removerFoto,
                                     HttpSession session,
                                     RedirectAttributes ra,
                                     Model model) {
        Aluno sess = (Aluno) session.getAttribute("aluno");
        if (sess == null) {
            ra.addFlashAttribute("msgErro", "Sessão expirada. Faça login novamente.");
            return "redirect:/aluno/login";
        }
        String cpf = sess.getCpf();
        try {
            Aluno alunoAtualizado = new Aluno();
            alunoAtualizado.setNome(nome);
            alunoAtualizado.setEmail(email);
            if (telefone != null && !telefone.trim().isEmpty()) {
                String telDigits = telefone.replaceAll("[^0-9]", "");
                if (telDigits.length() != 11) {
                    ra.addFlashAttribute("msgErro", "Telefone inválido. Use 11 dígitos (DDD + número).");
                    return "redirect:/aluno/perfil/editar";
                }
                alunoAtualizado.setTelefone(telDigits);
            }
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                alunoAtualizado.setDataNascimento(LocalDate.parse(dataNascimento));
            }
            if (objetivo != null && !objetivo.trim().isEmpty()) {
                alunoAtualizado.setObjetivo(objetivo);
            }
            if (endereco != null && !endereco.trim().isEmpty()) {
                alunoAtualizado.setEndereco(endereco);
            }

            Path uploadDir = Paths.get(uploadBaseDir, "perfis");
            Files.createDirectories(uploadDir);
            boolean removeFoto = removerFoto != null && (removerFoto.equals("true") || removerFoto.equals("on"));
            if (removeFoto) {
                // Remove do banco e do disco
                String antigo = sess.getFotoPerfil();
                alunoAtualizado.setFotoPerfil(null);
                if (antigo != null && !antigo.isBlank()) {
                    try { Files.deleteIfExists(uploadDir.resolve(antigo)); } catch (Exception ignored) {}
                }
            } else if (file != null && !file.isEmpty()) {
                String ct = file.getContentType();
                long max = 2L * 1024 * 1024; // 2 MB
                boolean tipoValido = ct != null && (ct.equals("image/webp") || ct.equals("image/jpeg") || ct.equals("image/png"));
                if (!tipoValido) {
                    ra.addFlashAttribute("msgErro", "Formato de imagem inválido. Use WebP, JPEG ou PNG.");
                    return "redirect:/aluno/perfil/editar";
                }
                if (file.getSize() > max) {
                    ra.addFlashAttribute("msgErro", "Arquivo muito grande. Limite: 2 MB.");
                    return "redirect:/aluno/perfil/editar";
                }
                String cpfDigits = cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
                String original = file.getOriginalFilename();
                String ext = (original != null && original.lastIndexOf('.') != -1) ? original.substring(original.lastIndexOf('.')) : "";
                String novoNome = cpfDigits + ext.toLowerCase();
                Path destino = uploadDir.resolve(novoNome);
                Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
                alunoAtualizado.setFotoPerfil(novoNome);
                // Agendar remoção do arquivo antigo (se houver e nome diferente)
                String antigo = sess.getFotoPerfil();
                if (antigo != null && !antigo.isBlank() && !antigo.equals(novoNome)) {
                    try { Files.deleteIfExists(uploadDir.resolve(antigo)); } catch (Exception ignored) {}
                }
            } else {
                // Mantém a foto antiga se não enviou nova nem removeu
                alunoAtualizado.setFotoPerfil(sess.getFotoPerfil());
            }

            // Persistir alterações
            boolean enviouArquivo = (file != null && !file.isEmpty());
            alunoService.atualizarAluno(cpf, alunoAtualizado);
            // Atualizar objeto de sessão
            alunoService.buscarPorCpf(cpf).ifPresent(a -> session.setAttribute("aluno", a));
            if (enviouArquivo) {
                ra.addFlashAttribute("msgSucesso", "Foto de perfil atualizada com sucesso.");
            } else {
                ra.addFlashAttribute("msgSucesso", "Perfil atualizado com sucesso.");
            }
            return "redirect:/aluno/perfil";
        } catch (Exception e) {
            ra.addFlashAttribute("msgErro", "Erro ao atualizar perfil: " + e.getMessage());
            return "redirect:/aluno/perfil/editar";
        }
    }
    
    @GetMapping("/treino")
    public String treino(Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            // Sem aluno na sessão: exigir login para evitar erro na view
            return "redirect:/aluno/login";
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
            // Sem aluno na sessão: exigir login para evitar erro na view
            return "redirect:/aluno/login";
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
    public String turmas(@RequestParam(name = "q", required = false) String q, 
                         Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        model.addAttribute("aluno", aluno);

        if (aluno != null) {
            // 1. Busca todas as turmas do aluno (retorna uma lista mutável)
            java.util.List<com.academia.model.Turma> turmasTotal = turmaService.listarTurmasDoAluno(aluno.getCpf());
            
            // Criamos uma nova lista mutável para evitar erros de ordenação em listas fixas
            java.util.List<com.academia.model.Turma> turmas = new java.util.ArrayList<>(turmasTotal);

            // 2. Filtra por nome se houver uma busca (parâmetro 'q')
            if (q != null && !q.trim().isEmpty()) {
                String termoBusca = q.trim().toLowerCase();
                turmas = turmas.stream()
                        .filter(t -> t.getTitulo() != null && t.getTitulo().toLowerCase().contains(termoBusca))
                        .collect(java.util.stream.Collectors.toList());
                model.addAttribute("q", q);
            } else {
                model.addAttribute("q", "");
            }

            // 3. Ordena as turmas por data e hora
            if (!turmas.isEmpty()) {
                turmas.sort(java.util.Comparator.comparing(com.academia.model.Turma::getDataDaAula)
                                                .thenComparing(com.academia.model.Turma::getHoraAula)
                                                .reversed());
            }

            model.addAttribute("turmasMatriculadas", turmas);
        }
        return "aluno/turmas-aluno";
    }

   

    // Alias para garantir que a URL amigável use a mesma lógica
    @GetMapping("/minhas_turmas")
    public String minhasTurmas(@RequestParam(name = "q", required = false) String q, 
                               Model model, HttpSession session) {
        return turmas(q, model, session);
    }

    // Lista de turmas disponíveis do mesmo instrutor do plano ativo
    @GetMapping("/turmas/disponiveis")
    public String turmasDisponiveis(@RequestParam(name = "q", required = false) String q,
                                    Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        model.addAttribute("aluno", aluno);
        java.util.List<com.academia.model.Turma> turmas = java.util.List.of();
        java.util.Set<Long> turmasMatriculadasIds = java.util.Set.of();
        if (aluno != null) {
            turmas = turmaService.listarTodas().stream()
                // Filtrar por título se houver busca
                .filter(t -> q == null || q.trim().isEmpty() ||
                        (t.getTitulo() != null && t.getTitulo().toLowerCase().contains(q.trim().toLowerCase())))
                // Ordenar alfabeticamente por título
                .sorted(java.util.Comparator.comparing(
                        (com.academia.model.Turma t) -> t.getTitulo() == null ? "" : t.getTitulo(),
                        String.CASE_INSENSITIVE_ORDER
                ))
                .toList();
            turmasMatriculadasIds = turmaService.listarTurmasDoAluno(aluno.getCpf()).stream()
                .map(com.academia.model.Turma::getId)
                .collect(java.util.stream.Collectors.toSet());
        }
        model.addAttribute("turmasDisponiveis", turmas);
        model.addAttribute("turmasMatriculadasIds", turmasMatriculadasIds);
        model.addAttribute("q", q == null ? "" : q.trim());
        return "aluno/turmas-disponiveis";
    }

    // Detalhe de uma turma (origem: Minhas Turmas)
    @GetMapping("/turmas/{id}")
    public String detalheTurmaAluno(@PathVariable Long id, Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        if (aluno == null) {
            return "redirect:/aluno/login";
        }
        var turmaOpt = turmaService.buscarPorId(id);
        if (turmaOpt.isEmpty()) {
            return "redirect:/aluno/turmas";
        }
        var turma = turmaOpt.get();
        
        // --- ADICIONE ESTA LINHA ABAIXO ---
        java.util.Set<Long> turmasMatriculadasIds = turmaService.listarTurmasDoAluno(aluno.getCpf()).stream()
                .map(com.academia.model.Turma::getId)
                .collect(java.util.stream.Collectors.toSet());
        // ---------------------------------

        model.addAttribute("aluno", aluno);
        model.addAttribute("turma", turma);
        model.addAttribute("alunosTurma", turma.getAlunos()); 
        model.addAttribute("backUrl", "/aluno/turmas");
        
        // --- ADICIONE ESTE ATRIBUTO ---
        model.addAttribute("turmasMatriculadasIds", turmasMatriculadasIds);
        
        return "aluno/turma-detalhe";
    }

    // Detalhe de uma turma (origem: Turmas Disponíveis)
    @GetMapping("/turmas/disponiveis/{id}")
    public String detalheTurmaDisponivel(@PathVariable Long id, Model model, HttpSession session) {
        Aluno aluno = (Aluno) session.getAttribute("aluno");
        if (aluno == null) {
            aluno = alunoService.listarTodos().isEmpty() ? null : alunoService.listarTodos().get(0);
        }
        if (aluno == null) {
            return "redirect:/aluno/login";
        }
        var turmaOpt = turmaService.buscarPorId(id);
        if (turmaOpt.isEmpty()) {
            return "redirect:/aluno/turmas/disponiveis";
        }
        var turma = turmaOpt.get();

        // --- ADICIONE ESTA LINHA ---
        java.util.Set<Long> turmasMatriculadasIds = turmaService.listarTurmasDoAluno(aluno.getCpf()).stream()
                .map(com.academia.model.Turma::getId)
                .collect(java.util.stream.Collectors.toSet());

        model.addAttribute("aluno", aluno);
        model.addAttribute("turma", turma);
        model.addAttribute("alunosTurma", turma.getAlunos());
        model.addAttribute("backUrl", "/aluno/turmas/disponiveis");
        
        // --- ADICIONE ESTE ATRIBUTO ---
        model.addAttribute("turmasMatriculadasIds", turmasMatriculadasIds);

        return "aluno/turma-detalhe";
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


    @GetMapping("/minhas_avaliacoes")
    public String minhasAvaliacoes(Model model, HttpSession session) {
        return avaliacoes(model, session);
    }

    @GetMapping("/meu_perfil")
    public String meuPerfil(Model model, HttpSession session) {
        return perfil(model, session);
    }
}