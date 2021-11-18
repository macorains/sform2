# --- !Ups
ALTER TABLE `sform`.`m_userinfo`
DROP COLUMN `PROVIDER_KEY`,
DROP COLUMN `PROVIDER_ID`,
ADD COLUMN `USERNAME` VARCHAR(50) NOT NULL AFTER `USER_ID`,
ADD COLUMN `PASSWORD` VARCHAR(255) NOT NULL AFTER `USERNAME`;
DROP TABLE users;

# --- !Downs
ALTER TABLE `sform`.`m_userinfo`
DROP COLUMN `PROVIDER_KEY` VARCHAR(50) NULL AFTER `USER_ID`,
DROP COLUMN `PROVIDER_ID` VARCHAR(50) NULL AFTER `PROVIDER_KEY`,
DROP COLUMN `USERNAME`,
DROP COLUMN `PASSWORD`;
CREATE TABLE users
(
  id varchar(255),
  username varchar(255),
  password varchar(255),
  linkedid varchar(255),
  serializedprofile varchar(10000)
);

