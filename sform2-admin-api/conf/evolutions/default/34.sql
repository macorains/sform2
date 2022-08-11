# --- !Ups
ALTER TABLE `d_form_transfer_task_mail`
CHANGE COLUMN `transfer_task_id` `form_transfer_task_id` INT NOT NULL ;

ALTER TABLE `d_form_transfer_task_salesforce`
CHANGE COLUMN `transfer_task_id` `form_transfer_task_id` INT NOT NULL ;

ALTER TABLE `d_form_transfer_task_salesforce_field`
CHANGE COLUMN `transfer_task_salesforce_id` `form_transfer_task_salesforce_id` INT NOT NULL ;

# --- !Downs
ALTER TABLE `d_form_transfer_task_mail`
CHANGE COLUMN `form_transfer_task_id` `transfer_task_id` INT NOT NULL ;

ALTER TABLE `d_form_transfer_task_salesforce`
CHANGE COLUMN `form_transfer_task_id` `transfer_task_id` INT NOT NULL ;

ALTER TABLE `d_form_transfer_task_salesforce_field`
CHANGE COLUMN `form_transfer_task_salesforce_id` `transfer_task_salesforce_id` INT NOT NULL ;
