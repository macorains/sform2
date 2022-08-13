# --- !Ups
ALTER TABLE `d_transfer_tasks`
ADD `form_id` INTEGER GENERATED ALWAYS AS (JSON_UNQUOTE(JSON_EXTRACT(`CONFIG`,'$.formId'))) VIRTUAL AFTER STATUS;

# --- !Downs
ALTER TABLE `d_transfer_tasks` DROP `form_id`;
