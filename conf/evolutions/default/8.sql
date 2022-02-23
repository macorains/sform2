# --- !Ups
CREATE TABLE `m_transfers` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `type_id` INT(11),
  `name` VARCHAR(128) NOT NULL,
  `status` INT(11) DEFAULT 1,
  `config` JSON,
  `created` DATETIME default null,
  `modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
drop table m_transfers

