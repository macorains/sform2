# --- !Ups
ALTER TABLE d_form
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ;

ALTER TABLE d_form_col
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_id form_id BIGINT NOT NULL ;

ALTER TABLE d_form_col_select
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_col_id form_col_id BIGINT NOT NULL ,
CHANGE COLUMN form_id form_id BIGINT NOT NULL ;

ALTER TABLE d_form_col_validation
CHANGE COLUMN id id BIGINT NOT NULL ,
CHANGE COLUMN form_col_id form_col_id BIGINT NOT NULL ,
CHANGE COLUMN form_id form_id BIGINT NOT NULL ;

ALTER TABLE d_form_transfer_task
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_id transfer_config_id BIGINT NOT NULL ,
CHANGE COLUMN form_id form_id BIGINT NOT NULL ;

ALTER TABLE d_form_transfer_task_condition
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_transfer_task_id form_transfer_task_id BIGINT NOT NULL ,
CHANGE COLUMN form_id form_id BIGINT NOT NULL ,
CHANGE COLUMN form_col_id form_col_id BIGINT NOT NULL ;

ALTER TABLE d_form_transfer_task_mail
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_transfer_task_id form_transfer_task_id BIGINT NOT NULL ,
CHANGE COLUMN from_address_id from_address_id BIGINT NOT NULL ,
CHANGE COLUMN bcc_address_id bcc_address_id BIGINT NULL DEFAULT NULL ,
CHANGE COLUMN replyto_address_id replyto_address_id BIGINT NULL DEFAULT NULL ;

ALTER TABLE d_form_transfer_task_salesforce
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_transfer_task_id form_transfer_task_id BIGINT NOT NULL ;

ALTER TABLE d_form_transfer_task_salesforce_field
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_transfer_task_salesforce_id form_transfer_task_salesforce_id BIGINT NOT NULL ;

ALTER TABLE d_transfer_config
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ;

ALTER TABLE d_transfer_config_mail
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_id transfer_config_id BIGINT NOT NULL ;

ALTER TABLE d_transfer_config_mail_address
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_mail_id transfer_config_mail_id BIGINT NOT NULL ;

ALTER TABLE d_transfer_config_salesforce
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_id transfer_config_id BIGINT NOT NULL ;

ALTER TABLE d_transfer_config_salesforce_object
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_salesforce_id transfer_config_salesforce_id BIGINT NOT NULL ;

ALTER TABLE d_transfer_config_salesforce_object_field
CHANGE COLUMN id id BIGINT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_salesforce_object_id transfer_config_salesforce_object_id BIGINT NOT NULL ;

# --- !Downs
ALTER TABLE d_form
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ;

ALTER TABLE d_form_col
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_id form_id INT NOT NULL ;

ALTER TABLE d_form_col_select
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_col_id form_col_id INT NOT NULL ,
CHANGE COLUMN form_id form_id INT NOT NULL ;

ALTER TABLE d_form_col_validation
CHANGE COLUMN id id INT NOT NULL ,
CHANGE COLUMN form_col_id form_col_id INT NOT NULL ,
CHANGE COLUMN form_id form_id INT NOT NULL ;

ALTER TABLE d_form_transfer_task
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_id transfer_config_id INT NOT NULL ,
CHANGE COLUMN form_id form_id INT NOT NULL ;

ALTER TABLE d_form_transfer_task_condition
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_transfer_task_id form_transfer_task_id INT NOT NULL ,
CHANGE COLUMN form_id form_id INT NOT NULL ,
CHANGE COLUMN form_col_id form_col_id INT NOT NULL ;

ALTER TABLE d_form_transfer_task_mail
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_transfer_task_id form_transfer_task_id INT NOT NULL ,
CHANGE COLUMN from_address_id from_address_id INT NOT NULL ,
CHANGE COLUMN bcc_address_id bcc_address_id INT NULL DEFAULT NULL ,
CHANGE COLUMN replyto_address_id replyto_address_id INT NULL DEFAULT NULL ;

ALTER TABLE d_form_transfer_task_salesforce
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_transfer_task_id form_transfer_task_id INT NOT NULL ;

ALTER TABLE d_form_transfer_task_salesforce_field
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN form_transfer_task_salesforce_id form_transfer_task_salesforce_id INT NOT NULL ;

ALTER TABLE d_transfer_config
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ;

ALTER TABLE d_transfer_config_mail
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_id transfer_config_id INT NOT NULL ;

ALTER TABLE d_transfer_config_mail_address
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_mail_id transfer_config_mail_id INT NOT NULL ;

ALTER TABLE d_transfer_config_salesforce
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_id transfer_config_id INT NOT NULL ;

ALTER TABLE d_transfer_config_salesforce_object
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_salesforce_id transfer_config_salesforce_id INT NOT NULL ;

ALTER TABLE d_transfer_config_salesforce_object_field
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ,
CHANGE COLUMN transfer_config_salesforce_object_id transfer_config_salesforce_object_id INT NOT NULL ;
