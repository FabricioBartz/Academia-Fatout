# Academia Fatout

Aplicação web para gestão de academia, com áreas para aluno e instrutor, telas Thymeleaf e layout responsivo. O projeto utiliza Spring Boot, Spring Data JPA, Spring Security e MySQL.

## Visão Geral
- Backend em Spring Boot 3.2.0 (Java 21) com Maven.
- Views com Thymeleaf em templates separados para perfis de usuário (aluno e instrutor).
- Persistência com Spring Data JPA e banco MySQL.
- Estilos em [src/main/resources/static/css/style.css](src/main/resources/static/css/style.css) (padrão Material 3 neutro personalizado).
- Recursos estáticos (CSS/JS) em [src/main/resources/static](src/main/resources/static) e páginas em [src/main/resources/templates](src/main/resources/templates).

## Stack Técnica
- Java 21
- Spring Boot 3.2.0 (Web, Thymeleaf, Data JPA, Security)
- MySQL 8+
- Maven
- Bootstrap (via WebJars) e jQuery (via WebJars)

## Pré‑requisitos
- JDK 21 instalado (e `JAVA_HOME` configurado)
- Maven 3.9+ instalado
- MySQL 8+ em execução (local ou remoto)

## Configuração
As propriedades padrão estão em [src/main/resources/application.properties](src/main/resources/application.properties).

- Porta padrão: `8081`
- Banco de dados (padrão local):
  - URL: `jdbc:mysql://localhost:3306/academia_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`
  - Usuário: `root`
  - Senha: `admin`
- JPA: `spring.jpa.hibernate.ddl-auto=none` (o schema é gerenciado via SQL)

Você pode sobrescrever via variáveis de ambiente (recomendado em produção):
- `SERVER_PORT` — porta do servidor
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

Exemplos (PowerShell):
```powershell
$env:SERVER_PORT="8081"
$env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/academia_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
$env:SPRING_DATASOURCE_USERNAME="root"
$env:SPRING_DATASOURCE_PASSWORD="admin"
```

## Banco de Dados
- Arquivo SQL do projeto: [bancodedados.sql](bancodedados.sql)
- Como preparar o banco (local):
  1. Crie o banco `academia_db` (se ainda não existir):
     ```sql
     CREATE DATABASE IF NOT EXISTS academia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
     ```
  2. Importe o conteúdo de [bancodedados.sql](bancodedados.sql) no `academia_db`.
  3. Ajuste usuário/senha em [src/main/resources/application.properties](src/main/resources/application.properties) ou via variáveis de ambiente.

Observação: Como `ddl-auto=none`, o Hibernate não cria/atualiza o schema automaticamente. Mantenha o schema com seus scripts SQL.

## Como Rodar (Desenvolvimento)
- Executar com Maven (hot reload das views se configurado):
```bash
mvn spring-boot:run
```

- Fazer build e rodar o JAR:
```bash
mvn clean package
java -jar target/academia-fatout-1.0.0.jar
```

- A aplicação inicia em: http://localhost:8081/

## Estrutura de Pastas (principal)
- Código Java: [src/main/java/com/academia](src/main/java/com/academia)
  - `AcademiaApplication.java` — classe principal Spring Boot
  - `controller/`, `service/`, `repository/`, `model/` — camadas MVC + persistência
- Templates Thymeleaf: [src/main/resources/templates](src/main/resources/templates)
  - Aluno: [src/main/resources/templates/aluno](src/main/resources/templates/aluno)
  - Instrutor: [src/main/resources/templates/instrutor](src/main/resources/templates/instrutor)
  - Exemplos: `login-aluno.html`, `dashboard-instrutor.html`, `turmas-disponiveis.html`, etc.
- Recursos estáticos: [src/main/resources/static](src/main/resources/static)
  - CSS: [src/main/resources/static/css/style.css](src/main/resources/static/css/style.css)
  - JS: [src/main/resources/static/js/confirm-modal.js](src/main/resources/static/js/confirm-modal.js)

## Estilo e UI
- O arquivo [style.css](src/main/resources/static/css/style.css) padroniza botões e componentes com uma estética inspirada no Google Material 3 (tons neutros) e variantes específicas por contexto (aluno/instrutor).
- Páginas trazem componentes responsivos com Bootstrap, e ícones/efeitos de hover configurados em CSS.

## Segurança e Acesso
- O projeto inclui `spring-boot-starter-security`. Telas como `login-aluno.html` e `login-instrutor.html` estão em [src/main/resources/templates](src/main/resources/templates).
- As regras de autenticação/autorização dependem das configurações nos `controllers`/configurações de segurança (ver [src/main/java/com/academia/config](src/main/java/com/academia/config) se aplicável).

## Perfis de Usuário (Views)
- Aluno: páginas como `dashboard-aluno.html`, `perfil-aluno.html`, `turmas-disponiveis.html`, `treino-aluno.html` em [src/main/resources/templates/aluno](src/main/resources/templates/aluno).
- Instrutor: páginas como `dashboard-instrutor.html`, `alunos-instrutor.html`, `exercicios-instrutor.html`, `turmas-instrutor.html` em [src/main/resources/templates/instrutor](src/main/resources/templates/instrutor).

## Build
- Limpar e compilar:
```bash
mvn clean package
```
- Artefato gerado: `target/academia-fatout-1.0.0.jar`

## Dicas de Troubleshooting
- Porta ocupada: ajuste `server.port` em [src/main/resources/application.properties](src/main/resources/application.properties) ou use `SERVER_PORT`.
- Erros de conexão MySQL:
  - Verifique host/porta/credenciais na `SPRING_DATASOURCE_URL`.
  - Garanta `allowPublicKeyRetrieval=true` em ambientes locais com MySQL 8.
  - Confira timezone com `serverTimezone=UTC`.
- Schema não encontrado: importe [bancodedados.sql](bancodedados.sql) e mantenha `ddl-auto=none` alinhado ao seu fluxo de migrações.

## Licença
Defina a licença do projeto (ex.: MIT, Apache-2.0) conforme necessário.

## Créditos
- Spring Boot, Thymeleaf, Spring Data JPA, Spring Security
- Bootstrap e jQuery via WebJars

## Autores 
-Arthur Soares Alves
-Artur Pieper Gruppelli
-Fabricio Fiss Bartz
-Thiago Mazzoni
-Valtair Augusto