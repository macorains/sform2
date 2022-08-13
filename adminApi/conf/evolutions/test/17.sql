# --- !Ups
ALTER TABLE `d_transfer_tasks`
CHANGE COLUMN `form_id` `form_id` VARCHAR(128) GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(`CONFIG`,'$.formId'))) VIRTUAL ;

# --- !Downs
ALTER TABLE `d_transfer_tasks`
CHANGE COLUMN `form_id` `form_id` INT GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(`CONFIG`,'$.formId'))) VIRTUAL ;
