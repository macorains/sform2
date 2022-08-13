# --- !Ups

CREATE TABLE `d_postdata` (
  `postdata_id` INT(11) NOT NULL AUTO_INCREMENT,
  `form_hashed_id` VARCHAR(45) NOT NULL,
  `postdata` JSON,
  `created` DATETIME DEFAULT NULL,
  `modified` DATETIME DEFAULT NULL,

  PRIMARY KEY (`postdata_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
DROP TABLE `d_postdata`;
