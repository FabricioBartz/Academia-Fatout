
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;

@Controller
@RequestMapping("/instrutor")
public class InstrutorController {

        @GetMapping("/editar-perfil")
        public String editarPerfilInstrutor(Model model, HttpSession session) {
            Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
            if (instrutor == null) {
                instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
            }
            model.addAttribute("instrutor", instrutor);
            return "instrutor/editar-perfil";
        }

        @PostMapping("/editar-perfil")
        public String salvarPerfilInstrutor(@RequestParam String cpf,
                                            @RequestParam String nome,
                                            @RequestParam String email,
                                            @RequestParam(required = false) String telefone,
                                            @RequestParam(required = false) String dataNascimento,
                                            @RequestParam(required = false) String dataCadastro,
                                            @RequestParam(required = false) Boolean admin,
                                            @RequestParam(value = "file", required = false) MultipartFile file,
                                            @RequestParam(value = "removerFoto", required = false) boolean removerFoto,
                                            HttpSession session, 
                                            RedirectAttributes ra) {
            try {
                // 1. Limpa o CPF (Igual no Aluno)
                String cpfLimpo = cpf.replaceAll("\\D", "");

                // 2. Busca o instrutor atual (A FONTE DA VERDADE)
                Instrutor instrutorExistente = instrutorService.buscarPorCpf(cpfLimpo)
                        .orElseThrow(() -> new Exception("Instrutor não encontrado"));

                // 3. Cria um NOVO objeto para a atualização (Método idêntico ao Aluno)
                Instrutor instrutorAtualizado = new Instrutor();
                instrutorAtualizado.setCpf(cpfLimpo);
                instrutorAtualizado.setNome(nome);
                instrutorAtualizado.setEmail(email);
                
                // Limpa telefone se enviado
                if (telefone != null && !telefone.trim().isEmpty()) {
                    instrutorAtualizado.setTelefone(telefone.replaceAll("\\D", ""));
                } else {
                    instrutorAtualizado.setTelefone(instrutorExistente.getTelefone());
                }

                // Converte datas com segurança
                if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                    instrutorAtualizado.setDataNascimento(java.time.LocalDate.parse(dataNascimento));
                } else {
                    instrutorAtualizado.setDataNascimento(instrutorExistente.getDataNascimento());
                }
                
                if (dataCadastro != null && !dataCadastro.trim().isEmpty()) {
                    instrutorAtualizado.setDataCadastro(java.time.LocalDate.parse(dataCadastro));
                } else {
                    instrutorAtualizado.setDataCadastro(instrutorExistente.getDataCadastro());
                }

                // 4. LÓGICA DA FOTO 
                if (removerFoto) {
                    instrutorAtualizado.setFotoPerfil(null);
                } else if (file != null && !file.isEmpty()) {
                    // Se enviou arquivo, faz o upload
                    String uploadDir = "src/main/resources/static/uploads/perfis/"; 
                    Path uploadPath = Paths.get(uploadDir);
                    if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                    String extensao = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                    String novoNome = cpfLimpo + extensao;
                    Files.copy(file.getInputStream(), uploadPath.resolve(novoNome), StandardCopyOption.REPLACE_EXISTING);
                    
                    instrutorAtualizado.setFotoPerfil(novoNome);
                } else {
                    // SE O CAMPO DE ARQUIVO ESTIVER VAZIO: 
                    // Atribuímos a foto que já existia no banco para não perder o dado
                    instrutorAtualizado.setFotoPerfil(instrutorExistente.getFotoPerfil());
                }

                // 5. PRESERVAÇÃO DE DADOS CRÍTICOS
                instrutorAtualizado.setSenha(instrutorExistente.getSenha());
                instrutorAtualizado.setAdmin(admin != null ? admin : instrutorExistente.isAdmin());

                // 6. SALVAR (Igual ao seu instrutorService.atualizarInstrutor)
                instrutorService.atualizarInstrutor(cpfLimpo, instrutorAtualizado);
                
                // Atualiza a sessão para a Navbar mudar na hora
                session.setAttribute("instrutor", instrutorAtualizado);
                
                ra.addFlashAttribute("msgSucesso", "Perfil atualizado com sucesso!");
                
            } catch (Exception e) {
                e.printStackTrace();
                ra.addFlashAttribute("msgErro", "Erro ao atualizar: " + e.getMessage());
                return "redirect:/instrutor/editar-perfil";
            }
            return "redirect:/instrutor/meu_perfil";
        }
    
    private final InstrutorService instrutorService;
    private final TurmaService turmaService;
    private final ExercicioService exercicioService;
    private final AlunoService alunoService;
    private final AvaliacaoService avaliacaoService;
    private final PlanoTreinoService planoTreinoService;
    @Value("${app.upload.base-dir:${user.home}/academia/uploads}")
    private String uploadBaseDir;
    
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
    // Alias amigável para Painel Principal
    @GetMapping("/painel_principal")
    public String painelPrincipal(Model model, HttpSession session) {
        return dashboard(model, session);
    }
    
    @GetMapping("/alunos")
    public String alunos(@RequestParam(name = "q", required = false) String q, Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        java.util.List<Aluno> alunos;
        if (q != null && !q.trim().isEmpty()) {
            alunos = alunoService.buscarPorNome(q.trim());
            model.addAttribute("q", q.trim());
        } else {
            alunos = alunoService.listarTodos();
            model.addAttribute("q", "");
        }
        // Mapa de início: usar exclusivamente a data editável do aluno (data de início na academia)
        java.util.Map<String, java.time.LocalDate> inicioPorAluno = new java.util.HashMap<>();
        for (Aluno a : alunos) {
            inicioPorAluno.put(a.getCpf(), a.getDataCadastro());
        }
        model.addAttribute("alunos", alunos);
        model.addAttribute("inicioPorAluno", inicioPorAluno);
        return "instrutor/alunos-instrutor";
    }
    // Alias para URL amigável
    @GetMapping("/meus_alunos")
    public String meusAlunos(@RequestParam(name = "q", required = false) String q, Model model, HttpSession session) {
        return alunos(q, model, session);
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

        // --- ADICIONE ESTA LÓGICA DE ORDENAÇÃO AQUI ---
        if (turmas != null) {
            turmas.sort(java.util.Comparator.comparing(Turma::getDataDaAula)
                                .thenComparing(Turma::getHoraAula)
                                .reversed());
        }
        // ----------------------------------------------

        model.addAttribute("turmas", turmas);
        return "instrutor/turmas-instrutor";
    }
    // Alias para URL amigável
    @GetMapping("/minhas_turmas")
    public String minhasTurmas(@RequestParam(name = "q", required = false) String q, Model model, HttpSession session) {
        return turmas(q, model, session);
    }
    @PostMapping("/turmas/criar")
    public String criarTurma(@RequestParam String titulo,
                            @RequestParam String descricao,
                            @RequestParam String dataDaAula,
                            @RequestParam String horaAula,
                            @RequestParam Integer vagas,
                            RedirectAttributes ra) {
        try {
            Instrutor instrutor = instrutorService.listarTodos().get(0);
            Turma turma = new Turma();
            turma.setTitulo(titulo);
            turma.setDescricao(descricao);
            turma.setDataDaAula(LocalDate.parse(dataDaAula));
            turma.setHoraAula(LocalTime.parse(horaAula));
            turma.setVagas(vagas);
            turma.setInstrutor(instrutor);
            turmaService.criarTurma(turma);
            ra.addFlashAttribute("msgSucesso", "Turma criada com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/turmas";
    }
    
    @PostMapping("/turmas/editar")
    public String editarTurma(@RequestParam Long id,
                             @RequestParam String titulo,
                             @RequestParam String descricao,
                             @RequestParam String dataDaAula,
                             @RequestParam String horaAula,
                             @RequestParam Integer vagas,
                             RedirectAttributes ra) {
        try {
            Turma turma = new Turma();
            turma.setTitulo(titulo);
            turma.setDescricao(descricao);
            turma.setDataDaAula(LocalDate.parse(dataDaAula));
            turma.setHoraAula(LocalTime.parse(horaAula));
            turma.setVagas(vagas);
            turmaService.atualizarTurma(id, turma);
            ra.addFlashAttribute("msgSucesso", "Turma atualizada com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/turmas";
    }
    
    @PostMapping("/turmas/excluir/{id}")
    public String excluirTurma(@PathVariable Long id, RedirectAttributes ra) {
        try {
            turmaService.deletarTurma(id);
            ra.addFlashAttribute("msgSucesso", "Turma excluída com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/turmas";
    }

    // Detalhe de uma turma com alunos matriculados
    @GetMapping("/turmas/{id}")
    public String detalheTurma(@PathVariable Long id, Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        var turma = turmaService.buscarPorId(id).orElse(null);
        if (turma == null || turma.getInstrutor() == null || (instrutor != null && !instrutor.getCpf().equals(turma.getInstrutor().getCpf()))) {
            return "redirect:/instrutor/turmas";
        }
        model.addAttribute("turma", turma);
        model.addAttribute("alunos", turma.getAlunos());
        return "instrutor/turma-detalhe";
    }

    // Remover aluno de uma turma (instrutor)
    @PostMapping("/turmas/{id}/remover-aluno/{cpfAluno}")
    public String removerAlunoDaTurma(@PathVariable Long id, @PathVariable String cpfAluno, RedirectAttributes ra, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        var turma = turmaService.buscarPorId(id).orElse(null);
        if (turma == null || turma.getInstrutor() == null || (instrutor != null && !instrutor.getCpf().equals(turma.getInstrutor().getCpf()))) {
            return "redirect:/instrutor/turmas";
        }
        try {
            turmaService.desmatricularAluno(id, cpfAluno);
            ra.addFlashAttribute("msgSucesso", "Aluno removido da turma.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/turmas/" + id;
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
                                    @RequestParam String descricao,
                                    RedirectAttributes ra) {
        try {
            Exercicio exercicio = new Exercicio();
            exercicio.setNome(nome);
            exercicio.setEquipamento(equipamento);
            exercicio.setGrupoMuscular(grupoMuscular);
            exercicio.setInstrucoes(instrucoes);
            exercicio.setDescricao(descricao);
            exercicioService.cadastrarExercicio(exercicio);
            ra.addFlashAttribute("msgSucesso", "Exercício adicionado com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/exercicios";
    }
    
    @PostMapping("/exercicios/editar")
    public String editarExercicio(@RequestParam Long id,
                                 @RequestParam String nome,
                                 @RequestParam String equipamento,
                                 @RequestParam String grupoMuscular,
                                 @RequestParam String instrucoes,
                                 @RequestParam String descricao,
                                 RedirectAttributes ra) {
        try {
            Exercicio exercicio = new Exercicio();
            exercicio.setNome(nome);
            exercicio.setEquipamento(equipamento);
            exercicio.setGrupoMuscular(grupoMuscular);
            exercicio.setInstrucoes(instrucoes);
            exercicio.setDescricao(descricao);
            exercicioService.atualizarExercicio(id, exercicio);
            ra.addFlashAttribute("msgSucesso", "Exercício atualizado com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/exercicios";
    }
    
    @PostMapping("/exercicios/excluir/{id}")
    public String excluirExercicio(@PathVariable Long id, RedirectAttributes ra) {
        try {
            exercicioService.deletarExercicio(id);
            ra.addFlashAttribute("msgSucesso", "Exercício excluído com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
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
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                Model model,
                                RedirectAttributes ra) {
        try {
            // Validação básica de CPF antes de persistir
            String cpfDigits = cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
            if (cpfDigits.length() != 11) {
                ra.addFlashAttribute("msgErro", "CPF inválido. Informe 11 dígitos.");
                return "redirect:/instrutor/alunos";
            }

            Aluno aluno = new Aluno();
            aluno.setCpf(cpfDigits);
            aluno.setNome(nome);
            aluno.setEmail(email);
            if (telefone != null && !telefone.trim().isEmpty()) {
                String telDigits = telefone.replaceAll("[^0-9]", "");
                if (telDigits.length() != 11) {
                    ra.addFlashAttribute("msgErro", "Telefone inválido. Use 11 dígitos (DDD + número).");
                    return "redirect:/instrutor/alunos";
                }
                aluno.setTelefone(telDigits);
            }
            aluno.setSenha(senha);
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                aluno.setDataNascimento(LocalDate.parse(dataNascimento));
            }
            if (objetivo != null && !objetivo.trim().isEmpty()) {
                aluno.setObjetivo(objetivo);
            }
            // Upload da foto de perfil, se enviado
            if (file != null && !file.isEmpty()) {
                // Valida tipo e tamanho
                String ct = file.getContentType();
                long max = 2L * 1024 * 1024; // 2 MB
                boolean tipoValido = ct != null && (ct.equals("image/webp") || ct.equals("image/jpeg") || ct.equals("image/png"));
                if (!tipoValido) {
                    ra.addFlashAttribute("msgErro", "Formato de imagem inválido. Use WebP, JPEG ou PNG.");
                    return "redirect:/instrutor/alunos";
                }
                if (file.getSize() > max) {
                    ra.addFlashAttribute("msgErro", "Arquivo muito grande. Limite: 2 MB.");
                    return "redirect:/instrutor/alunos";
                }
                // Garante criação das pastas externas: uploads/perfis
                Path uploadDir = Paths.get(uploadBaseDir, "perfis");
                Files.createDirectories(uploadDir);
                // Extrai extensão original
                String original = file.getOriginalFilename();
                String ext = (original != null && original.lastIndexOf('.') != -1) ? original.substring(original.lastIndexOf('.')) : "";
                String novoNome = cpfDigits + ext.toLowerCase();
                Path destino = uploadDir.resolve(novoNome);
                Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
                aluno.setFotoPerfil(novoNome);
            }
            alunoService.cadastrarAluno(aluno);
            ra.addFlashAttribute("msgSucesso", "Aluno cadastrado com sucesso.");
            return "redirect:/instrutor/alunos";
        } catch (Exception e) {
            // Em caso de erro inesperado, usar flash e redirecionar para evitar resposta comprometida
            ra.addFlashAttribute("msgErro", "Erro ao cadastrar aluno: " + e.getMessage());
            return "redirect:/instrutor/alunos";
        }
    }

    // Evita WARN de GET não suportado: redireciona para a listagem
    @GetMapping("/alunos/cadastrar")
    public String getCadastrarAluno() {
        return "redirect:/instrutor/alunos";
    }
    
    @PostMapping("/alunos/editar")
    public String editarAluno(@RequestParam String cpf,
                             @RequestParam String nome,
                             @RequestParam String email,
                             @RequestParam(required = false) String telefone,
                             @RequestParam(required = false) String dataNascimento,
                             @RequestParam(required = false) String dataInicio,
                             @RequestParam(required = false) String objetivo,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             Model model,
                             RedirectAttributes ra) {
        try {
            Aluno alunoAtualizado = new Aluno();
            alunoAtualizado.setNome(nome);
            alunoAtualizado.setEmail(email);
            if (telefone != null && !telefone.trim().isEmpty()) {
                String telDigits = telefone.replaceAll("[^0-9]", "");
                if (telDigits.length() != 11) {
                    ra.addFlashAttribute("msgErro", "Telefone inválido. Use 11 dígitos (DDD + número).");
                    return "redirect:/instrutor/alunos";
                }
                alunoAtualizado.setTelefone(telDigits);
            }
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                alunoAtualizado.setDataNascimento(LocalDate.parse(dataNascimento));
            }
            if (objetivo != null && !objetivo.trim().isEmpty()) {
                alunoAtualizado.setObjetivo(objetivo);
            }
            if (dataInicio != null && !dataInicio.trim().isEmpty()) {
                alunoAtualizado.setDataCadastro(java.time.LocalDate.parse(dataInicio));
            }
            // Upload da foto de perfil, se enviado
            if (file != null && !file.isEmpty()) {
                String ct = file.getContentType();
                long max = 2L * 1024 * 1024; // 2 MB
                boolean tipoValido = ct != null && (ct.equals("image/webp") || ct.equals("image/jpeg") || ct.equals("image/png"));
                if (!tipoValido) {
                    ra.addFlashAttribute("msgErro", "Formato de imagem inválido. Use WebP, JPEG ou PNG.");
                    return "redirect:/instrutor/alunos";
                }
                if (file.getSize() > max) {
                    ra.addFlashAttribute("msgErro", "Arquivo muito grande. Limite: 2 MB.");
                    return "redirect:/instrutor/alunos";
                }
                String cpfDigits = cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
                Path uploadDir = Paths.get(uploadBaseDir, "perfis");
                Files.createDirectories(uploadDir);
                String original = file.getOriginalFilename();
                String ext = (original != null && original.lastIndexOf('.') != -1) ? original.substring(original.lastIndexOf('.')) : "";
                String novoNome = cpfDigits + ext.toLowerCase();
                Path destino = uploadDir.resolve(novoNome);
                Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
                // Remover foto antiga se nome diferente
                try {
                    var atualOpt = alunoService.buscarPorCpf(cpf);
                    String antigo = atualOpt.map(Aluno::getFotoPerfil).orElse(null);
                    if (antigo != null && !antigo.isBlank() && !antigo.equals(novoNome)) {
                        Files.deleteIfExists(uploadDir.resolve(antigo));
                    }
                } catch (Exception ignored) {}
                alunoAtualizado.setFotoPerfil(novoNome);
            }
            alunoService.atualizarAluno(cpf, alunoAtualizado);
            ra.addFlashAttribute("msgSucesso", "Aluno atualizado com sucesso.");
            return "redirect:/instrutor/alunos";
        } catch (Exception e) {
            ra.addFlashAttribute("msgErro", "Erro ao atualizar aluno: " + e.getMessage());
            return "redirect:/instrutor/alunos";
        }
    }
    
    @PostMapping("/alunos/excluir/{cpf}")
    public String excluirAluno(@PathVariable String cpf, RedirectAttributes ra) {
        try {
            alunoService.deletarAluno(cpf);
            ra.addFlashAttribute("msgSucesso", "Aluno excluído com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/alunos";
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

    // JSON: detalhes de plano para auto-preenchimento ao copiar
    @GetMapping("/alunos/plano/json/{id}")
    @ResponseBody
    public java.util.Map<String, Object> planoJson(@PathVariable Long id) {
        var opt = planoTreinoService.buscarPorId(id);
        var resp = new java.util.HashMap<String, Object>();
        if (opt.isEmpty()) {
            resp.put("ok", false);
            resp.put("error", "Plano não encontrado");
            return resp;
        }
        var p = opt.get();
        resp.put("ok", true);
        resp.put("nome", p.getNome());
        resp.put("diasSemana", p.getDiasSemana());
        resp.put("observacoes", p.getObservacoes());
        var exs = new java.util.ArrayList<java.util.Map<String, Object>>();
        if (p.getExercicios() != null) {
            for (var ep : p.getExercicios()) {
                var m = new java.util.HashMap<String, Object>();
                m.put("exercicioId", ep.getExercicio().getId());
                m.put("series", ep.getSeries());
                m.put("repeticoes", ep.getRepeticoes());
                m.put("carga", ep.getCarga());
                exs.add(m);
            }
        }
        resp.put("exercicios", exs);
        return resp;
    }

    // Salvar plano de treino
    @PostMapping("/alunos/plano/{cpf}")
    public String salvarPlano(@PathVariable String cpf,
                              @RequestParam String nome,
                              @RequestParam(required = false) String observacoes,
                              @RequestParam(name = "diasSemana", required = false) java.util.List<String> diasSemana,
                              @RequestParam(name = "copiarPlanoId", required = false) Long copiarPlanoId,
                              @RequestParam(name = "dataTermino", required = false) String dataTermino,
                              @RequestParam(name = "exercicioId") java.util.List<Long> exercicioIds,
                              @RequestParam(name = "series") java.util.List<Integer> seriesList,
                              @RequestParam(name = "repeticoes") java.util.List<Integer> repeticoesList,
                              @RequestParam(name = "carga", required = false) java.util.List<Double> cargaList,
                              Model model,
                              HttpSession session,
                              RedirectAttributes ra) {
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
            if (dataTermino != null && !dataTermino.isEmpty()) {
                plano.setDataTermino(java.time.LocalDate.parse(dataTermino));
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
            ra.addFlashAttribute("msgSucesso", "Plano criado com sucesso.");
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

    // Listar todos os planos do aluno (página dedicada)
    @GetMapping("/alunos/planos/{cpf}")
    public String listarPlanosAluno(@PathVariable String cpf,
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

        // 1. Busca a lista original
        var planos = planoTreinoService.listarPorAluno(cpf);

        // 2. ADICIONE ESTA LÓGICA DE ORDENAÇÃO AQUI:
        if (planos != null) {
            // Compara p2 com p1 para ordem decrescente (mais recente primeiro)
            planos.sort((p1, p2) -> p2.getDataCriacao().compareTo(p1.getDataCriacao()));
        }

        model.addAttribute("planos", planos);
        return "instrutor/planos-aluno";
    }

    // Form de edição completa do plano com exercícios
    @GetMapping("/alunos/planos/{cpf}/{id}/editar")
    public String editarPlanoInstrutor(@PathVariable String cpf,
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
        model.addAttribute("exercicios", exercicioService.listarTodos());
        return "instrutor/editar-plano";
    }

    @PostMapping("/alunos/planos/{cpf}/{id}/editar")
    public String salvarEdicaoPlanoInstrutor(@PathVariable String cpf,
                                             @PathVariable Long id,
                                             @RequestParam String nome,
                                             @RequestParam(required = false) String observacoes,
                                             @RequestParam(name = "diasSemana", required = false) java.util.List<String> diasSemana,
                                             @RequestParam(name = "dataTermino", required = false) String dataTermino,
                                             @RequestParam(name = "exercicioId") java.util.List<Long> exercicioIds,
                                             @RequestParam(name = "series") java.util.List<Integer> seriesList,
                                             @RequestParam(name = "repeticoes") java.util.List<Integer> repeticoesList,
                                             @RequestParam(name = "carga", required = false) java.util.List<Double> cargaList,
                                             Model model,
                                             HttpSession session,
                                             RedirectAttributes ra) {
        try {
            var planoOpt = planoTreinoService.buscarPorId(id);
            if (planoOpt.isEmpty()) {
                return "redirect:/instrutor/alunos/planos/" + cpf;
            }
            var plano = planoOpt.get();
            plano.setNome(nome);
            plano.setObservacoes(observacoes);
            if (diasSemana != null && !diasSemana.isEmpty()) {
                plano.setDiasSemana(String.join(",", diasSemana));
            } else {
                plano.setDiasSemana("");
            }

            if (dataTermino != null && !dataTermino.isEmpty()) {
                plano.setDataTermino(java.time.LocalDate.parse(dataTermino));
            } else {
                plano.setDataTermino(null);
            }

            // Validar ao menos um exercício
            if (exercicioIds == null || exercicioIds.isEmpty()) {
                model.addAttribute("error", "Adicione pelo menos um exercício.");
                return editarPlanoInstrutor(cpf, id, model, session);
            }
            // Recriar lista de exercícios sem substituir a coleção gerenciada
            if (plano.getExercicios() != null) {
                plano.getExercicios().clear();
            }
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
                plano.getExercicios().add(ep);
            }
            // Persistir: save com ID existente fará update
            planoTreinoService.criarPlanoTreino(plano);
            ra.addFlashAttribute("msgSucesso", "Plano atualizado com sucesso.");
            // Voltar para a lista de planos do aluno (página anterior)
            return "redirect:/instrutor/alunos/planos/" + cpf;
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao salvar edição do plano: " + e.getMessage());
            return editarPlanoInstrutor(cpf, id, model, session);
        }
    }

    @PostMapping("/alunos/planos/{cpf}/{id}/excluir")
    public String excluirPlanoInstrutor(@PathVariable String cpf,
                                        @PathVariable Long id,
                                        HttpSession session,
                                        RedirectAttributes ra) {
        var planoOpt = planoTreinoService.buscarPorId(id);
        if (planoOpt.isPresent()) {
            var plano = planoOpt.get();
            if (plano.getAluno() != null && cpf.equals(plano.getAluno().getCpf())) {
                planoTreinoService.excluirPlanoTreino(id);
                ra.addFlashAttribute("msgSucesso", "Plano excluído com sucesso.");
            }
        }
        return "redirect:/instrutor/alunos/planos/" + cpf;
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
    
    // Formulário de edição de avaliação
    @GetMapping("/alunos/avaliacoes/{cpf}/{id}/editar")
    public String editarAvaliacaoForm(@PathVariable String cpf,
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
        model.addAttribute("aluno", aluno);

        AvaliacaoFisica avaliacao = avaliacaoService.buscarPorId(id).orElse(null);
        if (avaliacao == null || avaliacao.getAluno() == null || !cpf.equals(avaliacao.getAluno().getCpf())) {
            return "redirect:/instrutor/alunos/perfil/" + cpf;
        }
        model.addAttribute("avaliacao", avaliacao);
        return "instrutor/avaliacao-aluno";
    }

    // Salvar edição da avaliação
    @PostMapping("/alunos/avaliacoes/{cpf}/{id}/editar")
    public String salvarEdicaoAvaliacao(@PathVariable String cpf,
                                        @PathVariable Long id,
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
                                        RedirectAttributes ra) {
        try {
            AvaliacaoFisica atualizada = new AvaliacaoFisica();
            atualizada.setPeso(peso);
            atualizada.setAltura(altura);
            atualizada.setPeito(peito);
            atualizada.setCintura(cintura);
            atualizada.setQuadril(quadril);
            atualizada.setBicepsEsquerdo(bicepsEsquerdo);
            atualizada.setBicepsDireito(bicepsDireito);
            atualizada.setCoxaEsquerda(coxaEsquerda);
            atualizada.setCoxaDireita(coxaDireita);
            if (panturrilhaEsquerda != null) atualizada.setPanturrilhaEsquerda(panturrilhaEsquerda);
            if (panturrilhaDireita != null) atualizada.setPanturrilhaDireita(panturrilhaDireita);
            atualizada.setObservacoes(observacoes);

            avaliacaoService.atualizarAvaliacao(id, atualizada);
            ra.addFlashAttribute("msgSucesso", "Avaliação atualizada com sucesso.");
            return "redirect:/instrutor/alunos/avaliacoes/" + cpf + "/" + id;
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
            return "redirect:/instrutor/alunos/avaliacoes/" + cpf + "/" + id + "/editar";
        }
    }

    // Excluir avaliação
    @PostMapping("/alunos/avaliacoes/{cpf}/{id}/excluir")
    public String excluirAvaliacao(@PathVariable String cpf,
                                   @PathVariable Long id,
                                   RedirectAttributes ra) {
        try {
            avaliacaoService.deletarAvaliacao(id);
            ra.addFlashAttribute("msgSucesso", "Avaliação excluída com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/alunos/perfil/" + cpf;
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
                                 HttpSession session,
                                 RedirectAttributes ra) {
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
            ra.addFlashAttribute("msgSucesso", "Avaliação salva com sucesso.");
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
                                @RequestParam(required = false) String dataInicio,
                                @RequestParam(required = false) String objetivo,
                                @RequestParam(value = "file", required = false) MultipartFile file,
                                @RequestParam(value = "removerFoto", required = false) boolean removerFoto, // NOVO PARÂMETRO
                                Model model,
                                RedirectAttributes ra) {
        try {
            // 1. Busca o aluno atual no banco para ter a "fonte da verdade"
            Aluno alunoExistente = alunoService.buscarPorCpf(cpf)
                    .orElseThrow(() -> new Exception("Aluno não encontrado"));

            Aluno alunoAtualizado = new Aluno();
            alunoAtualizado.setCpf(cpf.replaceAll("[^0-9]", ""));
            alunoAtualizado.setNome(nome);
            alunoAtualizado.setEmail(email);
            alunoAtualizado.setSenha(alunoExistente.getSenha()); // Preserva a senha

            if (telefone != null && !telefone.trim().isEmpty()) {
                alunoAtualizado.setTelefone(telefone.replaceAll("[^0-9]", ""));
            }
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                alunoAtualizado.setDataNascimento(LocalDate.parse(dataNascimento));
            }
            if (objetivo != null && !objetivo.trim().isEmpty()) {
                alunoAtualizado.setObjetivo(objetivo);
            }
            if (dataInicio != null && !dataInicio.trim().isEmpty()) {
                alunoAtualizado.setDataCadastro(LocalDate.parse(dataInicio));
            }

            // 2. LÓGICA DA FOTO (Hierarquia: Remover > Nova > Manter Atual)
            if (removerFoto) {
                // Caso o instrutor marcou para remover a foto do aluno
                alunoAtualizado.setFotoPerfil(null);
                
                // Opcional: deletar o arquivo físico para economizar espaço
                if (alunoExistente.getFotoPerfil() != null) {
                    Path antigo = Paths.get(uploadBaseDir, "perfis", alunoExistente.getFotoPerfil());
                    Files.deleteIfExists(antigo);
                }
            } else if (file != null && !file.isEmpty()) {
                // Processo de Upload de nova foto
                String ct = file.getContentType();
                long max = 2L * 1024 * 1024;
                if (ct == null || (!ct.equals("image/webp") && !ct.equals("image/jpeg") && !ct.equals("image/png"))) {
                    ra.addFlashAttribute("msgErro", "Formato inválido. Use WebP, JPEG ou PNG.");
                    return "redirect:/instrutor/alunos/perfil/" + cpf;
                }

                Path uploadDir = Paths.get(uploadBaseDir, "perfis");
                Files.createDirectories(uploadDir);
                
                String original = file.getOriginalFilename();
                String ext = (original != null && original.lastIndexOf('.') != -1) ? original.substring(original.lastIndexOf('.')) : ".jpg";
                String novoNome = alunoAtualizado.getCpf() + ext.toLowerCase();
                
                Files.copy(file.getInputStream(), uploadDir.resolve(novoNome), StandardCopyOption.REPLACE_EXISTING);
                alunoAtualizado.setFotoPerfil(novoNome);
            } else {
                // SE O CAMPO ESTIVER VAZIO: Mantém a foto que já existia no banco
                alunoAtualizado.setFotoPerfil(alunoExistente.getFotoPerfil());
            }

            alunoService.atualizarAluno(cpf, alunoAtualizado);
            ra.addFlashAttribute("msgSucesso", "Aluno atualizado com sucesso.");
            return "redirect:/instrutor/alunos/perfil/" + cpf;

        } catch (Exception e) {
            ra.addFlashAttribute("msgErro", "Erro ao atualizar aluno: " + e.getMessage());
            return "redirect:/instrutor/alunos/perfil/" + cpf;
        }
    }
    // =====================
    // Gestão de Instrutores
    // =====================

    @GetMapping("/instrutores")
    public String instrutores(@RequestParam(name = "q", required = false) String q,
                              Model model,
                              HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        // Somente administradores podem acessar
        if (instrutor == null || !instrutor.isAdmin()) {
            return "redirect:/instrutor/dashboard";
        }
        model.addAttribute("instrutor", instrutor);

        java.util.List<Instrutor> instrutores = instrutorService.listarTodosMenos(instrutor.getCpf());
        if (q != null && !q.trim().isEmpty()) {
            String termo = q.trim().toLowerCase();
            instrutores = instrutores.stream()
                    .filter(i -> i.getNome() != null && i.getNome().toLowerCase().contains(termo))
                    .toList();
            model.addAttribute("q", q.trim());
        } else {
            model.addAttribute("q", "");
        }
        model.addAttribute("instrutores", instrutores);
        return "instrutor/instrutores-instrutor";
    }

    @PostMapping("/instrutores/cadastrar")
    public String cadastrarInstrutor(@RequestParam String cpf,
                                     @RequestParam String nome,
                                     @RequestParam String email,
                                     @RequestParam(required = false) String telefone,
                                     @RequestParam String senha,
                                     @RequestParam(required = false, name = "dataNascimento") String dataNascimento,
                                     @RequestParam(required = false, name = "admin") Boolean admin,
                                     HttpSession session,
                                     RedirectAttributes ra) {
        Instrutor atual = (Instrutor) session.getAttribute("instrutor");
        if (atual == null || !atual.isAdmin()) {
            return "redirect:/instrutor/dashboard";
        }
        try {
            String cpfDigits = cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
            if (cpfDigits.length() != 11) {
                ra.addFlashAttribute("msgErro", "CPF inválido. Informe 11 dígitos.");
                return "redirect:/instrutor/instrutores";
            }
            Instrutor novo = new Instrutor();
            novo.setCpf(cpfDigits);
            novo.setNome(nome);
            novo.setEmail(email);
            if (telefone != null && !telefone.trim().isEmpty()) {
                String telDigits = telefone.replaceAll("[^0-9]", "");
                if (telDigits.length() != 11) {
                    ra.addFlashAttribute("msgErro", "Telefone inválido. Use 11 dígitos (DDD + número).");
                    return "redirect:/instrutor/instrutores";
                }
                novo.setTelefone(telDigits);
            }
            novo.setSenha(senha);
            // Data de nascimento informada no cadastro
            if (dataNascimento != null && !dataNascimento.trim().isEmpty()) {
                novo.setDataNascimento(java.time.LocalDate.parse(dataNascimento));
            }
            // Registrar data de cadastro no sistema
            if (novo.getDataCadastro() == null) {
                novo.setDataCadastro(java.time.LocalDate.now());
            }
            novo.setAdmin(Boolean.TRUE.equals(admin));
            instrutorService.cadastrarInstrutor(novo);
            ra.addFlashAttribute("msgSucesso", "Instrutor cadastrado com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/instrutores";
    }

    @PostMapping("/instrutores/editar")
    public String editarInstrutor(@RequestParam String cpf,
                                @RequestParam String nome,
                                @RequestParam String email,
                                @RequestParam(required = false) String telefone,
                                @RequestParam(required = false, name = "dataInicio") String dataInicio,
                                @RequestParam(required = false, name = "dataNascimento") String dataNascimento,
                                @RequestParam(required = false, name = "admin") Boolean admin,
                                @RequestParam(value = "file", required = false) MultipartFile file, // NOVO
                                @RequestParam(value = "removerFoto", required = false) boolean removerFoto, // NOVO
                                HttpSession session,
                                RedirectAttributes ra) {
        String cpfLimpo = cpf.replaceAll("\\D", "");
        try {
            // 1. Busca o instrutor existente para não perder foto nem senha
            Instrutor instrutorExistente = instrutorService.buscarPorCpf(cpfLimpo)
                    .orElseThrow(() -> new Exception("Instrutor não encontrado"));

            // 2. Monta o objeto de atualização
            Instrutor atualizado = new Instrutor();
            atualizado.setCpf(cpfLimpo);
            atualizado.setNome(nome);
            atualizado.setEmail(email);
            atualizado.setSenha(instrutorExistente.getSenha()); // Preserva senha

            if (telefone != null) {
                atualizado.setTelefone(telefone.replaceAll("\\D", ""));
            }
            
            if (dataInicio != null && !dataInicio.isEmpty()) {
                atualizado.setDataCadastro(java.time.LocalDate.parse(dataInicio));
                atualizado.setDiaQueComecouTrabalhar(java.time.LocalDate.parse(dataInicio));
            }

            if (dataNascimento != null && !dataNascimento.isEmpty()) {
                atualizado.setDataNascimento(java.time.LocalDate.parse(dataNascimento));
            }

            atualizado.setAdmin(Boolean.TRUE.equals(admin));

            // 3. LÓGICA DA FOTO (IDÊNTICA À QUE FUNCIONA NO SEU PERFIL)
            if (removerFoto) {
                atualizado.setFotoPerfil(null);
                // Opcional: deletar arquivo físico aqui
            } else if (file != null && !file.isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/perfis/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

                String extensao = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                String novoNome = cpfLimpo + extensao;
                Files.copy(file.getInputStream(), uploadPath.resolve(novoNome), StandardCopyOption.REPLACE_EXISTING);
                atualizado.setFotoPerfil(novoNome);
            } else {
                // Se nada foi feito, mantém a foto que já estava no banco
                atualizado.setFotoPerfil(instrutorExistente.getFotoPerfil());
            }

            // 4. Salva no banco
            instrutorService.atualizarInstrutor(cpfLimpo, atualizado);
            
            ra.addFlashAttribute("msgSucesso", "Instrutor atualizado com sucesso.");
        } catch (Exception ex) {
            ra.addFlashAttribute("msgErro", "Erro ao atualizar: " + ex.getMessage());
        }
        if (cpfLimpo == null || cpfLimpo.isEmpty()) {
            ra.addFlashAttribute("msgErro", "CPF inválido para redirecionamento.");
            return "redirect:/instrutor/instrutores";
        }
        return "redirect:/instrutor/instrutores/perfil/" + cpfLimpo;
    }

    // Página dedicada para edição de perfil de instrutor (abre em nova aba)
    @GetMapping("/instrutores/editar/{cpf}")
    public String editarInstrutorPage(@PathVariable String cpf,
                                      Model model,
                                      HttpSession session,
                                      RedirectAttributes ra) {
        Instrutor atual = (Instrutor) session.getAttribute("instrutor");
        if (atual == null) {
            atual = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        // Somente admin
        if (atual == null || !atual.isAdmin()) {
            ra.addFlashAttribute("msgErro", "Acesso negado: somente administradores podem editar instrutores.");
            return "redirect:/instrutor/instrutores";
        }
        model.addAttribute("instrutor", atual);

        Instrutor alvo = instrutorService.buscarPorCpf(cpf).orElse(null);
        if (alvo == null) {
            ra.addFlashAttribute("msgErro", "Instrutor não encontrado.");
            return "redirect:/instrutor/instrutores";
        }
        model.addAttribute("instrutorPerfil", alvo);
        return "instrutor/editar-instrutor";
    }

    @PostMapping("/instrutores/excluir/{cpf}")
    public String excluirInstrutor(@PathVariable String cpf,
                                   HttpSession session,
                                   RedirectAttributes ra) {
        Instrutor atual = (Instrutor) session.getAttribute("instrutor");
        if (atual == null || !atual.isAdmin()) {
            return "redirect:/instrutor/dashboard";
        }
        if (atual.getCpf().equals(cpf)) {
            ra.addFlashAttribute("msgErro", "Você não pode excluir a si mesmo.");
            return "redirect:/instrutor/instrutores";
        }
        try {
            instrutorService.deletarInstrutor(cpf);
            ra.addFlashAttribute("msgSucesso", "Instrutor excluído com sucesso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("msgErro", ex.getMessage());
        }
        return "redirect:/instrutor/instrutores";
    }

    // Perfil de um instrutor (somente admin acessa)
    @GetMapping("/instrutores/perfil/{cpf}")
    public String perfilInstrutor(@PathVariable String cpf,
                                  Model model,
                                  HttpSession session) {
        Instrutor atual = (Instrutor) session.getAttribute("instrutor");
        if (atual == null) {
            atual = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        if (atual == null || !atual.isAdmin()) {
            return "redirect:/instrutor/dashboard";
        }
        model.addAttribute("instrutor", atual);

        Instrutor alvo = instrutorService.buscarPorCpf(cpf).orElse(null);
        if (alvo == null) {
            return "redirect:/instrutor/instrutores";
        }
        model.addAttribute("instrutorPerfil", alvo);
        model.addAttribute("turmas", turmaService.listarPorInstrutor(alvo.getCpf()));
        model.addAttribute("planosInstrutor", planoTreinoService.listarPorInstrutor(alvo.getCpf()));
        return "instrutor/perfil-instrutor";
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
        model.addAttribute("inicioAluno", aluno.getDataCadastro());

        // --- ORDENAÇÃO DOS PLANOS: Mais recente primeiro ---
        java.util.List<PlanoTreino> planos = planoTreinoService.listarHistorico(aluno.getCpf());
        if (planos != null) {
            planos.sort((p1, p2) -> p2.getDataCriacao().compareTo(p1.getDataCriacao()));
        }
        model.addAttribute("planos", planos);

        // --- ORDENAÇÃO DAS AVALIAÇÕES: Mais recente primeiro ---
        // Buscamos todas as avaliações (ou as últimas 5)
        var avaliacoes = avaliacaoService.listarPorAluno(aluno.getCpf()); 
        if (avaliacoes != null) {
            // Ordena pela data: a maior data (mais recente) fica no topo
            avaliacoes.sort((a1, a2) -> a2.getData().compareTo(a1.getData()));
        }
        model.addAttribute("avaliacoes", avaliacoes);
        
        return "instrutor/perfil-aluno";
    }
        // Página Meu Perfil do Instrutor (instrutor logado)
    @GetMapping("/meu_perfil")
    public String meuPerfil(Model model, HttpSession session) {
        Instrutor instrutor = (Instrutor) session.getAttribute("instrutor");
        if (instrutor == null) {
            instrutor = instrutorService.listarTodos().isEmpty() ? null : instrutorService.listarTodos().get(0);
        }
        model.addAttribute("instrutor", instrutor);
        return "instrutor/meu-perfil";
    }
}