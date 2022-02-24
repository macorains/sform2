# --- !Ups
ALTER TABLE `m_userinfo`
ADD COLUMN `linked_id` VARCHAR(255) NULL DEFAULT NULL AFTER `id`,
ADD COLUMN `serializedprofile` VARCHAR(10000) NULL DEFAULT NULL AFTER `avatar_url`,
CHANGE COLUMN `user_id` `id` VARCHAR(50) CHARACTER SET 'utf8' NOT NULL ,
CHANGE COLUMN `username` `username` VARCHAR(255) NOT NULL ;

# --- !Downs
ALTER TABLE `m_userinfo`
DROP COLUMN `linked_id`,
DROP COLUMN `serializedprofile`,
CHANGE COLUMN `id` `user_id` VARCHAR(50) CHARACTER SET 'utf8' NOT NULL ,
CHANGE COLUMN `username` `username` VARCHAR(50) NOT NULL ;
