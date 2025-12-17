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
  PRIMARY KEY (`cpf`),
  CONSTRAINT `FK_aluno_pessoa` FOREIGN KEY (`cpf`) REFERENCES `pessoa` (`cpf`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aluno`
--

LOCK TABLES `aluno` WRITE;
/*!40000 ALTER TABLE `aluno` DISABLE KEYS */;
INSERT INTO `aluno` VALUES ('02673548900','Perder peso'),('12345678901','ganhar peso'),('55555555555','Condicionamento Físico'),('98765432100','Emagrecimento');
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
INSERT INTO `aluno_turma` VALUES ('12345678901',1),('12345678901',2);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `avaliacao_fisica`
--

LOCK TABLES `avaliacao_fisica` WRITE;
/*!40000 ALTER TABLE `avaliacao_fisica` DISABLE KEYS */;
INSERT INTO `avaliacao_fisica` VALUES (1,'12345678901','11111111111','2025-12-14',88,1.77,28.088991030674453,120,85,77,88,77,65,54,87,57,''),(2,'02673548900','11111111111','2025-12-16',75.5,1.75,24.653061224489797,100,85,95,36,35,58,57,40,39,'Texto de observação');
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
  `grupo_muscular` varchar(45) DEFAULT NULL,
  `instrucoes` text,
  `descricao` text,
  PRIMARY KEY (`id_exercicio`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercicio`
--

LOCK TABLES `exercicio` WRITE;
/*!40000 ALTER TABLE `exercicio` DISABLE KEYS */;
INSERT INTO `exercicio` VALUES (1,'Supino Reto','Barra e Banco','Peito','Deite-se no banco, segure a barra com as mãos afastadas na largura dos ombros. Desça a barra até o peito e empurre para cima.','Exercício básico para desenvolvimento do peitoral.'),(2,'Agachamento Livre','Barra','Pernas','Posicione a barra nos ombros, desça flexionando os joelhos até formar 90 graus e retorne à posição inicial.','Exercício fundamental para desenvolvimento das pernas'),(3,'Agachamento Livre com Barra','Rack de Agachamento e Barra','Pernas (Quadríceps e Glúteos) ','Em pé, segure a barra na altura da parte superior do peito com as mãos um pouco além da largura dos ombros. Empurre a barra para cima da cabeça até os braços esticarem totalmente, mantendo o abdômen e glúteos bem contraídos para não curvar as costas.','O clássico teste de força de empurrar para a parte superior do corpo. Diferente do supino, este exige muito mais estabilização de todo o tronco por ser feito em pé.');
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
) ENGINE=InnoDB AUTO_INCREMENT=81 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exercicio_plano`
--

LOCK TABLES `exercicio_plano` WRITE;
/*!40000 ALTER TABLE `exercicio_plano` DISABLE KEYS */;
INSERT INTO `exercicio_plano` VALUES (65,2,4,5,12,NULL,NULL,NULL),(66,1,4,1,10,NULL,NULL,NULL),(67,1,4,1,10,NULL,NULL,NULL),(68,2,4,2,12,NULL,NULL,NULL),(69,2,5,5,12,NULL,NULL,NULL),(70,1,5,1,10,NULL,NULL,NULL),(71,1,5,1,10,NULL,NULL,NULL),(72,2,5,2,12,NULL,NULL,NULL),(77,2,6,5,12,NULL,NULL,NULL),(78,1,6,1,10,NULL,NULL,NULL),(79,1,6,1,10,NULL,NULL,NULL),(80,2,6,2,12,NULL,NULL,NULL);
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
  PRIMARY KEY (`cpf`),
  CONSTRAINT `FK_instrutor_pessoa` FOREIGN KEY (`cpf`) REFERENCES `pessoa` (`cpf`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `instrutor`
--

LOCK TABLES `instrutor` WRITE;
/*!40000 ALTER TABLE `instrutor` DISABLE KEYS */;
INSERT INTO `instrutor` VALUES ('11111111111','2020-03-15'),('22222222222','2021-06-10');
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
  PRIMARY KEY (`cpf`),
  UNIQUE KEY `UK_Pessoa_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pessoa`
--

LOCK TABLES `pessoa` WRITE;
/*!40000 ALTER TABLE `pessoa` DISABLE KEYS */;
INSERT INTO `pessoa` VALUES ('02673548900','Aluno Teste 05','alunoteste05@gmail.com','51998758231','B00,.','2000-04-16',NULL,NULL),('11111111111','Carlos Silva','carlos@academia.com','11999999999','123','1980-01-01',NULL,NULL),('12345678901','João Silva','joao@email.com','11987654321','123','1995-03-14',NULL,NULL),('22222222222','Ana Costa','ana@academia.com','11988888888','123','1985-05-20',NULL,NULL),('55555555555','Pedro Oliveira','pedro@email.com','11955555555','123','1990-11-30',NULL,NULL),('98765432100','Maria Santos','maria@email.com','11976543210','123','1992-07-22',NULL,NULL);
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
  PRIMARY KEY (`id_plano`),
  KEY `IDX_plano_aluno` (`cpf_aluno`),
  KEY `IDX_plano_instrutor` (`cpf_instrutor`),
  CONSTRAINT `FK_plano_aluno` FOREIGN KEY (`cpf_aluno`) REFERENCES `aluno` (`cpf`) ON DELETE SET NULL,
  CONSTRAINT `FK_plano_instrutor` FOREIGN KEY (`cpf_instrutor`) REFERENCES `instrutor` (`cpf`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plano_de_treino`
--

LOCK TABLES `plano_de_treino` WRITE;
/*!40000 ALTER TABLE `plano_de_treino` DISABLE KEYS */;
INSERT INTO `plano_de_treino` VALUES (4,'12345678901','11111111111','2025-12-16',NULL,'teste final 2','teste para ver se esta funcionando o campo observação','SEG,TER,QUA,QUI,SEX,SAB,DOM'),(5,'12345678901','11111111111','2025-12-16',NULL,'teste final 2','teste para ver se esta funcionando o campo observação','SEG,TER,QUA,QUI,SEX,SAB,DOM'),(6,'02673548900','11111111111','2025-12-16',NULL,'plano de treino teste','teste para ver se esta funcionando o campo observação','TER,QUA');
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `turma`
--

LOCK TABLES `turma` WRITE;
/*!40000 ALTER TABLE `turma` DISABLE KEYS */;
INSERT INTO `turma` VALUES (1,'11111111111','Funcional Matinal','Treino funcional focado em condicionamento físico e mobilidade','2025-12-20','07:00:00',18),(2,'22222222222','Spinning','Aula de ciclismo indoor com música animada','2025-12-21','18:30:00',20),(5,'11111111111','turma do Carlos','abc','2025-12-17','10:00:00',19),(7,'22222222222','Natação para iniciantes','natação','2026-01-17','10:00:00',5);
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

-- Dump completed on 2025-12-16 21:51:36
