package com.academia.controller;

import com.academia.model.Aluno;
import com.academia.model.Instrutor;
import com.academia.model.Turma;
import com.academia.model.Exercicio;
import com.academia.model.PlanoTreino;
import com.academia.model.ExercicioPlano;
import com.academia.model.AvaliacaoFisica;
import com.academia.service.InstrutorService;
import com.academia.service.TurmaService;
import com.academia.service.ExercicioService;
import com.academia.service.AlunoService;
import com.academia.service.AvaliacaoService;
import com.academia.service.PlanoTreinoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/instrutor")
public class InstrutorController {
    
    private final InstrutorService instrutorService;
    private final TurmaService turmaService;
    private final ExercicioService exercicioService;
    private final AlunoService alunoService;
    private final AvaliacaoService avaliacaoService;
    private final PlanoTreinoService planoTreinoService;
    
    public InstrutorController(InstrutorService instrutorService, TurmaService turmaService, ExercicioService exercicioService, AlunoService alunoService, AvaliacaoService avaliacaoService, PlanoTreinoService planoTreinoService) {
        this.instrutorService = instrutorService;
        this.turmaService = turmaService;
        this.exercicioService = exercicioService;
        this.alunoService = alunoService;
        this.avaliacaoService = avaliacaoService;
        this.planoTreinoService = planoTreinoService;
    }
    
    @GetMapping("/login")
    public String loginPage() {
        return "instrutor/login-instrutor";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        Model model,
                        HttpSession session) {
        try {
            Instrutor instrutor = instrutorService.login(email, senha);
            // guarda instrutor na sessão para uso nas próximas requisições
            session.setAttribute("instrutor", instrutor);
            return "redirect:/instrutor/dashboard";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "instrutor/login-instrutor";
        }
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        // Contagens dinâmicas
        long alunosCount = alunoService.contarAlunos();
        model.addAttribute("alunosCount", alunosCount);
        long turmasCount = turmaService.listarPorInstrutor(instrutor == null ? "" : instrutor.getCpf()).size();
        model.addAttribute("turmasCount", turmasCount);
        long exerciciosCount = exercicioService.contarExercicios();
        model.addAttribute("exerciciosCount", exerciciosCount);
        return "instrutor/dashboard-instrutor";
    }
    
    @GetMapping("/alunos")
    public String alunos(@RequestParam(name = "q", required = false) String q, Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        if (q != null && !q.trim().isEmpty()) {
            model.addAttribute("alunos", alunoService.buscarPorNome(q.trim()));
            model.addAttribute("q", q.trim());
        } else {
            model.addAttribute("alunos", alunoService.listarTodos());
            model.addAttribute("q", "");
        }
        return "instrutor/alunos-instrutor";
    }
    
