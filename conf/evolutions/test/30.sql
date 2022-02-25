# --- !Ups
DROP TABLE `m_user`;

CREATE TABLE `d_transfer_config_mail` (
`id` INT NOT NULL AUTO_INCREMENT,
`transfer_config_id` INT NOT NULL,
`subject` VARCHAR(255) NOT NULL,
`reply_to` VARCHAR(255) NULL,
`from_address` VARCHAR(255) NOT NULL,
`to_address` VARCHAR(255) NOT NULL,
`cc_address` VARCHAR(255) NULL,
`bcc_address` VARCHAR(255) NULL,
`body` TEXT NULL,
`user_group` VARCHAR(30) NOT NULL,
`created_user` VARCHAR(45) NOT NULL,
`modified_user` VARCHAR(45) NOT NULL,
`created` DATETIME NOT NULL,
`modified` DATETIME NOT NULL,
PRIMARY KEY (`id`),
INDEX `idx_transfer_config_id` (`transfer_config_id` ASC));

CREATE TABLE `d_transfer_config_salesforce` (
`id` INT NOT NULL AUTO_INCREMENT,
`transfer_config_id` INT NOT NULL,
`sf_user_name` VARCHAR(255) NOT NULL,
`sf_password` VARCHAR(255) NOT NULL,
`sf_security_token` VARCHAR(255) NOT NULL,
`user_group` VARCHAR(30) NOT NULL,
`created_user` VARCHAR(45) NOT NULL,
`modified_user` VARCHAR(45) NOT NULL,
`created` DATETIME NOT NULL,
`modified` DATETIME NOT NULL,
PRIMARY KEY (`id`),
INDEX `idx_transfer_config_id` (`transfer_config_id` ASC));


# --- !Downs
CREATE TABLE `m_user` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `uid` VARCHAR(45) CHARACTER SET utf8 NOT NULL,
  `name` VARCHAR(45) CHARACTER SET utf8 DEFAULT NULL,
  `email` VARCHAR(100) CHARACTER SET utf8 NOT NULL,
  `status` INT NOT NULL,
  `password` VARCHAR(128) CHARACTER SET utf8 DEFAULT NULL,
  `created` DATETIME DEFAULT NULL,
  `modified` DATETIME DEFAULT NULL,
  `uuid` VARCHAR(50) CHARACTER SET utf8 DEFAULT NULL,
  `provider_id` VARCHAR(255) CHARACTER SET utf8 DEFAULT NULL,
  `provider_key` VARCHAR(255) CHARACTER SET utf8 DEFAULT NULL,
  `first_name` VARCHAR(30) CHARACTER SET utf8 DEFAULT NULL,
  `last_name` VARCHAR(30) CHARACTER SET utf8 DEFAULT NULL,
  `full_name` VARCHAR(50) CHARACTER SET utf8 DEFAULT NULL,
  `avatar_url` VARCHAR(255) CHARACTER SET utf8 DEFAULT NULL,
  `activated` TINYINT DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
DROP TABLE `d_transfer_config_mail`;
DROP TABLE `d_transfer_config_salesforce`;
