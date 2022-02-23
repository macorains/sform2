# --- !Ups

ALTER TABLE `m_user` ADD `uuid` VARCHAR(50);
ALTER TABLE `m_user` ADD `provider_id` VARCHAR(255);
ALTER TABLE `m_user` ADD `provider_key` VARCHAR(255);
ALTER TABLE `m_user` ADD `first_name` VARCHAR(30);
ALTER TABLE `m_user` ADD `last_name` VARCHAR(30);
ALTER TABLE `m_user` ADD `full_name` VARCHAR(50);
ALTER TABLE `m_user` ADD `avatar_url` VARCHAR(255);
ALTER TABLE `m_user` ADD `activated` TINYINT;


# --- !Downs

ALTER TABLE `m_user` DROP `uuid`;
ALTER TABLE `m_user` DROP `provider_id`;
ALTER TABLE `m_user` DROP `provider_key`;
ALTER TABLE `m_user` DROP `first_name`;
ALTER TABLE `m_user` DROP `last_name`;
ALTER TABLE `m_user` DROP `full_name`;
ALTER TABLE `m_user` DROP `avatar_url`;
ALTER TABLE `m_user` DROP `activated`;
