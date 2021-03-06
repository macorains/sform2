# --- !Ups
ALTER TABLE D_FORM_COL
CHANGE COLUMN CREATED CREATED DATETIME NOT NULL ,
CHANGE COLUMN MODIFIED MODIFIED DATETIME NOT NULL ;

CREATE TABLE D_FORM_COL_SELECT (
  ID INT NOT NULL AUTO_INCREMENT,
  FORM_COL_ID INT NOT NULL,
  FORM_ID INT NOT NULL,
  SELECT_INDEX INT NOT NULL,
  SELECT_NAME VARCHAR(128) NOT NULL,
  SELECT_VALUE VARCHAR(45) NOT NULL,
  IS_DEFAULT TINYINT(1) NOT NULL,
  EDIT_STYLE VARCHAR(255) NULL,
  VIEW_STYLE VARCHAR(255) NULL,
  USER_GROUP VARCHAR(30) NOT NULL,
  CREATED_USER VARCHAR(45) NOT NULL,
  MODIFIED_USER VARCHAR(45) NOT NULL,
  CREATED DATETIME NOT NULL,
  MODIFIED DATETIME NOT NULL,
  PRIMARY KEY (ID, FORM_COL_ID, FORM_ID),
  INDEX IDX_USER_GROUP (USER_GROUP ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4;

# --- !Downs
DROP TABLE D_FORM_COL_SELECT;

ALTER TABLE D_FORM_COL
CHANGE COLUMN CREATED CREATED VARCHAR(45) NOT NULL ,
CHANGE COLUMN MODIFIED MODIFIED VARCHAR(45) NOT NULL ;
