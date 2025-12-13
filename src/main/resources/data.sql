-- Inserir instrutores (tabela INSTRUTOR, não PESSOA)
INSERT INTO instrutor (cpf, nome, email, telefone, senha, data_nascimento, dia_que_comecou_trabalhar) 
VALUES 
('11111111111', 'Carlos Silva', 'carlos@academia.com', '11999999999', '123', '1980-01-01', '2020-03-15'),
('22222222222', 'Ana Costa', 'ana@academia.com', '11988888888', '123', '1985-05-20', '2021-06-10');

-- Inserir alunos (tabela ALUNO, não PESSOA)
INSERT INTO aluno (cpf, nome, email, telefone, senha, data_nascimento, objetivo) 
VALUES 
('12345678901', 'João Silva', 'joao@email.com', '11987654321', '123', '1995-03-14', 'Hipertrofia'),
('98765432100', 'Maria Santos', 'maria@email.com', '11976543210', '123', '1992-07-22', 'Emagrecimento'),
('55555555555', 'Pedro Oliveira', 'pedro@email.com', '11955555555', '123', '1990-11-30', 'Condicionamento Físico');

-- Inserir exercícios
INSERT INTO exercicio (id_exercicio, nome, equipamento, grupo_muscular, instrucoes, descricao) 
VALUES 
(1, 'Supino Reto', 'Barra e Banco', 'Peito', 'Deite-se no banco, segure a barra com as mãos afastadas na largura dos ombros. Desça a barra até o peito e empurre para cima.', 'Exercício básico para desenvolvimento do peitoral'),
(2, 'Agachamento Livre', 'Barra', 'Pernas', 'Posicione a barra nos ombros, desça flexionando os joelhos até formar 90 graus e retorne à posição inicial.', 'Exercício fundamental para desenvolvimento das pernas');

-- Inserir turmas
INSERT INTO turma (id_turma, titulo, descricao, data_da_aula, hora_aula, vagas, cpf_instrutor) 
VALUES 
(1, 'Funcional Matinal', 'Treino funcional focado em condicionamento físico e mobilidade', '2024-12-20', '07:00', 15, '11111111111'),
(2, 'Spinning', 'Aula de ciclismo indoor com música animada', '2024-12-21', '18:30', 20, '22222222222');

-- Matricular alunos em turmas
INSERT INTO aluno_turma (cpf_aluno, id_turma) 
VALUES 
('12345678901', 1),
('12345678901', 2);