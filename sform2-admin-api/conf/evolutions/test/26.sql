# --- !Ups
ALTER TABLE `m_transfers`
DROP COLUMN `config`,
DROP COLUMN `type_id`,
ADD COLUMN `type_code` VARCHAR(45) NOT NULL AFTER id,
CHANGE COLUMN `status` `status` INT NOT NULL ,
CHANGE COLUMN `created` `created` DATETIME NOT NULL ,
CHANGE COLUMN `modified` `modified` DATETIME NOT NULL ;

# --- !Downs
ALTER TABLE `m_transfers`
ADD COLUMN `config` JSON DEFAULT NULL,
ADD COLUMN `type_id` INT DEFAULT NULL,
DROP COLUMN `type_code`,
CHANGE COLUMN `status` `status` INT DEFAULT 1,
CHANGE COLUMN `created` `created` DATETIME DEFAULT NULL,
CHANGE COLUMN `modified` `modified` DATETIME DEFAULT NULL;
