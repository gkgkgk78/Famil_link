-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema famil_link
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema famil_link
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `famil_link` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `famil_link` ;

-- -----------------------------------------------------
-- Table `famil_link`.`account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `famil_link`.`account` (
  `uid` BIGINT NOT NULL AUTO_INCREMENT COMMENT '번호',
  `email` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL COMMENT '이메일',
  `pw` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL COMMENT '비밀번호',
  `address` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL COMMENT '대표주소',
  `phone` VARCHAR(13) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL COMMENT '대표번호',
  `nickname` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NULL COMMENT '가족별명',
  `role` ENUM('ROLE_USER', 'ROLE_ADMIN') CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL DEFAULT 'ROLE_USER' COMMENT '시큐리티',
  `refresh_token` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NULL DEFAULT NULL,
  `sdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',
  `salt` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NULL DEFAULT NULL COMMENT '개인',
  `level` TINYINT NOT NULL DEFAULT '0' COMMENT '권한',
  PRIMARY KEY USING BTREE (`uid`),
  UNIQUE INDEX `email` USING BTREE (`email`) VISIBLE,
  UNIQUE INDEX `phone` (`phone` ASC) VISIBLE,
  UNIQUE INDEX `address` (`address` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
COMMENT = '가족 계정';


-- -----------------------------------------------------
-- Table `famil_link`.`member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `famil_link`.`member` (
  `uid` BIGINT NOT NULL AUTO_INCREMENT,
  `user_uid` BIGINT NOT NULL COMMENT '가족 계정 번호',
  `name` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL COMMENT '이름',
  `sdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',
  `model_path` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL COMMENT '인식 모델 경로',
  `role` ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
  `refresh_token` VARCHAR(200) NULL DEFAULT NULL,
  `salt` VARCHAR(100) NULL DEFAULT NULL,
  `level` TINYINT NOT NULL DEFAULT '0',
  `nickname` VARCHAR(100) NULL,
  PRIMARY KEY (`uid`),
  UNIQUE INDEX `user_uid_name` (`user_uid` ASC, `name` ASC) VISIBLE,
  CONSTRAINT `member_user_uid_FK`
    FOREIGN KEY (`user_uid`)
    REFERENCES `famil_link`.`account` (`uid`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
COMMENT = '가족 구성원';


-- -----------------------------------------------------
-- Table `famil_link`.`movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `famil_link`.`movie` (
  `uid` BIGINT NOT NULL AUTO_INCREMENT,
  `member_from` BIGINT NOT NULL COMMENT 'from 해당 유저에게',
  `member_to` BIGINT NOT NULL COMMENT 'to 해당 유저가',
  `path` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL COMMENT '경로',
  `sdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '보낸 일시',
  `udate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '받은 일시',
  `status` ENUM('0', '1') CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL DEFAULT '0' COMMENT '읽음 여부',
  `public` ENUM('0', '1') CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_0900_ai_ci' NOT NULL DEFAULT '0' COMMENT '공개 여부',
  PRIMARY KEY (`uid`),
  INDEX `movie_user_uid_FK` USING BTREE (`member_to`) VISIBLE,
  INDEX `movie_member_from_FK` (`member_from` ASC) VISIBLE,
  CONSTRAINT `movie_member_from_FK`
    FOREIGN KEY (`member_from`)
    REFERENCES `famil_link`.`member` (`uid`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `movie_member_to_FK`
    FOREIGN KEY (`member_to`)
    REFERENCES `famil_link`.`member` (`uid`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
AUTO_INCREMENT = 2
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci
COMMENT = '영상';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
