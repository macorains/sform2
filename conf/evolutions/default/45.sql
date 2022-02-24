# --- !Ups
ALTER TABLE `m_userinfo`
CHANGE COLUMN `linked_id` `linkedid` VARCHAR(255) NULL DEFAULT NULL ;

# --- !Downs
ALTER TABLE `m_userinfo`
CHANGE COLUMN `linkedid` `linked_id` VARCHAR(255) NULL DEFAULT NULL ;
