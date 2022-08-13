# --- !Ups
ALTER TABLE `m_userinfo`
ADD COLUMN `deletable` TINYINT AFTER `activated`

# --- !Downs
ALTER TABLE `m_userinfo` DROP COLUMN `deletable`;
