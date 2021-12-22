# --- !Ups
ALTER TABLE `sform`.`m_userinfo`
ADD COLUMN `LINKED_ID` VARCHAR(255) NULL DEFAULT NULL AFTER `ID`,
ADD COLUMN `SERIALIZEDPROFILE` VARCHAR(10000) NULL DEFAULT NULL AFTER `AVATAR_URL`,
CHANGE COLUMN `USER_ID` `ID` VARCHAR(50) CHARACTER SET 'utf8' NOT NULL ,
CHANGE COLUMN `USERNAME` `USERNAME` VARCHAR(255) NOT NULL ;

# --- !Downs
ALTER TABLE `sform`.`m_userinfo`
DROP COLUMN `LINKED_ID`
DROP COLUMN `SERIALIZEDPROFILE`
CHANGE COLUMN `ID` `USER_ID` VARCHAR(50) CHARACTER SET 'utf8' NOT NULL ,
CHANGE COLUMN `USERNAME` `USERNAME` VARCHAR(50) NOT NULL ;