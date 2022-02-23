# --- !Ups
ALTER TABLE d_transfer_config_salesforce_object
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ;

ALTER TABLE d_transfer_config_salesforce_object_field
CHANGE COLUMN id id INT NOT NULL AUTO_INCREMENT ;

# --- !Downs
ALTER TABLE d_transfer_config_salesforce_object
CHANGE COLUMN id id INT NOT NULL;

ALTER TABLE d_transfer_config_salesforce_object_field
CHANGE COLUMN id id INT NOT NULL;
