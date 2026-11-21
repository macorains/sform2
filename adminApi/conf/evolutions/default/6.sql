# --- !Ups
CREATE TABLE d_form_transfer_task_smtp_mail (
  id bigint NOT NULL AUTO_INCREMENT,
  form_transfer_task_id bigint NOT NULL,
  subject varchar(255) NOT NULL DEFAULT '',
  body text NOT NULL,
  to_address varchar(255),
  to_address_field varchar(255),
  cc_address varchar(255),
  bcc_address varchar(255),
  user_group varchar(255) NOT NULL,
  created_user varchar(255) NOT NULL,
  modified_user varchar(255) NOT NULL,
  created datetime NOT NULL,
  modified datetime NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_fttsmtp_task FOREIGN KEY (form_transfer_task_id) REFERENCES d_form_transfer_task (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

# --- !Downs
DROP TABLE IF EXISTS d_form_transfer_task_smtp_mail;
