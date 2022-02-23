# --- !Ups

CREATE TABLE `m_authinfo` (
  `authinfo_id` INT(11) NOT NULL AUTO_INCREMENT,
  `provider_id` VARCHAR(50) NOT NULL,
  `provider_key` VARCHAR(50) NOT NULL,
  `hasher` VARCHAR(255),
  `password` VARCHAR(255),
  `salt` VARCHAR(255),
  PRIMARY KEY (`authinfo_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

# --- !Downs
      drop table m_authinfo
