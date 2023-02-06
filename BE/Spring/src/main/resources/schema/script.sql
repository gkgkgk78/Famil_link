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
CREATE SCHEMA IF NOT EXISTS `famil_link` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `famil_link` ;

-- -----------------------------------------------------
-- Table `famil_link`.`account`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `famil_link`.`account` (
  `uid` BIGINT NOT NULL AUTO_INCREMENT COMMENT '번호',
  `email` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL COMMENT '이메일',
  `pw` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL COMMENT '비밀번호',
  `address` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL COMMENT '대표주소',
  `phone` VARCHAR(13) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL COMMENT '대표번호',
  `nickname` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL COMMENT '가족별명',
  `role` ENUM('ROLE_USER', 'ROLE_ADMIN') CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL DEFAULT 'ROLE_USER' COMMENT '시큐리티',
  `refresh_token` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL,
  `sdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',
  `salt` VARCHAR(100) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NULL DEFAULT NULL COMMENT '개인',
  `level` TINYINT NOT NULL DEFAULT '0' COMMENT '권한',
  `model_path` VARCHAR(100) NULL DEFAULT NULL,
  `photo_path` VARCHAR(100) NULL DEFAULT NULL,
  PRIMARY KEY USING BTREE (`uid`),
  UNIQUE INDEX `email` USING BTREE (`email`) VISIBLE,
  UNIQUE INDEX `phone` (`phone` ASC) VISIBLE,
  UNIQUE INDEX `address` (`address` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci
COMMENT = '가족 계정';


-- -----------------------------------------------------
-- Table `famil_link`.`member`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `famil_link`.`member` (
  `uid` BIGINT NOT NULL AUTO_INCREMENT,
  `user_uid` BIGINT NOT NULL COMMENT '가족 계정 번호',
  `name` VARCHAR(50) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL COMMENT '이름',
  `sdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',
  `nickname` VARCHAR(45) NULL DEFAULT NULL,
  `role` ENUM('ROLE_USER', 'ROLE_ADMIN') NOT NULL DEFAULT 'ROLE_USER',
  `refresh_token` VARCHAR(200) NULL DEFAULT NULL,
  `salt` VARCHAR(100) NULL DEFAULT NULL,
  `level` TINYINT NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`, `user_uid`),
  UNIQUE INDEX `user_uid_name` (`user_uid` ASC, `name` ASC) VISIBLE,
  CONSTRAINT `member_user_uid_FK`
    FOREIGN KEY (`user_uid`)
    REFERENCES `famil_link`.`account` (`uid`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB
AUTO_INCREMENT = 4
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci
COMMENT = '가족 구성원';


-- -----------------------------------------------------
-- Table `famil_link`.`movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `famil_link`.`movie` (
  `uid` BIGINT NOT NULL AUTO_INCREMENT,
  `member_from` BIGINT NOT NULL COMMENT 'from 해당 유저에게',
  `member_to` BIGINT NOT NULL COMMENT 'to 해당 유저가',
  `path` VARCHAR(200) CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL COMMENT '경로',
  `sdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '보낸 일시',
  `udate` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '받은 일시',
  `status` ENUM('0', '1') CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL DEFAULT '0' COMMENT '읽음 여부',
  `public` ENUM('0', '1') CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci' NOT NULL DEFAULT '0' COMMENT '공개 여부',
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
COLLATE = utf8mb4_general_ci
COMMENT = '영상';


-- -----------------------------------------------------
-- Table `famil_link`.`todo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `famil_link`.`todo` (
  `account_uid` BIGINT NOT NULL,
  `content` VARCHAR(100) NULL DEFAULT NULL,
  `uid` BIGINT NOT NULL AUTO_INCREMENT,
  `sdate` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` ENUM('0', '1') NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`),
  INDEX `fk_table1_account1` (`account_uid` ASC) VISIBLE,
  CONSTRAINT `fk_table1_account1`
    FOREIGN KEY (`account_uid`)
    REFERENCES `famil_link`.`account` (`uid`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci;


-- -----------------------------------------------------
-- Table `famil_link`.`schedule`
-- -----------------------------------------------------
CREATE TABLE `famil_link`.`schedule` (
    `uid` BIGINT NOT NULL AUTO_INCREMENT,
    `account_uid` BIGINT NOT NULL,
    `member_uid` BIGINT NOT NULL,
    `content` LONGTEXT NOT NULL,
    `date` DATE NULL,
    PRIMARY KEY (`uid`),
    INDEX `account_uid_idx` (`account_uid` ASC) VISIBLE,
    INDEX `member_uid_FK_idx` (`member_uid` ASC) VISIBLE,
    CONSTRAINT `account_uid_FK`
    FOREIGN KEY (`account_uid`)
    REFERENCES `famil_link`.`account` (`uid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
    CONSTRAINT `member_uid_FK`
    FOREIGN KEY (`member_uid`)
    REFERENCES `famil_link`.`member` (`uid`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_general_ci
COMMENT = '스케줄';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