    @GetMapping("/turmas")
    public String turmas(@RequestParam(name = "q", required = false) String q,
                         Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        java.util.List<Turma> turmas;
        if (q != null && !q.trim().isEmpty()) {
            turmas = turmaService.buscarPorTitulo(q.trim());
            model.addAttribute("q", q.trim());
        } else {
            turmas = turmaService.listarPorInstrutor(instrutor == null ? "" : instrutor.getCpf());
            model.addAttribute("q", "");
        }
        model.addAttribute("turmas", turmas);
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
    public String exercicios(@RequestParam(name = "q", required = false) String q,
                             Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        java.util.List<Exercicio> exercicios;
        if (q != null && !q.trim().isEmpty()) {
            exercicios = exercicioService.buscarPorNome(q.trim());
            model.addAttribute("q", q.trim());
        } else {
            exercicios = exercicioService.listarTodos();
            model.addAttribute("q", "");
        }
        model.addAttribute("exercicios", exercicios);
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
    
    @PostMapping("/alunos/editar")
    public String editarAluno(@RequestParam String cpf,
                             @RequestParam String nome,
                             @RequestParam String email,
                             @RequestParam(required = false) String telefone,
                             @RequestParam(required = false) String dataNascimento,
                             @RequestParam(required = false) String objetivo,
                             Model model) {
        try {
            Aluno alunoAtualizado = new Aluno();
            alunoAtualizado.setNome(nome);
            alunoAtualizado.setEmail(email);
            if (telefone != null && !telefone.trim().isEmpty()) {
                alunoAtualizado.setTelefone(telefone);
            }
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                alunoAtualizado.setDataNascimento(LocalDate.parse(dataNascimento));
            }
            if (objetivo != null && !objetivo.trim().isEmpty()) {
                alunoAtualizado.setObjetivo(objetivo);
            }
            alunoService.atualizarAluno(cpf, alunoAtualizado);
            return "redirect:/instrutor/alunos";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao atualizar aluno: " + e.getMessage());
            Instrutor instrutor = instrutorService.listarTodos().get(0);
            model.addAttribute("instrutor", instrutor);
            model.addAttribute("alunos", alunoService.listarTodos());
            return "instrutor/alunos-instrutor";
        }
    }
    
    @PostMapping("/alunos/excluir/{cpf}")
    public String excluirAluno(@PathVariable String cpf) {
        alunoService.deletarAluno(cpf);
        return "redirect:/instrutor/alunos";
    }
    
    @GetMapping("/alunos/perfil/{cpf}")
    public String perfilAluno(@PathVariable String cpf, Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        
        Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
        if (aluno == null) {
            return "redirect:/instrutor/alunos";
        }
        
        model.addAttribute("aluno", aluno);
        // Histórico de planos de treino
        var planos = planoTreinoService.listarHistorico(aluno.getCpf());
        model.addAttribute("planos", planos);
        // Carregar últimas 5 avaliações (ordenadas por data desc)
        var avaliacoes = avaliacaoService.listarUltimas5PorAluno(aluno.getCpf());
        model.addAttribute("avaliacoes", avaliacoes);
        return "instrutor/perfil-aluno";
    }
    
    @GetMapping("/alunos/avaliacao/{cpf}")
    public String formularioAvaliacao(@PathVariable String cpf, Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        
        Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
        if (aluno == null) {
            return "redirect:/instrutor/alunos";
        }
        
        model.addAttribute("aluno", aluno);
        return "instrutor/avaliacao-aluno";
    }

    // Formulário para criar plano de treino
    @GetMapping("/alunos/plano/{cpf}")
    public String formularioPlano(@PathVariable String cpf, Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);

        Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
        if (aluno == null) {
            return "redirect:/instrutor/alunos";
        }
        model.addAttribute("aluno", aluno);
        model.addAttribute("exercicios", exercicioService.listarTodos());
        model.addAttribute("planosInstrutor", planoTreinoService.listarPorInstrutor(instrutor.getCpf()));
        return "instrutor/criar-plano";
    }

