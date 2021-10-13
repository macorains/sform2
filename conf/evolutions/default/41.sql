# --- !Ups
ALTER TABLE `sform`.`d_transfer_detail_log`
CHANGE COLUMN `RESULT` `RESULT_CODE` INT NULL DEFAULT NULL ;

# --- !Downs
ALTER TABLE `sform`.`d_transfer_detail_log`
CHANGE COLUMN `RESULT_CODE` `RESULT` INT NULL DEFAULT NULL ;