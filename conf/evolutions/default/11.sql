# --- !Ups
CREATE TABLE `d_transfer_tasks` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `transfer_type_id` INT(11) NOT NULL,
  `name` VARCHAR(128) NOT NULL,
  `status` INT(11) NOT NULL DEFAULT 0,
  `config` JSON,
  `created` DATETIME DEFAULT NULL,
  `modified` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
drop table d_transfer_tasks