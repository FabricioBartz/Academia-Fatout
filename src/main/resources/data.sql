-- Inicialização automática de dados pelo Spring Boot
-- Observação: não cria tabelas, apenas insere dados
-- As tabelas devem existir (Hibernate com ddl-auto=update ou já criadas)

-- Pessoas (instrutores)
INSERT INTO pessoa (cpf, nome, email, telefone, senha, data_nascimento)
VALUES
('11111111111', 'Carlos Silva', 'carlos@academia.com', '11999999999', '123', '1980-01-01')
ON DUPLICATE KEY UPDATE email=VALUES(email);

INSERT INTO pessoa (cpf, nome, email, telefone, senha, data_nascimento)
VALUES
('22222222222', 'Ana Costa', 'ana@academia.com', '11988888888', '123', '1985-05-20')
ON DUPLICATE KEY UPDATE email=VALUES(email);

-- Instrutores
INSERT INTO instrutor (cpf, dia_que_comecou_trabalhar)
VALUES ('11111111111', '2020-03-15')
ON DUPLICATE KEY UPDATE dia_que_comecou_trabalhar=VALUES(dia_que_comecou_trabalhar);

INSERT INTO instrutor (cpf, dia_que_comecou_trabalhar)
VALUES ('22222222222', '2021-06-10')
ON DUPLICATE KEY UPDATE dia_que_comecou_trabalhar=VALUES(dia_que_comecou_trabalhar);

-- Pessoas (alunos)
INSERT INTO pessoa (cpf, nome, email, telefone, senha, data_nascimento)
VALUES ('12345678901', 'João Silva', 'joao@email.com', '11987654321', '123', '1995-03-14')
ON DUPLICATE KEY UPDATE email=VALUES(email);

INSERT INTO pessoa (cpf, nome, email, telefone, senha, data_nascimento)
VALUES ('98765432100', 'Maria Santos', '11976543210', 'maria@email.com', '123', '1992-07-22')
ON DUPLICATE KEY UPDATE email=VALUES(email);

INSERT INTO pessoa (cpf, nome, email, telefone, senha, data_nascimento)
VALUES ('55555555555', 'Pedro Oliveira', 'pedro@email.com', '11955555555', '123', '1990-11-30')
ON DUPLICATE KEY UPDATE email=VALUES(email);

-- Alunos
INSERT INTO aluno (cpf, objetivo) VALUES ('12345678901', 'Hipertrofia')
ON DUPLICATE KEY UPDATE objetivo=VALUES(objetivo);
INSERT INTO aluno (cpf, objetivo) VALUES ('98765432100', 'Emagrecimento')
ON DUPLICATE KEY UPDATE objetivo=VALUES(objetivo);
INSERT INTO aluno (cpf, objetivo) VALUES ('55555555555', 'Condicionamento Físico')
ON DUPLICATE KEY UPDATE objetivo=VALUES(objetivo);

-- Exercícios
INSERT INTO exercicio (id_exercicio, nome, equipamento, grupo_muscular, instrucoes, descricao)
VALUES (1, 'Supino Reto', 'Barra e Banco', 'Peito', 'Deite-se no banco, segure a barra com as mãos afastadas na largura dos ombros. Desça a barra até o peito e empurre para cima.', 'Exercício básico para desenvolvimento do peitoral')
ON DUPLICATE KEY UPDATE nome=VALUES(nome);
INSERT INTO exercicio (id_exercicio, nome, equipamento, grupo_muscular, instrucoes, descricao)
VALUES (2, 'Agachamento Livre', 'Barra', 'Pernas', 'Posicione a barra nos ombros, desça flexionando os joelhos até formar 90 graus e retorne à posição inicial.', 'Exercício fundamental para desenvolvimento das pernas')
ON DUPLICATE KEY UPDATE nome=VALUES(nome);

-- Turmas
INSERT INTO turma (id_turma, titulo, descricao, data_da_aula, hora_aula, vagas, cpf_instrutor)
VALUES (1, 'Funcional Matinal', 'Treino funcional focado em condicionamento físico e mobilidade', '2024-12-20', '07:00:00', 15, '11111111111')
ON DUPLICATE KEY UPDATE titulo=VALUES(titulo);
INSERT INTO turma (id_turma, titulo, descricao, data_da_aula, hora_aula, vagas, cpf_instrutor)
VALUES (2, 'Spinning', 'Aula de ciclismo indoor com música animada', '2024-12-21', '18:30:00', 20, '22222222222')
ON DUPLICATE KEY UPDATE titulo=VALUES(titulo);

-- Matrículas
INSERT INTO aluno_turma (cpf_aluno, id_turma) VALUES ('12345678901', 1)
ON DUPLICATE KEY UPDATE id_turma=VALUES(id_turma);
INSERT INTO aluno_turma (cpf_aluno, id_turma) VALUES ('12345678901', 2)
ON DUPLICATE KEY UPDATE id_turma=VALUES(id_turma);
