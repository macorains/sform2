# --- !Ups
ALTER TABLE `sform`.`m_userinfo`
DROP COLUMN `provider_key`,
DROP COLUMN `provider_id`,
ADD COLUMN `username` VARCHAR(50) NOT NULL AFTER `user_id`,
ADD COLUMN `password` VARCHAR(255) NOT NULL AFTER `username`;
DROP TABLE users;

# --- !Downs
ALTER TABLE `sform`.`m_userinfo`
ADD COLUMN `provider_key` VARCHAR(50) NULL AFTER `user_id`,
ADD COLUMN `provider_id` VARCHAR(50) NULL AFTER `provider_key`,
DROP COLUMN `username`,
DROP COLUMN `password`;
CREATE TABLE users
(
  id varchar(255),
  username varchar(255),
  password varchar(255),
  linkedid varchar(255),
  serializedprofile varchar(10000)
);

