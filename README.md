# Academia FatOut

Sistema de gestão para academias, com controle de alunos, instrutores, turmas, planos de treino e avaliações físicas. Interface renderizada no servidor (Spring Boot + Thymeleaf), pensada para uso em desktop.

**[Ver demo](https://academia-fatout.onrender.com/)**

Duas coisas antes de acessar:
- O banco de dados é resetado automaticamente a cada 6 horas, então qualquer cadastro feito na demo é temporário.
- Os dados de exemplo (nomes, CPFs, e-mails) são todos fictícios.

---

## O que o sistema faz

**Instrutores** cadastram e gerenciam alunos, criam turmas com horário e vagas, montam planos de treino com exercícios/séries/repetições, e registram avaliações físicas ao longo do tempo (peso, medidas, IMC).

**Alunos** consultam seu plano de treino, se matriculam em turmas disponíveis e acompanham o histórico das próprias avaliações.

## Sobre o desenvolvimento

Esse projeto nasceu como trabalho em grupo (4 integrantes) para a disciplina de Engenharia de Software na UFPel. Fiquei responsável pela modelagem dos diagramas UML e por boa parte do desenvolvimento em Java/Spring Boot — o controle de alunos, instrutores, matrículas, avaliações físicas e planos de treino.

Depois da entrega, decidi ir além do escopo acadêmico e publicar o projeto como demo real, o que trouxe problemas que a versão de sala de aula nunca tinha me obrigado a resolver.

**Por que Docker?** O Render (onde a demo está hospedada) não tem suporte nativo a Java — só reconhece Node, Python, Go, Ruby, etc. A saída foi escrever um `Dockerfile` multi-stage: uma etapa com Maven só para compilar o `.jar`, e uma segunda etapa, bem mais enxuta, só com o JRE necessário para rodar. Nunca tinha escrito um Dockerfile antes disso.

**O banco caindo sem explicação aparente.** Depois de configurar tudo (Render + MySQL gerenciado na Aiven), a aplicação começou a falhar com `UnknownHostException` — o próprio endereço do banco parou de resolver via DNS. Pelos logs, entendi que o plano gratuito da Aiven desliga automaticamente serviços recém-criados que não tiveram uso "real" nas primeiras 24h — o que fazia sentido, já que eu tinha só rodado o schema inicial e ficado um tempo sem a aplicação de fato bater no banco. Resolvido reativando o serviço manualmente e monitorando o padrão de uso depois.

**Dados sumindo depois de cada deploy.** Fotos de perfil enviadas pelos usuários desapareciam sempre que eu fazia um novo deploy. Causa: o Render usa filesystem efêmero no plano gratuito — cada deploy sobe um container novo, do zero, sem nada que tenha sido gravado em disco durante a execução anterior. Ainda não migrei isso para armazenamento externo (ver "Limitações conhecidas" abaixo).

**Reset automático do banco.** Como a demo aceita cadastro de qualquer visitante, criei uma rotina agendada (`DataResetScheduler`, com `@Scheduled`) que restaura o banco ao estado inicial periodicamente, via script SQL. Resolve dois problemas de uma vez: evita que o banco fique cheio de lixo de teste, e limita o estrago que um usuário mal-intencionado consegue causar.

Usei o GitHub Copilot como apoio de produtividade ao longo do desenvolvimento, mas toda a parte de arquitetura, modelagem de dados, deploy e troubleshooting de produção foi conduzida e entendida por mim — inclusive os problemas acima, que não vieram de nenhum tutorial, apareceram na prática e precisaram ser debugados do zero.

## Stack

- **Backend:** Java 21, Spring Boot 3.4.1, Spring Data JPA / Hibernate, Spring Security
- **Views:** Thymeleaf, Bootstrap, jQuery
- **Banco:** MySQL 8+ (Aiven em produção)
- **Build:** Maven
- **Deploy:** Docker, Render

## Rodando localmente

Pré-requisitos: JDK 21, Maven 3.9+, MySQL 8+ rodando.

```bash
# cria o banco
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS academia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# popula com o schema + dados de exemplo
mysql -u root -p academia_db < bancodedados.sql
```

Confira as credenciais em `src/main/resources/application.properties` (por padrão, usuário `root` / senha `admin` / porta `3306`), depois:

```bash
mvn clean package
mvn spring-boot:run
```

Acesse em `http://localhost:8080/`.

### Com Docker

```bash
docker build -t academia-fatout .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/academia_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=admin \
  academia-fatout
```

## Arquitetura de deploy

```
Render (container Docker)  --JDBC-->  Aiven for MySQL (banco gerenciado)
```

Deploy automático a cada push na branch principal. O profile `demo` (`application-demo.properties`) ativa a rotina de reset e lê as credenciais do banco via variáveis de ambiente — nunca ficam hardcoded no repositório.

## Estrutura do projeto

```
Academia-Fatout/
├── Dockerfile
├── bancodedados.sql
└── src/main/
    ├── java/com/academia/
    │   ├── config/         # Security, Web, DataResetScheduler
    │   ├── controller/
    │   ├── model/           # Entidades JPA
    │   ├── repository/
    │   └── service/
    └── resources/
        ├── templates/        # Views Thymeleaf
        ├── static/
        ├── db/reset-data.sql # Script do reset automático
        ├── application.properties
        └── application-demo.properties
```

## Modelo de dados

`Pessoa` é a entidade base, com `Aluno` e `Instrutor` herdando dela. Um instrutor cria `Turma`s (com vagas e horário) e `PlanoDeTreino`s (compostos por `Exercicio`s, via `ExercicioPlano`); alunos se matriculam em turmas (`AlunoTurma`) e acumulam um histórico de `AvaliacaoFisica` ao longo do tempo.

## Limitações conhecidas

- Upload de foto de perfil usa disco local, que não persiste entre deploys no Render. Próximo passo: migrar para BLOB no banco ou um serviço de object storage.
