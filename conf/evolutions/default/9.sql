# --- !Ups
CREATE TABLE `d_transfer_log` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `transfer_id` INT(11),
  `transfer_type_id` INT(11),
  `status` INT(11),
  `transfer_data` JSON,
  `result_data` JSON,
  `created` DATETIME DEFAULT NULL,
  `modified` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
drop table d_transfer_log


