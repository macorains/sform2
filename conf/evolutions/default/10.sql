# --- !Ups
CREATE TABLE `d_salesforce_transfer_log` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `postdata_id` INT(11),
  `postdata` JSON,
  `modified_postdata` JSON,
  `result` INT(11),
  `message` JSON,
  `created` DATETIME DEFAULT NULL,
  `modified` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
drop table d_salesforce_transfer_log