# --- !Ups
ALTER TABLE d_form_col_validation
ADD COLUMN required TINYINT(1) NULL AFTER input_type;

CREATE TABLE d_transfer_config_salesforce_object (
  id INT NOT NULL,
  transfer_config_salesforce_id INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  label VARCHAR(255) NOT NULL,
  active TINYINT(1) NOT NULL,
  user_group VARCHAR(30) NOT NULL,
  created_user VARCHAR(45) NOT NULL,
  modified_user VARCHAR(45) NOT NULL,
  created DATETIME NOT NULL,
  modified DATETIME NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_transfer_config_salesforce_id (transfer_config_salesforce_id ASC));

CREATE TABLE d_transfer_config_salesforce_object_field (
  id INT NOT NULL,
  transfer_config_salesforce_field_id INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  label VARCHAR(255) NOT NULL,
  field_type VARCHAR(30) NOT NULL,
  active TINYINT(1) NOT NULL,
  user_group VARCHAR(30) NOT NULL,
  created_user VARCHAR(45) NOT NULL,
  modified_user VARCHAR(45) NOT NULL,
  created DATETIME NOT NULL,
  modified DATETIME NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_transfer_config_salesforce_object_id (transfer_config_salesforce_field_id ASC));

# --- !Downs
ALTER TABLE d_form_col_validation
DROP COLUMN required;
DROP TABLE d_transfer_config_salesforce_object;
DROP TABLE d_transfer_config_salesforce_object_field;
