# --- !Ups
ALTER TABLE `d_form_transfer_task`
ADD COLUMN `name` VARCHAR(255) NULL AFTER `task_index`;

# --- !Downs
ALTER TABLE `d_form_transfer_task`
DROP COLUMN `name`;
