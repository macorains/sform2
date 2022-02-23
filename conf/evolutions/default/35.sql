# --- !Ups
ALTER TABLE d_transfer_config_salesforce_object_field
CHANGE COLUMN transfer_config_salesforce_field_id transfer_config_salesforce_object_id INT NOT NULL ;

# --- !Downs
ALTER TABLE d_transfer_config_salesforce_object_field
CHANGE COLUMN transfer_config_salesforce_object_id transfer_config_salesforce_field_id INT NOT NULL ;
