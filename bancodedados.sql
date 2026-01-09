CREATE DATABASE  IF NOT EXISTS `academia_db` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `academia_db`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: academia_db
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `aluno`
--

DROP TABLE IF EXISTS `aluno`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aluno` (
  `cpf` char(11) NOT NULL,
  `objetivo` varchar(255) DEFAULT NULL,
  `foto_perfil` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cpf`),
  CONSTRAINT `FK_aluno_pessoa` FOREIGN KEY (`cpf`) REFERENCES `pessoa` (`cpf`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aluno`
--

LOCK TABLES `aluno` WRITE;
/*!40000 ALTER TABLE `aluno` DISABLE KEYS */;
INSERT INTO `aluno` VALUES ('02673548900','Perder peso',NULL),('02784570000','ganhar massa',NULL),('08897510064','ficar definido',NULL),('12345678901','ganhar massa','12345678901.png'),('12345685295','Condicionamento Físico e Saúde',NULL),('23545678980','Performance Esportiva',NULL),('55555555555','Condicionamento Físico','55555555555.png'),('98765432100','Emagrecimento',NULL);
/*!40000 ALTER TABLE `aluno` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `aluno_turma`
--

DROP TABLE IF EXISTS `aluno_turma`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aluno_turma` (
  `cpf_aluno` char(11) NOT NULL,
  `id_turma` bigint NOT NULL,
  PRIMARY KEY (`cpf_aluno`,`id_turma`),
  KEY `IDX_aluno_turma_aluno` (`cpf_aluno`),
  KEY `IDX_aluno_turma_turma` (`id_turma`),
  CONSTRAINT `FK_aluno_turma_aluno` FOREIGN KEY (`cpf_aluno`) REFERENCES `aluno` (`cpf`) ON DELETE CASCADE,
  CONSTRAINT `FK_aluno_turma_turma` FOREIGN KEY (`id_turma`) REFERENCES `turma` (`id_turma`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aluno_turma`
--

LOCK TABLES `aluno_turma` WRITE;
/*!40000 ALTER TABLE `aluno_turma` DISABLE KEYS */;
INSERT INTO `aluno_turma` VALUES ('12345678901',1),('02784570000',2),('12345678901',2),('12345678901',5),('02673548900',7),('02784570000',7),('12345678901',7),('12345678901',8),('02784570000',13),('08897510064',13),('12345678901',13),('12345685295',13),('23545678980',13);
/*!40000 ALTER TABLE `aluno_turma` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `avaliacao_fisica`
--

DROP TABLE IF EXISTS `avaliacao_fisica`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `avaliacao_fisica` (
  `id_avaliacao` bigint NOT NULL AUTO_INCREMENT,
  `cpf_aluno` char(11) DEFAULT NULL,
  `cpf_instrutor` char(11) DEFAULT NULL,
  `data` date DEFAULT NULL,
  `peso` double DEFAULT NULL,
  `altura` double DEFAULT NULL,
  `imc` double DEFAULT NULL,
  `peito` double DEFAULT NULL,
  `cintura` double DEFAULT NULL,
  `quadril` double DEFAULT NULL,
  `biceps_direito` double DEFAULT NULL,
  `biceps_esquerdo` double DEFAULT NULL,
  `coxa_direita` double DEFAULT NULL,
  `coxa_esquerda` double DEFAULT NULL,
  `panturrilha_direita` double DEFAULT NULL,
  `panturrilha_esquerda` double DEFAULT NULL,
  `observacoes` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id_avaliacao`),
  KEY `IDX_avaliacao_aluno` (`cpf_aluno`),
  KEY `IDX_avaliacao_instrutor` (`cpf_instrutor`),
  CONSTRAINT `FK_avaliacao_aluno` FOREIGN KEY (`cpf_aluno`) REFERENCES `aluno` (`cpf`) ON DELETE SET NULL,
  CONSTRAINT `FK_avaliacao_instrutor` FOREIGN KEY (`cpf_instrutor`) REFERENCES `instrutor` (`cpf`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avaliacao_fisica`
--

LOCK TABLES `avaliacao_fisica` WRITE;
/*!40000 ALTER TABLE `avaliacao_fisica` DISABLE KEYS */;
INSERT INTO `avaliacao_fisica` VALUES (1,'12345678901','11111111111','2025-12-14',88,1.76,28.40909090909091,120,85,77,88,77,65,54,87,57,'testando o campo de observações'),(2,'02673548900','11111111111','2025-12-16',75.5,1.75,24.653061224489797,100,85,95,36,35,58,57,40,39,'Texto de observação'),(4,'12345678901','11111111111','2025-12-17',95,1.74,31.37798916633637,100,85,95,40,40,60,60,40,40,''),(5,'55555555555','22222222222','2025-12-17',80,1.9,22.1606648199446,100,90,95,40,40,60,60,45,45,''),(6,'98765432100','11111111111','2025-12-25',60,1.6,23.437499999999996,90,65,92,25,25,50,50,32,32,''),(7,'02784570000','11111111111','2025-12-28',58,1.65,21.30394857667585,80,70,80,32,32,50,50,38,38,'avaliação de teste');
/*!40000 ALTER TABLE `avaliacao_fisica` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercicio`
--

DROP TABLE IF EXISTS `exercicio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercicio` (
  `id_exercicio` bigint NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `equipamento` varchar(255) DEFAULT NULL,
  `grupo_muscular` varchar(60) DEFAULT NULL,
  `instrucoes` text,
  `descricao` text,
  PRIMARY KEY (`id_exercicio`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercicio`
--

LOCK TABLES `exercicio` WRITE;
/*!40000 ALTER TABLE `exercicio` DISABLE KEYS */;
INSERT INTO `exercicio` VALUES (1,'Supino Reto','Barra e Banco.','Peito','Deite-se no banco, segure a barra com as mãos afastadas na largura dos ombros. Desça a barra até o peito e empurre para cima.','Exercício básico para desenvolvimento do peitoral.\r\n'),(2,'Agachamento Livre','Barra','Pernas','Posicione a barra nos ombros, desça flexionando os joelhos até formar 90 graus e retorne à posição inicial.','Exercício fundamental para desenvolvimento das pernas'),(3,'Agachamento Livre com Barra','Rack de Agachamento e Barra','Pernas (Quadríceps e Glúteos) ','Em pé, segure a barra na altura da parte superior do peito com as mãos um pouco além da largura dos ombros. Empurre a barra para cima da cabeça até os braços esticarem totalmente, mantendo o abdômen e glúteos bem contraídos para não curvar as costas.','O clássico teste de força de empurrar para a parte superior do corpo. Diferente do supino, este exige muito mais estabilização de todo o tronco por ser feito em pé.'),(4,'Remada Curvada','Barra','Dorsais (costas), romboides e bíceps.','Incline o tronco à frente, mantenha as costas retas e puxe a barra em direção ao abdômen, retraindo as escápulas.','Excelente para construir densidade nas costas e melhorar a postura.'),(5,'Levantamento Terra','Barra e Anilhas.','Costas (lombar), Glúteos, Posteriores de coxa e Trapézio.','Pés sob a barra, coluna reta. Segure a barra e suba estendendo quadril e joelhos. Mantenha a barra rente às pernas.','Exercício completo para força e cadeia posterior. Inicia com a carga no solo, exigindo estabilidade do core e potência.');
/*!40000 ALTER TABLE `exercicio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exercicio_plano`
--

DROP TABLE IF EXISTS `exercicio_plano`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exercicio_plano` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `id_exercicio` bigint DEFAULT NULL,
  `id_plano` bigint DEFAULT NULL,
  `series` int DEFAULT NULL,
  `repeticoes` int DEFAULT NULL,
  `carga` double DEFAULT NULL,
  `descanso` decimal(21,0) DEFAULT NULL,
  `observacoes` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_exercicio_plano_exercicio` (`id_exercicio`),
  KEY `IDX_exercicio_plano_plano` (`id_plano`),
  CONSTRAINT `FK_exercicio_plano_exercicio` FOREIGN KEY (`id_exercicio`) REFERENCES `exercicio` (`id_exercicio`) ON DELETE CASCADE,
  CONSTRAINT `FK_exercicio_plano_plano` FOREIGN KEY (`id_plano`) REFERENCES `plano_de_treino` (`id_plano`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercicio_plano`
--

LOCK TABLES `exercicio_plano` WRITE;
/*!40000 ALTER TABLE `exercicio_plano` DISABLE KEYS */;
INSERT INTO `exercicio_plano` VALUES (92,2,6,5,12,NULL,NULL,NULL),(93,1,6,1,10,NULL,NULL,NULL),(105,2,7,5,12,NULL,NULL,NULL),(106,2,7,2,12,NULL,NULL,NULL),(107,2,4,5,12,NULL,NULL,NULL),(108,2,4,2,12,NULL,NULL,NULL),(109,4,8,5,12,NULL,NULL,NULL),(110,1,9,5,2,5,NULL,NULL);
/*!40000 ALTER TABLE `exercicio_plano` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `instrutor`
--

DROP TABLE IF EXISTS `instrutor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `instrutor` (
  `cpf` char(11) NOT NULL,
  `dia_que_comecou_trabalhar` date DEFAULT NULL,
  `is_admin` tinyint(1) NOT NULL DEFAULT '0',
  `foto_perfil` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cpf`),
  CONSTRAINT `FK_instrutor_pessoa` FOREIGN KEY (`cpf`) REFERENCES `pessoa` (`cpf`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instrutor`
--

LOCK TABLES `instrutor` WRITE;
/*!40000 ALTER TABLE `instrutor` DISABLE KEYS */;
INSERT INTO `instrutor` VALUES ('11111111111',NULL,1,'11111111111.png'),('22222222222','2024-10-12',0,'22222222222.jpg'),('33333333333',NULL,1,NULL);
/*!40000 ALTER TABLE `instrutor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pessoa`
--

DROP TABLE IF EXISTS `pessoa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pessoa` (
  `cpf` char(11) NOT NULL,
  `nome` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `telefone` varchar(20) DEFAULT NULL,
  `senha` varchar(255) DEFAULT NULL,
  `data_nascimento` date DEFAULT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `usuario` varchar(45) DEFAULT NULL,
  `data_inicio` date DEFAULT NULL,
  PRIMARY KEY (`cpf`),
  UNIQUE KEY `UK_Pessoa_email` (`email`),
  CONSTRAINT `chk_cpf_format` CHECK (regexp_like(`cpf`,_utf8mb4'^[0-9]{11}$'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pessoa`
--

LOCK TABLES `pessoa` WRITE;
/*!40000 ALTER TABLE `pessoa` DISABLE KEYS */;
INSERT INTO `pessoa` VALUES ('02673548900','Aluno Teste 05','alunoteste05@gmail.com','51998758232','B00,.','2000-04-16',NULL,NULL,'2025-11-16'),('02784570000','Fabricio Bartz','fabricio@aluno.com','51996568624','B123,.','1994-08-16',NULL,NULL,'2024-12-27'),('08897510064','Arthur Alves','arthur@aluno.com','53994755625','123','2001-09-22',NULL,NULL,'2025-12-27'),('11111111111','Carlos Silva','carlos@academia.com','11999999994','123','1990-02-23',NULL,NULL,'2020-09-15'),('12345678901','João Silva','joao@email.com','11987654321','123','1995-03-14',NULL,NULL,'2025-12-14'),('12345685295','Artur Gruppelli','gruppelli@aluno.com','53998789612','123','1998-03-29',NULL,NULL,'2026-01-08'),('22222222222','Ana Costa','ana@academia.com','11988888883','123','1985-05-20',NULL,NULL,'2024-10-12'),('23545678980','Thiago Mazzoni','mazzoni@aluno.com','51996183594','123','2000-07-09',NULL,NULL,'2026-01-08'),('33333333333','Valtair Souza','valtair@academia.com','53998169734','B123,.','1994-02-23',NULL,NULL,'2024-12-27'),('55555555555','Pedro Oliveira','pedro@email.com','11955555555','123','1990-11-30',NULL,NULL,'2025-12-17'),('98765432100','Maria Santos','maria@email.com','11976543210','123','1992-07-22',NULL,NULL,'2025-12-25');
/*!40000 ALTER TABLE `pessoa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plano_de_treino`
--

DROP TABLE IF EXISTS `plano_de_treino`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plano_de_treino` (
  `id_plano` bigint NOT NULL AUTO_INCREMENT,
  `cpf_aluno` char(11) DEFAULT NULL,
  `cpf_instrutor` char(11) DEFAULT NULL,
  `data_criacao` date DEFAULT NULL,
  `tempo` time DEFAULT NULL,
  `nome` varchar(255) DEFAULT NULL,
  `observacoes` varchar(500) DEFAULT NULL,
  `dias_semana` varchar(50) DEFAULT NULL,
  `data_termino` date DEFAULT NULL,
  PRIMARY KEY (`id_plano`),
  KEY `IDX_plano_aluno` (`cpf_aluno`),
  KEY `IDX_plano_instrutor` (`cpf_instrutor`),
  CONSTRAINT `FK_plano_aluno` FOREIGN KEY (`cpf_aluno`) REFERENCES `aluno` (`cpf`) ON DELETE SET NULL,
  CONSTRAINT `FK_plano_instrutor` FOREIGN KEY (`cpf_instrutor`) REFERENCES `instrutor` (`cpf`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plano_de_treino`
--

LOCK TABLES `plano_de_treino` WRITE;
/*!40000 ALTER TABLE `plano_de_treino` DISABLE KEYS */;
INSERT INTO `plano_de_treino` VALUES (4,'12345678901','11111111111','2025-12-16',NULL,'treino do joao','teste para ver se esta funcionando o campo observação','QUA,QUI,SEX','2026-01-06'),(6,'02673548900','11111111111','2025-12-16',NULL,'plano de treino teste','teste para ver se esta funcionando o campo observação','TER,QUA,SAB,DOM',NULL),(7,'12345678901','11111111111','2025-12-17',NULL,'treino de teste','teste para ver se esta funcionando o campo observação','SEG,TER,QUA,QUI,SEX,SAB,DOM','2026-03-20'),(8,'02784570000','11111111111','2025-12-28',NULL,'treino do Fabricio','teste para ver se esta funcionando o campo observação','TER,QUA,QUI,SEX','2026-03-20'),(9,'23545678980','22222222222','2026-01-08',NULL,'treino do Mazzoni','Descanso de 60s entre as séries.','SEG,TER,QUA,QUI,SEX','2026-04-05');
/*!40000 ALTER TABLE `plano_de_treino` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `turma`
--

DROP TABLE IF EXISTS `turma`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `turma` (
  `id_turma` bigint NOT NULL AUTO_INCREMENT,
  `cpf_instrutor` char(11) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `data_da_aula` date DEFAULT NULL,
  `hora_aula` time DEFAULT NULL,
  `vagas` int DEFAULT NULL,
  PRIMARY KEY (`id_turma`),
  KEY `IDX_turma_instrutor` (`cpf_instrutor`),
  CONSTRAINT `FK_turma_instrutor` FOREIGN KEY (`cpf_instrutor`) REFERENCES `instrutor` (`cpf`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turma`
--

LOCK TABLES `turma` WRITE;
/*!40000 ALTER TABLE `turma` DISABLE KEYS */;
INSERT INTO `turma` VALUES (1,'11111111111','Funcional Matinal','Treino funcional focado em condicionamento físico e mobilidade.','2025-12-20','07:00:00',3),(2,'22222222222','Spinning','Aula de ciclismo indoor com música animada','2025-12-21','18:30:00',20),(5,'11111111111','turma do Carlos','abc','2025-12-17','10:00:00',19),(7,'22222222222','Natação para iniciantes','natação','2026-01-17','10:00:00',5),(8,'11111111111','Yoga','Fortaleça o corpo e acalme a mente. Nesta turma, exploramos o Yoga de forma dinâmica, focando no alinhamento corporal, ganho de mobilidade e fortalecimento muscular consciente.','2026-01-10','10:00:00',10),(13,'33333333333','Circuito Funcional de Alta Intensidade','Treino dinâmico que combina movimentos compostos com barra e exercícios de peso corporal. Foco em queima calórica, resistência cardiovascular e agilidade.','2026-04-15','13:30:00',5);
/*!40000 ALTER TABLE `turma` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-01-09  0:02:03
