# --- !Ups

CREATE TABLE `d_form` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hashed_id` VARCHAR(45) NOT NULL,
  `form_data` JSON,
  `created_user` VARCHAR(45),
  `modified_user` VARCHAR(45),
  `created` DATETIME DEFAULT NULL,
  `modified` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

# --- !Downs
  drop table d_form
