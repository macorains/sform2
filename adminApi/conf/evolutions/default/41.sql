# --- !Ups
ALTER TABLE `d_transfer_detail_log`
CHANGE COLUMN `result` `result_code` INT NULL DEFAULT NULL ;

# --- !Downs
ALTER TABLE `d_transfer_detail_log`
CHANGE COLUMN `result_code` `result` INT NULL DEFAULT NULL ;
