# --- !Ups
DROP TABLE M_USER;

CREATE TABLE d_transfer_config_mail (
id INT NOT NULL AUTO_INCREMENT,
transfer_config_id INT NOT NULL,
subject VARCHAR(255) NOT NULL,
reply_to VARCHAR(255) NULL,
from_address VARCHAR(255) NOT NULL,
to_address VARCHAR(255) NOT NULL,
cc_address VARCHAR(255) NULL,
bcc_address VARCHAR(255) NULL,
body TEXT NULL,
user_group VARCHAR(30) NOT NULL,
created_user VARCHAR(45) NOT NULL,
modified_user VARCHAR(45) NOT NULL,
created DATETIME NOT NULL,
modified DATETIME NOT NULL,
PRIMARY KEY (id),
INDEX idx_transfer_config_id (transfer_config_id ASC));

CREATE TABLE d_transfer_config_salesforce (
id INT NOT NULL AUTO_INCREMENT,
transfer_config_id INT NOT NULL,
sf_user_name VARCHAR(255) NOT NULL,
sf_password VARCHAR(255) NOT NULL,
sf_security_token VARCHAR(255) NOT NULL,
user_group VARCHAR(30) NOT NULL,
created_user VARCHAR(45) NOT NULL,
modified_user VARCHAR(45) NOT NULL,
created DATETIME NOT NULL,
modified DATETIME NOT NULL,
PRIMARY KEY (id),
INDEX idx_transfer_config_id (transfer_config_id ASC));


# --- !Downs
CREATE TABLE m_user (
  id int NOT NULL AUTO_INCREMENT,
  uid varchar(45) CHARACTER SET utf8 NOT NULL,
  name varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  email varchar(100) CHARACTER SET utf8 NOT NULL,
  status int NOT NULL,
  password varchar(128) CHARACTER SET utf8 DEFAULT NULL,
  created datetime DEFAULT NULL,
  modified datetime DEFAULT NULL,
  uuid varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  provider_id varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  provider_key varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  first_name varchar(30) CHARACTER SET utf8 DEFAULT NULL,
  last_name varchar(30) CHARACTER SET utf8 DEFAULT NULL,
  full_name varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  avatar_url varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  activated tinyint DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
DROP TABLE d_transfer_config_mail;
DROP TABLE d_transfer_config_salesforce;
