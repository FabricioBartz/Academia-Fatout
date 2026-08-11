-- =====================================================================
-- reset-data.sql
-- Restaura o banco "academia_db" para o estado inicial (dados de seed),
-- apagando tudo o que foi cadastrado pelos visitantes da demo.
--
-- Local no projeto: src/main/resources/db/reset-data.sql
-- Executado por: com.academia.config.DataResetScheduler (via @Scheduled)
--
-- Baseado nos dados originais de bancodedados.sql (dump do projeto).
-- =====================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------
-- 1) Limpa todas as tabelas e reinicia os contadores AUTO_INCREMENT
--    (a ordem aqui não importa, pois as checagens de FK estão desligadas)
-- ---------------------------------------------------------------------
TRUNCATE TABLE aluno_turma;
TRUNCATE TABLE avaliacao_fisica;
TRUNCATE TABLE exercicio_plano;
TRUNCATE TABLE plano_de_treino;
TRUNCATE TABLE turma;
TRUNCATE TABLE exercicio;
TRUNCATE TABLE aluno;
TRUNCATE TABLE instrutor;
TRUNCATE TABLE pessoa;

-- ---------------------------------------------------------------------
-- 2) Reinsere os dados de seed, agora em ordem "pai antes do filho"
--    (aqui a ordem importa, mesmo com FK_CHECKS=0, para manter os dados
--    coerentes entre si)
-- ---------------------------------------------------------------------

-- pessoa (tabela base de Aluno/Instrutor)
INSERT INTO pessoa (cpf, nome, email, telefone, senha, data_nascimento, endereco, usuario, data_inicio) VALUES
('02673548900','Aluno Teste 05','alunoteste05@gmail.com','51998758232','B00,.','2000-04-16',NULL,NULL,'2025-11-16'),
('02784570000','Fabricio Bartz','fabricio@aluno.com','51996568624','B123,.','1994-08-16',NULL,NULL,'2024-12-27'),
('08897510064','Arthur Alves','arthur@aluno.com','53994755625','123','2001-09-22',NULL,NULL,'2025-12-27'),
('11111111111','Carlos Silva','carlos@academia.com','11999999994','123','1990-02-23',NULL,NULL,'2020-09-15'),
('12345678901','João Silva','joao@email.com','11987654321','123','1995-03-14',NULL,NULL,'2025-12-14'),
('12345685295','Artur Gruppelli','gruppelli@aluno.com','53998789612','123','1998-03-29',NULL,NULL,'2026-01-08'),
('22222222222','Ana Costa','ana@academia.com','11988888883','123','1985-05-20',NULL,NULL,'2024-10-12'),
('23545678980','Thiago Mazzoni','mazzoni@aluno.com','51996183594','123','2000-07-09',NULL,NULL,'2026-01-08'),
('33333333333','Valtair Souza','valtair@academia.com','53998169734','B123,.','1994-02-23',NULL,NULL,'2024-12-27'),
('55555555555','Pedro Oliveira','pedro@email.com','11955555555','123','1990-11-30',NULL,NULL,'2025-12-17'),
('98765432100','Maria Santos','maria@email.com','11976543210','123','1992-07-22',NULL,NULL,'2025-12-25');

-- instrutor (depende de pessoa)
INSERT INTO instrutor (cpf, dia_que_comecou_trabalhar, is_admin, foto_perfil) VALUES
('11111111111',NULL,1,'11111111111.png'),
('22222222222','2024-10-12',0,'22222222222.jpg'),
('33333333333',NULL,1,NULL);

-- aluno (depende de pessoa)
INSERT INTO aluno (cpf, objetivo, foto_perfil) VALUES
('02673548900','Perder peso',NULL),
('02784570000','ganhar massa',NULL),
('08897510064','ficar definido',NULL),
('12345678901','ganhar massa','12345678901.png'),
('12345685295','Condicionamento Físico e Saúde',NULL),
('23545678980','Performance Esportiva',NULL),
('55555555555','Condicionamento Físico','55555555555.png'),
('98765432100','Emagrecimento',NULL);

-- exercicio (independente)
INSERT INTO exercicio (id_exercicio, nome, equipamento, grupo_muscular, instrucoes, descricao) VALUES
(1,'Supino Reto','Barra e Banco.','Peito','Deite-se no banco, segure a barra com as mãos afastadas na largura dos ombros. Desça a barra até o peito e empurre para cima.','Exercício básico para desenvolvimento do peitoral.\r\n'),
(2,'Agachamento Livre','Barra','Pernas','Posicione a barra nos ombros, desça flexionando os joelhos até formar 90 graus e retorne à posição inicial.','Exercício fundamental para desenvolvimento das pernas'),
(3,'Agachamento Livre com Barra','Rack de Agachamento e Barra','Pernas (Quadríceps e Glúteos) ','Em pé, segure a barra na altura da parte superior do peito com as mãos um pouco além da largura dos ombros. Empurre a barra para cima da cabeça até os braços esticarem totalmente, mantendo o abdômen e glúteos bem contraídos para não curvar as costas.','O clássico teste de força de empurrar para a parte superior do corpo. Diferente do supino, este exige muito mais estabilização de todo o tronco por ser feito em pé.'),
(4,'Remada Curvada','Barra','Dorsais (costas), romboides e bíceps.','Incline o tronco à frente, mantenha as costas retas e puxe a barra em direção ao abdômen, retraindo as escápulas.','Excelente para construir densidade nas costas e melhorar a postura.'),
(5,'Levantamento Terra','Barra e Anilhas.','Costas (lombar), Glúteos, Posteriores de coxa e Trapézio.','Pés sob a barra, coluna reta. Segure a barra e suba estendendo quadril e joelhos. Mantenha a barra rente às pernas.','Exercício completo para força e cadeia posterior. Inicia com a carga no solo, exigindo estabilidade do core e potência.');

