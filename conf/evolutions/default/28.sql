# --- !Ups
CREATE TABLE d_transfer_config (
  id INT NOT NULL AUTO_INCREMENT,
  type_code VARCHAR(45) NOT NULL,
  config_index INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  status INT NOT NULL,
  user_group VARCHAR(30) NOT NULL,
  created_user VARCHAR(45) NOT NULL,
  modified_user VARCHAR(45) NOT NULL,
  created DATETIME NOT NULL,
  modified DATETIME NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_user_group (user_group ASC));

CREATE TABLE d_form_transfer_task (
  id INT NOT NULL,
  transfer_config_id INT NOT NULL,
  form_id INT NOT NULL,
  task_index INT NOT NULL,
  user_group VARCHAR(30) NOT NULL,
  created_user VARCHAR(45) NOT NULL,
  modified_user VARCHAR(45) NOT NULL,
  created DATETIME NOT NULL,
  modified DATETIME NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_form_id (form_id ASC));
  
CREATE TABLE d_form_transfer_task_condition (
  id INT NOT NULL,
  form_transfer_task_id INT NOT NULL,
  form_id INT NOT NULL,
  form_col_id INT NOT NULL,
  operator VARCHAR(10) NOT NULL,
  cond_value VARCHAR(255) NOT NULL,
  user_group VARCHAR(30) NOT NULL,
  created_user VARCHAR(45) NOT NULL,
  modified_user VARCHAR(45) NOT NULL,
  created DATETIME NOT NULL,
  modified DATETIME NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_form_transfer_task_id (form_transfer_task_id ASC));

# --- !Downs
DROP TABLE d_transfer_config;
DROP TABLE d_form_transfer_task;
DROP TABLE d_form_transfer_task_condition;