# --- !Ups
CREATE TABLE `d_form_transfer_task_mail` (
  `id` INT NOT NULL,
  `transfer_task_id` INT NOT NULL,
  `from_address_id` INT NOT NULL,
  `to_address` VARCHAR(255) NOT NULL,
  `cc_address` VARCHAR(255) NULL,
  `bcc_address_id` INT NULL,
  `replyto_address_id` INT NULL,
  `subject` VARCHAR(255) NOT NULL,
  `body` TEXT NOT NULL,
  `user_group` VARCHAR(30) NOT NULL,
  `created_user` VARCHAR(45) NOT NULL,
  `modified_user` VARCHAR(45) NOT NULL,
  `created` DATETIME NOT NULL,
  `modified` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_transfer_task_id` (`transfer_task_id` ASC));

CREATE TABLE `d_form_transfer_task_salesforce` (
  `id` INT NOT NULL,
  `transfer_task_id` INT NOT NULL,
  `object_name` VARCHAR(255) NOT NULL,
  `user_group` VARCHAR(30) NOT NULL,
  `created_user` VARCHAR(45) NOT NULL,
  `modified_user` VARCHAR(45) NOT NULL,
  `created` DATETIME NOT NULL,
  `modified` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_transfer_task_id` (`transfer_task_id` ASC));

CREATE TABLE `d_form_transfer_task_salesforce_field` (
  `id` INT NOT NULL,
  `transfer_task_salesforce_id` INT NOT NULL,
  `form_column_id` VARCHAR(45) NOT NULL,
  `field_name` VARCHAR(255) NULL,
  `user_group` VARCHAR(30) NOT NULL,
  `created_user` VARCHAR(45) NOT NULL,
  `modified_user` VARCHAR(45) NOT NULL,
  `created` DATETIME NOT NULL,
  `modified` DATETIME NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_transfer_task_id` (`transfer_task_salesforce_id` ASC));

# --- !Downs
DROP TABLE `d_form_transfer_task_mail`;
DROP TABLE `d_form_transfer_task_salesforce`;
DROP TABLE `d_form_transfer_task_salesforce_field`;
