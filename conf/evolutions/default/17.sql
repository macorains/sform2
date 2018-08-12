# --- !Ups
ALTER TABLE `sform2s_store`.`d_transfer_tasks`
CHANGE COLUMN `FORM_ID` `FORM_ID` VARCHAR(128) GENERATED ALWAYS AS (json_unquote(json_extract(`CONFIG`,'$.formId'))) VIRTUAL ;

# --- !Downs
ALTER TABLE `sform2s_store`.`d_transfer_tasks`
CHANGE COLUMN `FORM_ID` `FORM_ID` INT GENERATED ALWAYS AS (json_unquote(json_extract(`CONFIG`,'$.formId'))) VIRTUAL ;
