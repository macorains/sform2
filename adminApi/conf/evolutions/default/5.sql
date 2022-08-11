# --- !Ups

CREATE TABLE `m_userinfo` (
  `user_id` VARCHAR(50) NOT NULL,
  `provider_id` VARCHAR(50),
  `provider_key` VARCHAR(50),
  `first_name` VARCHAR(30),
  `last_name` VARCHAR(30),
  `full_name` VARCHAR(50),
  `email` VARCHAR(255),
  `avatar_url` VARCHAR(255),
  `activated` TINYINT,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

# --- !Downs
DROP TABLE `m_userinfo`;
