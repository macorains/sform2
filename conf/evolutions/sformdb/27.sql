# --- !Ups
DROP TABLE D_TRANSFER_TASKS;

# --- !Downs
CREATE TABLE D_TRANSFER_TASKS (
  ID int NOT NULL AUTO_INCREMENT,
  TRANSFER_TYPE_ID int NOT NULL,
  NAME varchar(128) CHARACTER SET utf8 NOT NULL,
  STATUS int NOT NULL DEFAULT '0',
  FORM_ID varchar(128) CHARACTER SET utf8 GENERATED ALWAYS AS (json_unquote(json_extract(CONFIG,_utf8mb4'$.formId'))) VIRTUAL,
  CONFIG json DEFAULT NULL,
  USER_GROUP varchar(30) CHARACTER SET utf8 DEFAULT NULL,
  CREATED_USER varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  CREATED datetime DEFAULT NULL,
  MODIFIED_USER varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  MODIFIED datetime DEFAULT NULL,
  PRIMARY KEY (ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin