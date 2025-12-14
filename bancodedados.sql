-- Arquivo: bancodedados.sql
-- Uso: mysql -u root -padmin < bancodedados.sql
-- Objetivo: cria o banco, as tabelas e insere dados iniciais; inclui rotina opcional
--           para ajustar collation para utf8mb4_0900_ai_ci (MySQL 8)

-- Garante que o cliente MySQL interprete corretamente caracteres acentuados
SET NAMES utf8mb4;
SET character_set_client = utf8mb4;
SET character_set_connection = utf8mb4;

-- Desabilita checagem de chaves estrangeiras durante import (opcional)
SET FOREIGN_KEY_CHECKS = 0;

-- -----------------------------------------------------
-- Seção 1: criação do banco, tabelas e inserts (corrigido)
-- -----------------------------------------------------
DROP DATABASE IF EXISTS academia_db;
CREATE DATABASE IF NOT EXISTS academia_db
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
USE academia_db;

-- Tabela Pessoa (dados comuns)
CREATE TABLE pessoa (
  cpf CHAR(11) NOT NULL,
  nome VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  telefone VARCHAR(20),
  senha VARCHAR(255),
  data_nascimento DATE,
  endereco VARCHAR(255),
  usuario VARCHAR(45),
  PRIMARY KEY (cpf),
  UNIQUE KEY UK_Pessoa_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela Aluno (herda de Pessoa)
CREATE TABLE aluno (
  cpf CHAR(11) NOT NULL,
  objetivo VARCHAR(255),
  PRIMARY KEY (cpf),
  CONSTRAINT FK_aluno_pessoa FOREIGN KEY (cpf) REFERENCES pessoa(cpf) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela Instrutor (herda de Pessoa)
CREATE TABLE instrutor (
  cpf CHAR(11) NOT NULL,
  dia_que_comecou_trabalhar DATE,
  PRIMARY KEY (cpf),
  CONSTRAINT FK_instrutor_pessoa FOREIGN KEY (cpf) REFERENCES pessoa(cpf) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela Exercicio
CREATE TABLE exercicio (
  id_exercicio BIGINT NOT NULL AUTO_INCREMENT,
  nome VARCHAR(255) NOT NULL,
  equipamento VARCHAR(255),
  grupo_muscular VARCHAR(45),
  instrucoes TEXT,
  descricao TEXT,
  PRIMARY KEY (id_exercicio)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela Turma
CREATE TABLE turma (
  id_turma BIGINT NOT NULL AUTO_INCREMENT,
  cpf_instrutor CHAR(11),
  titulo VARCHAR(255),
  descricao VARCHAR(255),
  data_da_aula DATE,
  hora_aula TIME,
  vagas INT,
  PRIMARY KEY (id_turma),
  INDEX IDX_turma_instrutor (cpf_instrutor),
  CONSTRAINT FK_turma_instrutor FOREIGN KEY (cpf_instrutor) REFERENCES instrutor(cpf) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela AlunoTurma (join)
CREATE TABLE aluno_turma (
  cpf_aluno CHAR(11) NOT NULL,
  id_turma BIGINT NOT NULL,
  PRIMARY KEY (cpf_aluno, id_turma),
  INDEX IDX_aluno_turma_aluno (cpf_aluno),
  INDEX IDX_aluno_turma_turma (id_turma),
  CONSTRAINT FK_aluno_turma_aluno FOREIGN KEY (cpf_aluno) REFERENCES aluno(cpf) ON DELETE CASCADE,
  CONSTRAINT FK_aluno_turma_turma FOREIGN KEY (id_turma) REFERENCES turma(id_turma) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela AvaliacaoFisica
CREATE TABLE avaliacao_fisica (
  id_avaliacao BIGINT NOT NULL AUTO_INCREMENT,
  cpf_aluno CHAR(11),
  cpf_instrutor CHAR(11),
  data DATE,
  peso DECIMAL(10,2),
  altura DECIMAL(10,2),
  imc DECIMAL(10,2),
  peito DECIMAL(10,2),
  cintura DECIMAL(10,2),
  quadril DECIMAL(10,2),
  biceps_direito DECIMAL(10,2),
  biceps_esquerdo DECIMAL(10,2),
  coxa_direita DECIMAL(10,2),
  coxa_esquerda DECIMAL(10,2),
  panturrilha_direita DECIMAL(10,2),
  panturrilha_esquerda DECIMAL(10,2),
  observacoes VARCHAR(500),
  PRIMARY KEY (id_avaliacao),
  INDEX IDX_avaliacao_aluno (cpf_aluno),
  INDEX IDX_avaliacao_instrutor (cpf_instrutor),
  CONSTRAINT FK_avaliacao_aluno FOREIGN KEY (cpf_aluno) REFERENCES aluno(cpf) ON DELETE SET NULL,
  CONSTRAINT FK_avaliacao_instrutor FOREIGN KEY (cpf_instrutor) REFERENCES instrutor(cpf) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela PlanoDeTreino
CREATE TABLE plano_de_treino (
  id_plano BIGINT NOT NULL AUTO_INCREMENT,
  cpf_aluno CHAR(11),
  cpf_instrutor CHAR(11),
  data_criacao DATE,
  tempo TIME,
  nome VARCHAR(255),
  observacoes VARCHAR(500),
  PRIMARY KEY (id_plano),
  INDEX IDX_plano_aluno (cpf_aluno),
  INDEX IDX_plano_instrutor (cpf_instrutor),
  CONSTRAINT FK_plano_aluno FOREIGN KEY (cpf_aluno) REFERENCES aluno(cpf) ON DELETE SET NULL,
  CONSTRAINT FK_plano_instrutor FOREIGN KEY (cpf_instrutor) REFERENCES instrutor(cpf) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Tabela ExercicioPlano
CREATE TABLE exercicio_plano (
  id BIGINT NOT NULL AUTO_INCREMENT,
  id_exercicio BIGINT,
  id_plano BIGINT,
  series INT,
  repeticoes INT,
  carga DECIMAL(10,2),
  descanso TIME,
  observacoes VARCHAR(500),
  PRIMARY KEY (id),
  INDEX IDX_exercicio_plano_exercicio (id_exercicio),
  INDEX IDX_exercicio_plano_plano (id_plano),
  CONSTRAINT FK_exercicio_plano_exercicio FOREIGN KEY (id_exercicio) REFERENCES exercicio(id_exercicio) ON DELETE CASCADE,
  CONSTRAINT FK_exercicio_plano_plano FOREIGN KEY (id_plano) REFERENCES plano_de_treino(id_plano) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dados iniciais (adaptados)

-- Pessoas (instrutores)
INSERT INTO pessoa (cpf, nome, email, telefone, senha, data_nascimento) VALUES
('11111111111', 'Carlos Silva', 'carlos@academia.com', '11999999999', '123', '1980-01-01'),
('22222222222', 'Ana Costa', 'ana@academia.com', '11988888888', '123', '1985-05-20');

-- Registra como Instrutor
INSERT INTO instrutor (cpf, dia_que_comecou_trabalhar) VALUES
('11111111111', '2020-03-15'),
('22222222222', '2021-06-10');

-- Pessoas (alunos)
INSERT INTO pessoa (cpf, nome, email, telefone, senha, data_nascimento) VALUES
('12345678901', 'João Silva', 'joao@email.com', '11987654321', '123', '1995-03-14'),
('98765432100', 'Maria Santos', 'maria@email.com', '11976543210', '123', '1992-07-22'),
('55555555555', 'Pedro Oliveira', 'pedro@email.com', '11955555555', '123', '1990-11-30');

INSERT INTO aluno (cpf, objetivo) VALUES
('12345678901', 'Hipertrofia'),
('98765432100', 'Emagrecimento'),
('55555555555', 'Condicionamento Físico');

-- Exercícios
INSERT INTO exercicio (id_exercicio, nome, equipamento, grupo_muscular, instrucoes, descricao) VALUES
(1, 'Supino Reto', 'Barra e Banco', 'Peito', 'Deite-se no banco, segure a barra com as mãos afastadas na largura dos ombros. Desça a barra até o peito e empurre para cima.', 'Exercício básico para desenvolvimento do peitoral'),
(2, 'Agachamento Livre', 'Barra', 'Pernas', 'Posicione a barra nos ombros, desça flexionando os joelhos até formar 90 graus e retorne à posição inicial.', 'Exercício fundamental para desenvolvimento das pernas');

-- Turmas
INSERT INTO turma (id_turma, titulo, descricao, data_da_aula, hora_aula, vagas, cpf_instrutor) VALUES
(1, 'Funcional Matinal', 'Treino funcional focado em condicionamento físico e mobilidade', '2024-12-20', '07:00:00', 15, '11111111111'),
(2, 'Spinning', 'Aula de ciclismo indoor com música animada', '2024-12-21', '18:30:00', 20, '22222222222');

-- Matrículas
INSERT INTO aluno_turma (cpf_aluno, id_turma) VALUES
('12345678901', 1),
('12345678901', 2);

-- -----------------------------------------------------
-- Seção 2: rotina opcional para forçar collation utf8mb4_0900_ai_ci
-- (útil se a instalação MySQL usar collations diferentes)
-- -----------------------------------------------------

-- Ajusta collation do banco (reforça para 0900)
ALTER DATABASE academia_db CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

USE academia_db;

-- Converte todas as tabelas do schema para a collation desejada
DELIMITER $$
CREATE PROCEDURE fix_collation()
BEGIN
  DECLARE done INT DEFAULT FALSE;
  DECLARE tbl VARCHAR(255);
  DECLARE cur CURSOR FOR
    SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_SCHEMA = 'academia_db' AND TABLE_TYPE = 'BASE TABLE';
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN cur;
  read_loop: LOOP
    FETCH cur INTO tbl;
    IF done THEN
      LEAVE read_loop;
    END IF;
    SET @s = CONCAT('ALTER TABLE `', 'academia_db', '`.`', tbl, '` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;');
    PREPARE stmt FROM @s;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END LOOP;
  CLOSE cur;
END$$
DELIMITER ;

CALL fix_collation();
DROP PROCEDURE IF EXISTS fix_collation;

-- Verificação rápida (opcional): mostra collation das tabelas
SELECT TABLE_NAME, TABLE_COLLATION FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='academia_db' ORDER BY TABLE_NAME;

-- Reabilita checagem de chaves estrangeiras
SET FOREIGN_KEY_CHECKS = 1;

-- Fim de arquivo
