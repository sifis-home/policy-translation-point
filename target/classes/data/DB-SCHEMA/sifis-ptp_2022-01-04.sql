# ************************************************************
# Sequel Pro SQL dump
# Version 5446
#
# https://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 127.0.0.1 (MySQL 8.0.21)
# Database: sifis-ptp
# Generation Time: 2022-01-04 10:34:50 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table dbaction
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dbaction`;

CREATE TABLE `dbaction` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `rule_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsl5cmjqqs05opu20pba04bha` (`entity_id`),
  KEY `FKd5vfgowbh3hdt1uaba4t7qjwi` (`rule_id`),
  CONSTRAINT `FKd5vfgowbh3hdt1uaba4t7qjwi` FOREIGN KEY (`rule_id`) REFERENCES `dbrule` (`id`),
  CONSTRAINT `FKsl5cmjqqs05opu20pba04bha` FOREIGN KEY (`entity_id`) REFERENCES `dbentity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `dbaction` WRITE;
/*!40000 ALTER TABLE `dbaction` DISABLE KEYS */;

INSERT INTO `dbaction` (`id`, `url`, `entity_id`, `rule_id`)
VALUES
	(7,'http://elite.polito.it/ontologies/sifis-home.owl#sifis_dont_record_video_action',NULL,7),
	(8,'http://elite.polito.it/ontologies/sifis-home.owl#sifis_record_audio_action',NULL,8);

/*!40000 ALTER TABLE `dbaction` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table dbdetail
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dbdetail`;

CREATE TABLE `dbdetail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `type` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `action_id` bigint DEFAULT NULL,
  `trigger_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK98vv5q2ff5eoewks0mbrqt2lw` (`action_id`),
  KEY `FKcfjqa7dgvs79vnnjwwsifyf52` (`trigger_id`),
  CONSTRAINT `FK98vv5q2ff5eoewks0mbrqt2lw` FOREIGN KEY (`action_id`) REFERENCES `dbaction` (`id`),
  CONSTRAINT `FKcfjqa7dgvs79vnnjwwsifyf52` FOREIGN KEY (`trigger_id`) REFERENCES `dbtrigger` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `dbdetail` WRITE;
/*!40000 ALTER TABLE `dbdetail` DISABLE KEYS */;

INSERT INTO `dbdetail` (`id`, `type`, `url`, `value`, `action_id`, `trigger_id`)
VALUES
	(13,'location','http://elite.polito.it/ontologies/sifis-home.owl#sifis_location_detail','Bedroom',7,NULL),
	(14,'value','http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_interval_detail','-',NULL,7),
	(15,'location','http://elite.polito.it/ontologies/sifis-home.owl#sifis_location_detail','Entire Home',8,NULL),
	(16,'value','http://elite.polito.it/ontologies/sifis-home.owl#sifis_hour_interval_detail','-',NULL,8);

/*!40000 ALTER TABLE `dbdetail` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table dbentity
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dbentity`;

CREATE TABLE `dbentity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4j62tn3hvd09b7pa2n7qyvk6u` (`user_id`),
  CONSTRAINT `FK4j62tn3hvd09b7pa2n7qyvk6u` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# Dump of table dbrule
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dbrule`;

CREATE TABLE `dbrule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `timestamp` bigint DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrthas252irlmwocm22hevsmlr` (`user_id`),
  CONSTRAINT `FKrthas252irlmwocm22hevsmlr` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `dbrule` WRITE;
/*!40000 ALTER TABLE `dbrule` DISABLE KEYS */;

INSERT INTO `dbrule` (`id`, `timestamp`, `type`, `user_id`)
VALUES
	(7,1641292379688,'sifis',5),
	(8,1641292396049,'sifis',5);

/*!40000 ALTER TABLE `dbrule` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table dbtrigger
# ------------------------------------------------------------

DROP TABLE IF EXISTS `dbtrigger`;

CREATE TABLE `dbtrigger` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `url` varchar(255) DEFAULT NULL,
  `entity_id` bigint DEFAULT NULL,
  `rule_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5ndxaxil4q8g9v1pt1qhwokr9` (`entity_id`),
  KEY `FK9cacof68w3dr6peeey1n4mxrv` (`rule_id`),
  CONSTRAINT `FK5ndxaxil4q8g9v1pt1qhwokr9` FOREIGN KEY (`entity_id`) REFERENCES `dbentity` (`id`),
  CONSTRAINT `FK9cacof68w3dr6peeey1n4mxrv` FOREIGN KEY (`rule_id`) REFERENCES `dbrule` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `dbtrigger` WRITE;
/*!40000 ALTER TABLE `dbtrigger` DISABLE KEYS */;

INSERT INTO `dbtrigger` (`id`, `url`, `entity_id`, `rule_id`)
VALUES
	(7,'http://elite.polito.it/ontologies/sifis-home.owl#sifis_every_morning_trigger',NULL,7),
	(8,'http://elite.polito.it/ontologies/sifis-home.owl#sifis_every_evening_trigger',NULL,8);

/*!40000 ALTER TABLE `dbtrigger` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table hibernate_sequence
# ------------------------------------------------------------

DROP TABLE IF EXISTS `hibernate_sequence`;

CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;

INSERT INTO `hibernate_sequence` (`next_val`)
VALUES
	(1);

/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `role_id` bigint NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;

INSERT INTO `role` (`role_id`, `role`)
VALUES
	(1,'ADMIN');

/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT,
  `active` int DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `ontology_iri` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_kiqfjabx9puw3p1eg7kily8kg` (`password`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;

INSERT INTO `user` (`user_id`, `active`, `last_name`, `name`, `ontology_iri`, `password`, `username`)
VALUES
	(5,1,'ptp-demo','ptp-demo','http://elite.polito.it/ontologies/sifis-home.owl/ptp-demo','ptp-demo','ptp-demo');

/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table user_role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `user_role`;

CREATE TABLE `user_role` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKa68196081fvovjhkek5m97n3y` (`role_id`),
  CONSTRAINT `FK859n2jvi8ivhui0rl0esws6o` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
  CONSTRAINT `FKa68196081fvovjhkek5m97n3y` FOREIGN KEY (`role_id`) REFERENCES `role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;

INSERT INTO `user_role` (`user_id`, `role_id`)
VALUES
	(5,1);

/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;



/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
