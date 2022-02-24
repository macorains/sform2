# --- !Ups
ALTER TABLE m_userinfo
DROP COLUMN `provider_key`,
DROP COLUMN `provider_id`,
ADD COLUMN `username` VARCHAR(50) NOT NULL AFTER `user_id`,
ADD COLUMN `password` VARCHAR(255) NOT NULL AFTER `username`;
DROP TABLE `users`;

# --- !Downs
ALTER TABLE `m_userinfo`
ADD COLUMN `provider_key` VARCHAR(50) NULL AFTER `user_id`,
ADD COLUMN `provider_id` VARCHAR(50) NULL AFTER `provider_key`,
DROP COLUMN `username`,
DROP COLUMN `password`;
CREATE TABLE `users`
(
  `id` VARCHAR(255),
  `username` VARCHAR(255),
  `password` VARCHAR(255),
  `linkedid` VARCHAR(255),
  `serializedprofile` VARCHAR(10000)
);

