-- Script: mysql_fix_collation.sql
-- Alinha collation do banco e converte todas as tabelas para utf8mb4_0900_ai_ci (MySQL 8 default)
-- Uso: mysql -u root -padmin < mysql_fix_collation.sql

-- Ajusta collation do banco
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
