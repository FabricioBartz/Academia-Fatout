# Academia FatOut

Sistema web de gestão de academia com interface voltada para Desktop. Permite que **instrutores** gerenciem alunos, turmas, planos de treino e avaliações físicas, enquanto **alunos** acompanham seus treinos, se matriculam em turmas e visualizam sua evolução física.

🔗 **Demo online:** [https://academia-fatout.onrender.com/](#)

> ⚠️ **Sobre a demo pública:** para manter o ambiente de demonstração limpo e seguro, o banco de dados é **restaurado automaticamente para o estado inicial a cada 6 horas**. Qualquer cadastro feito na demo (novos alunos, turmas, planos de treino etc.) é temporário e será apagado no próximo ciclo de reset. Veja a seção [Arquitetura de Deploy](#arquitetura-de-deploy) para mais detalhes.

> ⚠️ **Sobre os dados de exemplo:** todos os nomes, CPFs, e-mails, telefones e demais informações pessoais presentes no banco de dados de demonstração são **fictícios**, gerados apenas para fins de teste e apresentação do projeto. Não correspondem a pessoas reais.


---

## Índice

- [Visão Geral](#visão-geral)
- [Funcionalidades](#funcionalidades)
- [Stack Técnica](#stack-técnica)
- [Arquitetura de Deploy](#arquitetura-de-deploy)
- [Rodando o Projeto Localmente](#rodando-o-projeto-localmente)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Modelo de Dados](#modelo-de-dados)

---

## Visão Geral

O Academia FatOut é uma aplicação full-stack server-side, construída com **Spring Boot** no backend e **Thymeleaf** para renderização das páginas (sem frontend separado em SPA). O sistema modela o dia a dia de uma academia real: cadastro de pessoas (que podem ser alunos e/ou instrutores), organização de turmas com horários e vagas, montagem de planos de treino compostos por exercícios, e acompanhamento de avaliações físicas ao longo do tempo.

O projeto foi construído com foco em aprendizado prático de tecnologias usadas no mercado — Spring Data JPA, Spring Security, deploy containerizado e infraestrutura em nuvem — culminando em uma versão publicada e acessível publicamente como demonstração.

## Funcionalidades

**Para instrutores:**
- Autenticação e controle de acesso (com papel de administrador para gestão de outros instrutores)
- Cadastro e gerenciamento de alunos
- Criação e gerenciamento de turmas (título, descrição, data, horário, vagas)
- Montagem de planos de treino personalizados, com exercícios, séries, repetições e observações
- Registro de avaliações físicas (peso, altura, IMC, medidas corporais) por aluno, ao longo do tempo

**Para alunos:**
- Visualização do próprio perfil e objetivo de treino
- Consulta aos planos de treino atribuídos
- Matrícula em turmas disponíveis
- Acompanhamento do histórico de avaliações físicas

## Stack Técnica

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.4.1 |
| Persistência | Spring Data JPA / Hibernate |
| Banco de dados | MySQL 8+ |
| Segurança | Spring Security (autenticação por sessão + BCrypt) |
| Views | Thymeleaf |
| Build | Maven |
| UI | Bootstrap e jQuery (via WebJars) |
| Deploy | Docker, hospedado no Render |
| Banco em produção | MySQL gerenciado (Aiven, plano Free) |

## Arquitetura de Deploy

A versão de demonstração pública roda em uma arquitetura simples de duas peças, ambas em planos gratuitos:

```
┌───────────────────┐          ┌──────────────────────┐
│   Render (app)    │  --->    │   Aiven for MySQL    │
│  Docker container │  JDBC    │  (banco gerenciado)  │
└───────────────────┘          └──────────────────────┘
```

- **Render** hospeda o container Docker da aplicação Spring Boot, com deploy automático a cada push na branch principal.
- **Aiven for MySQL** hospeda o banco de dados de forma gerenciada e persistente, independente do ciclo de vida do container da aplicação.

### Rotina de reset automático

Como a demo aceita cadastros de qualquer visitante, o projeto implementa uma rotina agendada (`DataResetScheduler`, usando `@Scheduled`) que restaura periodicamente o banco de dados ao seu estado inicial (dados de seed), via script SQL executado com `ScriptUtils`/`JdbcTemplate`. Isso evita que:
- o banco fique sobrecarregado de dados de teste acumulados ao longo do tempo;
- usuários mal-intencionados consigam corromper ou poluir permanentemente os dados da demo.

O intervalo do reset é configurável via propriedade (`app.reset.interval-ms`), e a rotina só é ativada no ambiente de produção (profile `demo`), nunca no ambiente de desenvolvimento local.

> **Nota sobre uploads de imagem:** o armazenamento de fotos de perfil atualmente usa o sistema de arquivos local do servidor, o que é adequado para desenvolvimento, mas não persiste entre deploys no Render (filesystem efêmero). Uma futura melhoria planejada é migrar para armazenamento externo (banco de dados como BLOB ou serviço de object storage).

## Rodando o Projeto Localmente

### Pré-requisitos
- JDK 21 instalado (e `JAVA_HOME` configurado)
- Maven 3.9+ instalado
- MySQL 8+ em execução (local ou remoto)

### 1. Suba o Banco de Dados MySQL

Abra o MySQL Workbench, DBeaver ou o terminal MySQL e crie o banco:
```sql
CREATE DATABASE IF NOT EXISTS academia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Importe o arquivo [`bancodedados.sql`](bancodedados.sql) para popular as tabelas e dados iniciais:
```bash
mysql -u root -p academia_db < bancodedados.sql
```
(ajuste usuário/senha conforme necessário)

### 2. Configure o acesso ao banco

Verifique as credenciais em [`src/main/resources/application.properties`](src/main/resources/application.properties):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/academia_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=admin
```

### 3. Compile e rode o projeto

```bash
mvn clean package
mvn -e spring-boot:run
```
Ou rode o `.jar` gerado:
```bash
java -jar target/academia-fatout-1.0.0.jar
```

### 4. Acesse a aplicação

Abra o navegador em: [http://localhost:8080/](http://localhost:8080/)

### Rodando com Docker (opcional)

O projeto também inclui um `Dockerfile` multi-stage, usado no deploy de produção. Para rodar localmente via Docker:
```bash
docker build -t academia-fatout .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/academia_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=admin \
  academia-fatout
```

## Estrutura do Projeto

```
Academia-Fatout/
├── Dockerfile                          # Build multi-stage para deploy
├── bancodedados.sql                    # Schema + dados de seed
├── pom.xml
└── src/main/
    ├── java/com/academia/
    │   ├── config/                     # Configurações (Security, Web, Scheduler)
    │   ├── controller/                 # Controllers Thymeleaf (Aluno, Instrutor, Main)
    │   ├── model/                      # Entidades JPA (Pessoa, Aluno, Instrutor, etc.)
    │   ├── repository/                 # Repositórios Spring Data JPA
    │   └── service/                    # Regras de negócio
    └── resources/
        ├── templates/                  # Views Thymeleaf (aluno/, instrutor/)
        ├── static/                     # CSS, JS, imagens estáticas
        ├── db/reset-data.sql           # Script de restauração do banco (demo)
        ├── application.properties      # Configuração base (dev local)
        └── application-demo.properties # Configuração de produção (Render + Aiven)
```

## Modelo de Dados

O sistema usa herança de tabela para representar pessoas que podem assumir o papel de aluno e/ou instrutor:

- **`pessoa`** — dados cadastrais base (CPF, nome, e-mail, telefone, etc.)
- **`aluno`** — dados específicos do aluno (objetivo, foto), vinculado a `pessoa`
- **`instrutor`** — dados específicos do instrutor (admin, data de início), vinculado a `pessoa`
- **`turma`** — aulas/turmas oferecidas por um instrutor
- **`aluno_turma`** — matrícula (relação muitos-para-muitos entre aluno e turma)
- **`plano_de_treino`** — plano de treino atribuído a um aluno por um instrutor
- **`exercicio`** — catálogo de exercícios disponíveis
- **`exercicio_plano`** — exercícios que compõem um plano de treino, com séries/repetições/carga
- **`avaliacao_fisica`** — histórico de avaliações físicas do aluno

---

Projeto desenvolvido para fins de aprendizado.
