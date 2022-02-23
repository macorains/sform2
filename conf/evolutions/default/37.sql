# --- !Ups
ALTER TABLE d_form_transfer_task
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT;

ALTER TABLE d_form_transfer_task_condition
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT;

ALTER TABLE d_form_transfer_task_mail
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT;

ALTER TABLE d_form_transfer_task_salesforce
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT;

ALTER TABLE d_form_transfer_task_salesforce_field
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT;

# --- !Downs
ALTER TABLE d_form_transfer_task
CHANGE COLUMN id id INT NOT NULL;

ALTER TABLE d_form_transfer_task_condition
CHANGE COLUMN id id INT NOT NULL;

ALTER TABLE d_form_transfer_task_mail
CHANGE COLUMN id id INT NOT NULL;

ALTER TABLE d_form_transfer_task_salesforce
CHANGE COLUMN id id INT NOT NULL;

ALTER TABLE d_form_transfer_task_salesforce_field
CHANGE COLUMN id id INT NOT NULL;