    // Salvar plano de treino
    @PostMapping("/alunos/plano/{cpf}")
    public String salvarPlano(@PathVariable String cpf,
                              @RequestParam String nome,
                              @RequestParam(required = false) String observacoes,
                              @RequestParam(name = "diasSemana", required = false) java.util.List<String> diasSemana,
                              @RequestParam(name = "copiarPlanoId", required = false) Long copiarPlanoId,
                              @RequestParam(name = "exercicioId") java.util.List<Long> exercicioIds,
                              @RequestParam(name = "series") java.util.List<Integer> seriesList,
                              @RequestParam(name = "repeticoes") java.util.List<Integer> repeticoesList,
                              @RequestParam(name = "carga", required = false) java.util.List<Double> cargaList,
                              Model model,
                              HttpSession session) {
        try {
            Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
            if (instrutor == null) {
                instrutor = instrutorService.listarTodos().get(0);
            }

            Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
            if (aluno == null) {
                return "redirect:/instrutor/alunos";
            }

            PlanoTreino plano = new PlanoTreino();
            plano.setAluno(aluno);
            plano.setInstrutor(instrutor);
            plano.setNome(nome);
            plano.setObservacoes(observacoes);
            if (diasSemana != null && !diasSemana.isEmpty()) {
                plano.setDiasSemana(String.join(",", diasSemana));
            }
            if (copiarPlanoId != null) {
                var origemOpt = planoTreinoService.buscarPorId(copiarPlanoId);
                if (origemOpt.isPresent()) {
                    var origem = origemOpt.get();
                    if (plano.getDiasSemana() == null || plano.getDiasSemana().isEmpty()) {
                        plano.setDiasSemana(origem.getDiasSemana());
                    }
                    java.util.List<ExercicioPlano> itens = new java.util.ArrayList<>();
                    for (ExercicioPlano epOrig : origem.getExercicios()) {
                        ExercicioPlano ep = new ExercicioPlano();
                        ep.setPlanoTreino(plano);
                        ep.setExercicio(epOrig.getExercicio());
                        ep.setSeries(epOrig.getSeries());
                        ep.setRepeticoes(epOrig.getRepeticoes());
                        ep.setCarga(epOrig.getCarga());
                        itens.add(ep);
                    }
                    plano.setExercicios(itens);
                }
            } else {
                // Validar ao menos um exercício
                if (exercicioIds == null || exercicioIds.isEmpty()) {
                    model.addAttribute("error", "Adicione pelo menos um exercício ou selecione um plano para copiar.");
                    model.addAttribute("instrutor", instrutor);
                    model.addAttribute("aluno", aluno);
                    model.addAttribute("exercicios", exercicioService.listarTodos());
                    model.addAttribute("planosInstrutor", planoTreinoService.listarPorInstrutor(instrutor.getCpf()));
                    return "instrutor/criar-plano";
                }

                // Monta exercícios
                java.util.List<ExercicioPlano> itens = new java.util.ArrayList<>();
                for (int i = 0; i < exercicioIds.size(); i++) {
                    var exercicio = exercicioService.buscarPorId(exercicioIds.get(i)).orElse(null);
                    if (exercicio == null) continue;
                    Integer series = i < seriesList.size() ? seriesList.get(i) : null;
                    Integer repeticoes = i < repeticoesList.size() ? repeticoesList.get(i) : null;
                    Double carga = (cargaList != null && i < cargaList.size()) ? cargaList.get(i) : null;
                    ExercicioPlano ep = new ExercicioPlano();
                    ep.setPlanoTreino(plano);
                    ep.setExercicio(exercicio);
                    ep.setSeries(series);
                    ep.setRepeticoes(repeticoes);
                    ep.setCarga(carga);
                    itens.add(ep);
                }
                plano.setExercicios(itens);
            }

            planoTreinoService.criarPlanoTreino(plano);

            return "redirect:/instrutor/alunos/perfil/" + cpf;
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao salvar plano: " + e.getMessage());
            Instrutor instrutor = instrutorService.listarTodos().get(0);
            model.addAttribute("instrutor", instrutor);
            Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
            model.addAttribute("aluno", aluno);
            model.addAttribute("exercicios", exercicioService.listarTodos());
            model.addAttribute("planosInstrutor", planoTreinoService.listarPorInstrutor(instrutor.getCpf()));
            return "instrutor/criar-plano";
        }
    }

    // Detalhe do plano de treino do aluno (instrutor)
    @GetMapping("/alunos/planos/{cpf}/{id}")
    public String detalhePlanoInstrutor(@PathVariable String cpf,
                                        @PathVariable Long id,
                                        Model model,
                                        HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);

        var aluno = alunoService.buscarPorCpf(cpf).orElse(null);
        if (aluno == null) return "redirect:/instrutor/alunos";
        model.addAttribute("aluno", aluno);

