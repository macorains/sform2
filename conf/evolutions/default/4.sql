# --- !Ups

CREATE TABLE `d_authtoken` (
  `id` VARCHAR(50) NOT NULL,
  `user_id` VARCHAR(50) NOT NULL,
  `expiry` DATETIME
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

# --- !Downs
  drop table d_authtoken
