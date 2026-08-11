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

## Passo a Passo para Rodar o Projeto

### 1. Suba o Banco de Dados MySQL
Você precisa ter o MySQL 8+ instalado e rodando localmente. O banco padrão é `academia_db`.

#### Como criar e preparar o banco:
1. Abra o MySQL Workbench, DBeaver ou o terminal MySQL.
2. Execute o comando para criar o banco:
  ```sql
  CREATE DATABASE IF NOT EXISTS academia_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```
3. Importe o arquivo [bancodedados.sql](bancodedados.sql) para popular as tabelas e dados iniciais:
  - No Workbench/DBeaver: clique em "Importar" e selecione o arquivo.
  - No terminal:
    ```bash
    mysql -u root -p academia_db < bancodedados.sql
    ```
  - Ajuste usuário/senha conforme necessário (padrão: root/admin).

### 2. Configure o acesso ao banco
Verifique se as credenciais do banco estão corretas em [src/main/resources/application.properties](src/main/resources/application.properties):
```
spring.datasource.url=jdbc:mysql://localhost:3306/academia_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=admin
```

### 3. Compile e rode o projeto
No terminal, execute:
```bash
mvn clean package
mvn -e spring-boot:run
```
Ou rode o JAR gerado:
```bash
java -jar target/academia-fatout-1.0.0.jar
```

### 4. Acesse a aplicação
Abra o navegador em: [http://localhost:8081/](http://localhost:8081/)
