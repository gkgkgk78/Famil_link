-- --------------------------------------------------------
-- 호스트:                          183.97.128.170
-- 서버 버전:                        8.0.31-0ubuntu0.22.04.1 - (Ubuntu)
-- 서버 OS:                        Linux
-- HeidiSQL 버전:                  12.1.0.6537
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- famil_link 데이터베이스 구조 내보내기
CREATE DATABASE IF NOT EXISTS `famil_link` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `famil_link`;

-- 테이블 famil_link.account 구조 내보내기
CREATE TABLE IF NOT EXISTS `account` (
  `uid` bigint NOT NULL AUTO_INCREMENT COMMENT '번호',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '이메일',
  `pw` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '비밀번호',
  `address` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '대표주소',
  `phone` varchar(13) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '대표번호',
  `role` enum('ROLE_USER','ROLE_ADMIN') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'ROLE_USER' COMMENT '시큐리티',
  `refresh_token` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `sdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',
  `salt` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '개인',
  `level` tinyint NOT NULL DEFAULT '0' COMMENT '권한',
  PRIMARY KEY (`uid`) USING BTREE,
  UNIQUE KEY `email` (`email`) USING BTREE,
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `address` (`address`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='가족 계정';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 famil_link.member 구조 내보내기
CREATE TABLE IF NOT EXISTS `member` (
  `uid` bigint NOT NULL AUTO_INCREMENT,
  `user_uid` bigint NOT NULL COMMENT '가족 계정 번호',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '이름',
  `sdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '가입일시',
  `model_path` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '인식 모델 경로',
  PRIMARY KEY (`uid`),
  UNIQUE KEY `user_uid_name` (`user_uid`,`name`),
  CONSTRAINT `member_user_uid_FK` FOREIGN KEY (`user_uid`) REFERENCES `account` (`uid`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='가족 구성원';

-- 내보낼 데이터가 선택되어 있지 않습니다.

-- 테이블 famil_link.movie 구조 내보내기
CREATE TABLE IF NOT EXISTS `movie` (
  `uid` bigint NOT NULL AUTO_INCREMENT,
  `member_from` bigint NOT NULL COMMENT 'from 해당 유저에게',
  `member_to` bigint NOT NULL COMMENT 'to 해당 유저가',
  `path` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '경로',
  `sdate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '보낸 일시',
  `udate` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '받은 일시',
  `status` enum('0','1') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '읽음 여부',
  `public` enum('0','1') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '공개 여부',
  PRIMARY KEY (`uid`),
  KEY `movie_user_uid_FK` (`member_to`) USING BTREE,
  KEY `movie_member_from_FK` (`member_from`),
  CONSTRAINT `movie_member_from_FK` FOREIGN KEY (`member_from`) REFERENCES `member` (`uid`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `movie_member_to_FK` FOREIGN KEY (`member_to`) REFERENCES `member` (`uid`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='영상';

-- 내보낼 데이터가 선택되어 있지 않습니다.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
