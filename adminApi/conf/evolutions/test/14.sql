# --- !Ups
ALTER TABLE `m_userinfo`
ADD COLUMN `user_group` VARCHAR(30) AFTER `provider_key`;

# --- !Downs
ALTER TABLE `m_userinfo` DROP COLUMN `user_group`;
