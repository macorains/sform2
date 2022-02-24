# --- !Ups
CREATE TABLE `d_transfer_detail_log` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `postdata_id` INT(11),
  `transfer_type_id` INT(11),
  `status` INT(11),
  `postdata` JSON,
  `modified_postdata` JSON,
  `result` INT(11),
  `message` JSON,
  `created` DATETIME DEFAULT NULL,
  `modified` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
DROP TABLE `d_transfer_detail_log`