-- turma (depende de instrutor)
INSERT INTO turma (id_turma, cpf_instrutor, titulo, descricao, data_da_aula, hora_aula, vagas) VALUES
(1,'11111111111','Funcional Matinal','Treino funcional focado em condicionamento físico e mobilidade.','2025-12-20','07:00:00',3),
(2,'22222222222','Spinning','Aula de ciclismo indoor com música animada','2025-12-21','18:30:00',20),
(5,'11111111111','turma do Carlos','abc','2025-12-17','10:00:00',19),
(7,'22222222222','Natação para iniciantes','natação','2026-01-17','10:00:00',5),
(8,'11111111111','Yoga','Fortaleça o corpo e acalme a mente. Nesta turma, exploramos o Yoga de forma dinâmica, focando no alinhamento corporal, ganho de mobilidade e fortalecimento muscular consciente.','2026-01-10','10:00:00',10),
(13,'33333333333','Circuito Funcional de Alta Intensidade','Treino dinâmico que combina movimentos compostos com barra e exercícios de peso corporal. Foco em queima calórica, resistência cardiovascular e agilidade.','2026-04-15','13:30:00',5);

-- plano_de_treino (depende de aluno e instrutor)
INSERT INTO plano_de_treino (id_plano, cpf_aluno, cpf_instrutor, data_criacao, tempo, nome, observacoes, dias_semana, data_termino) VALUES
(4,'12345678901','11111111111','2025-12-16',NULL,'treino do joao','teste para ver se esta funcionando o campo observação','QUA,QUI,SEX','2026-01-06'),
(6,'02673548900','11111111111','2025-12-16',NULL,'plano de treino teste','teste para ver se esta funcionando o campo observação','TER,QUA,SAB,DOM',NULL),
(7,'12345678901','11111111111','2025-12-17',NULL,'treino de teste','teste para ver se esta funcionando o campo observação','SEG,TER,QUA,QUI,SEX,SAB,DOM','2026-03-20'),
(8,'02784570000','11111111111','2025-12-28',NULL,'treino do Fabricio','teste para ver se esta funcionando o campo observação','TER,QUA,QUI,SEX','2026-03-20'),
(9,'23545678980','22222222222','2026-01-08',NULL,'treino do Mazzoni','Descanso de 60s entre as séries.','SEG,TER,QUA,QUI,SEX','2026-04-05');

-- exercicio_plano (depende de exercicio e plano_de_treino)
INSERT INTO exercicio_plano (id, id_exercicio, id_plano, series, repeticoes, carga, descanso, observacoes) VALUES
(92,2,6,5,12,NULL,NULL,NULL),
(93,1,6,1,10,NULL,NULL,NULL),
(105,2,7,5,12,NULL,NULL,NULL),
(106,2,7,2,12,NULL,NULL,NULL),
(107,2,4,5,12,NULL,NULL,NULL),
(108,2,4,2,12,NULL,NULL,NULL),
(109,4,8,5,12,NULL,NULL,NULL),
(110,1,9,5,2,5,NULL,NULL);

-- avaliacao_fisica (depende de aluno e instrutor)
INSERT INTO avaliacao_fisica (id_avaliacao, cpf_aluno, cpf_instrutor, data, peso, altura, imc, peito, cintura, quadril, biceps_direito, biceps_esquerdo, coxa_direita, coxa_esquerda, panturrilha_direita, panturrilha_esquerda, observacoes) VALUES
(1,'12345678901','11111111111','2025-12-14',88,1.76,28.40909090909091,120,85,77,88,77,65,54,87,57,'testando o campo de observações'),
(2,'02673548900','11111111111','2025-12-16',75.5,1.75,24.653061224489797,100,85,95,36,35,58,57,40,39,'Texto de observação'),
(4,'12345678901','11111111111','2025-12-17',95,1.74,31.37798916633637,100,85,95,40,40,60,60,40,40,''),
(5,'55555555555','22222222222','2025-12-17',80,1.9,22.1606648199446,100,90,95,40,40,60,60,45,45,''),
(6,'98765432100','11111111111','2025-12-25',60,1.6,23.437499999999996,90,65,92,25,25,50,50,32,32,''),
(7,'02784570000','11111111111','2025-12-28',58,1.65,21.30394857667585,80,70,80,32,32,50,50,38,38,'avaliação de teste');

-- aluno_turma (tabela associativa, depende de aluno e turma)
INSERT INTO aluno_turma (cpf_aluno, id_turma) VALUES
('12345678901',1),
('02784570000',2),
('12345678901',2),
('12345678901',5),
('02673548900',7),
('02784570000',7),
('12345678901',7),
('12345678901',8),
('02784570000',13),
('08897510064',13),
('12345678901',13),
('12345685295',13),
('23545678980',13);

SET FOREIGN_KEY_CHECKS = 1;