        var plano = planoTreinoService.buscarPorId(id).orElse(null);
        if (plano == null || plano.getAluno() == null || !cpf.equals(plano.getAluno().getCpf())) {
            return "redirect:/instrutor/alunos/perfil/" + cpf;
        }
        model.addAttribute("plano", plano);
        return "instrutor/plano-detalhe";
    }

    @GetMapping("/alunos/avaliacoes/{cpf}/{id}")
    public String visualizarAvaliacao(@PathVariable String cpf,
                                      @PathVariable Long id,
                                      Model model,
                                      HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);

        Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
        if (aluno == null) {
            return "redirect:/instrutor/alunos";
        }

        AvaliacaoFisica avaliacao = avaliacaoService.buscarPorId(id).orElse(null);
        if (avaliacao == null || avaliacao.getAluno() == null || !cpf.equals(avaliacao.getAluno().getCpf())) {
            // se não pertence ao aluno ou não existe, volta para perfil
            return "redirect:/instrutor/alunos/perfil/" + cpf;
        }

        model.addAttribute("aluno", aluno);
        model.addAttribute("avaliacao", avaliacao);
        return "instrutor/avaliacao-detalhe";
    }
    
    @PostMapping("/alunos/avaliacao/{cpf}")
    public String salvarAvaliacao(@PathVariable String cpf,
                                 @RequestParam Double peso,
                                 @RequestParam Double altura,
                                 @RequestParam Double peito,
                                 @RequestParam Double cintura,
                                 @RequestParam Double quadril,
                                 @RequestParam Double bicepsEsquerdo,
                                 @RequestParam Double bicepsDireito,
                                 @RequestParam Double coxaEsquerda,
                                 @RequestParam Double coxaDireita,
                                 @RequestParam(required = false) Double panturrilhaEsquerda,
                                 @RequestParam(required = false) Double panturrilhaDireita,
                                 @RequestParam(required = false) String observacoes,
                                 Model model,
                                 HttpSession session) {
        try {
            Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
            if (instrutor == null) {
                instrutor = instrutorService.listarTodos().get(0);
            }
            
            Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
            if (aluno == null) {
                return "redirect:/instrutor/alunos";
            }
            
            AvaliacaoFisica avaliacao = new AvaliacaoFisica();
            avaliacao.setAluno(aluno);
            avaliacao.setInstrutor(instrutor);
            avaliacao.setData(LocalDate.now());
            avaliacao.setPeso(peso);
            avaliacao.setAltura(altura);
            avaliacao.setPeito(peito);
            avaliacao.setCintura(cintura);
            avaliacao.setQuadril(quadril);
            avaliacao.setBicepsEsquerdo(bicepsEsquerdo);
            avaliacao.setBicepsDireito(bicepsDireito);
            avaliacao.setCoxaEsquerda(coxaEsquerda);
            avaliacao.setCoxaDireita(coxaDireita);
            if (panturrilhaEsquerda != null) {
                avaliacao.setPanturrilhaEsquerda(panturrilhaEsquerda);
            }
            if (panturrilhaDireita != null) {
                avaliacao.setPanturrilhaDireita(panturrilhaDireita);
            }
            avaliacao.setObservacoes(observacoes);
            
            avaliacaoService.criarAvaliacao(avaliacao);
            
            return "redirect:/instrutor/alunos/perfil/" + cpf;
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao salvar avaliação: " + e.getMessage());
            Instrutor instrutor = instrutorService.listarTodos().get(0);
            model.addAttribute("instrutor", instrutor);
            Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
            model.addAttribute("aluno", aluno);
            return "instrutor/avaliacao-aluno";
        }
    }
    
    @GetMapping("/alunos/editar/{cpf}")
    public String formularioEditarAluno(@PathVariable String cpf, Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        
        Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
        if (aluno == null) {
            return "redirect:/instrutor/alunos";
        }
        
        model.addAttribute("aluno", aluno);
        return "instrutor/editar-aluno";
    }
    
    @PostMapping("/alunos/editar/{cpf}")
    public String salvarEdicaoAluno(@PathVariable String cpf,
                                   @RequestParam String nome,
                                   @RequestParam String email,
                                   @RequestParam(required = false) String telefone,
                                   @RequestParam(required = false) String dataNascimento,
                                   @RequestParam(required = false) String objetivo,
                                   Model model) {
        try {
            Aluno alunoAtualizado = new Aluno();
            alunoAtualizado.setNome(nome);
            alunoAtualizado.setEmail(email);
            if (telefone != null && !telefone.trim().isEmpty()) {
                alunoAtualizado.setTelefone(telefone);
            }
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                alunoAtualizado.setDataNascimento(LocalDate.parse(dataNascimento));
            }
            if (objetivo != null && !objetivo.trim().isEmpty()) {
                alunoAtualizado.setObjetivo(objetivo);
            }
            alunoService.atualizarAluno(cpf, alunoAtualizado);
            return "redirect:/instrutor/alunos/perfil/" + cpf;
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao atualizar aluno: " + e.getMessage());
            Instrutor instrutor = instrutorService.listarTodos().get(0);
            model.addAttribute("instrutor", instrutor);
            Aluno aluno = alunoService.buscarPorCpf(cpf).orElse(null);
            model.addAttribute("aluno", aluno);
            return "instrutor/editar-aluno";
        }
    }
}