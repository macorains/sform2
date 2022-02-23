# --- !Ups
ALTER TABLE d_transfer_config_mail
DROP COLUMN body,
DROP COLUMN bcc_address,
DROP COLUMN cc_address,
DROP COLUMN to_address,
DROP COLUMN from_address,
DROP COLUMN reply_to,
DROP COLUMN subject,
ADD COLUMN use_cc TINYINT(1) NOT NULL AFTER transfer_config_id,
ADD COLUMN use_bcc TINYINT(1) NOT NULL AFTER use_cc,
ADD COLUMN use_replyto TINYINT(1) NOT NULL AFTER use_bcc;

CREATE TABLE d_transfer_config_mail_address (
  id INT NOT NULL AUTO_INCREMENT,
  transfer_config_mail_id INT NOT NULL,
  address_index INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  address VARCHAR(255) NOT NULL,
  user_group VARCHAR(30) NOT NULL,
  created_user VARCHAR(45) NOT NULL,
  modified_user VARCHAR(45) NOT NULL,
  created DATETIME NOT NULL,
  modified DATETIME NOT NULL,
  PRIMARY KEY (id),
  INDEX idx_transfer_config_mail_id (transfer_config_mail_id ASC));

# --- !Downs
ALTER TABLE d_transfer_config_mail
ADD COLUMN body TEXT,
ADD COLUMN bcc_address VARCHAR(255) DEFAULT NULL,
ADD COLUMN cc_address VARCHAR(255) DEFAULT NULL,
ADD COLUMN to_address VARCHAR(255) NOT NULL,
ADD COLUMN from_address VARCHAR(255) NOT NULL,
ADD COLUMN reply_to VARCHAR(255) DEFAULT NULL,
ADD COLUMN subject VARCHAR(255) NOT NULL,
DROP COLUMN use_cc,
DROP COLUMN use_bcc,
DROP COLUMN use_replyto;

DROP TABLE D_TRANSFER_CONFIG_MAIL_ADDRESS;
