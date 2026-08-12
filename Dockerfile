# =====================================================================
# Dockerfile — build e execução do projeto Academia FatOut (Spring Boot)
# Local no projeto: raiz do repositório (mesmo nível do pom.xml)
# =====================================================================

# ---- Etapa 1: build do projeto com Maven + Java 21 ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia primeiro só o pom.xml para aproveitar cache de dependências
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Agora copia o restante do código-fonte e builda o .jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Etapa 2: imagem final, só com o .jar (bem mais leve) ----
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copia o .jar gerado na etapa de build
COPY --from=build /app/target/academia-fatout-1.0.0.jar app.jar

# O Render define a porta via variável de ambiente PORT
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
