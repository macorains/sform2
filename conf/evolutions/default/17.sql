# --- !Ups
ALTER TABLE d_transfer_tasks
CHANGE COLUMN `form_id` `form_id` VARCHAR(128) GENERATED ALWAYS AS (json_unquote(json_extract(`CONFIG`,'$.formId'))) VIRTUAL ;

# --- !Downs
ALTER TABLE D_TRANSFER_TASKS
CHANGE COLUMN `form_id` `form_id` INT GENERATED ALWAYS AS (json_unquote(json_extract(`CONFIG`,'$.formId'))) VIRTUAL ;
