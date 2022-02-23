# --- !Ups
DROP TABLE d_transfer_tasks;

# --- !Downs
CREATE TABLE d_transfer_tasks (
  id INT NOT NULL AUTO_INCREMENT,
  transfer_type_id INT NOT NULL,
  name VARCHAR(128) CHARACTER SET utf8 NOT NULL,
  status INT NOT NULL DEFAULT '0',
  form_id VARCHAR(128) CHARACTER SET utf8 GENERATED ALWAYS AS (json_unquote(json_extract(CONFIG,_utf8mb4'$.formId'))) VIRTUAL,
  config JSON DEFAULT NULL,
  user_group VARCHAR(30) CHARACTER SET utf8 DEFAULT NULL,
  created_user VARCHAR(45) CHARACTER SET utf8 DEFAULT NULL,
  created DATETIME DEFAULT NULL,
  modified_user VARCHAR(45) CHARACTER SET utf8 DEFAULT NULL,
  modified DATETIME DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin