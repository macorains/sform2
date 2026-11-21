# --- !Ups
CREATE TABLE d_transfer_config_smtp_mail (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  transfer_config_id BIGINT      NOT NULL,
  smtp_host         VARCHAR(255) NOT NULL,
  smtp_port         INT          NOT NULL,
  smtp_user         VARCHAR(255) NOT NULL,
  smtp_password     VARCHAR(255) NOT NULL,
  iv_smtp_password  VARCHAR(255) NOT NULL,
  user_group        VARCHAR(255) NOT NULL,
  created_user      VARCHAR(255) NOT NULL,
  modified_user     VARCHAR(255) NOT NULL,
  created           DATETIME     NOT NULL,
  modified          DATETIME     NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_smtp_mail_transfer_config
    FOREIGN KEY (transfer_config_id) REFERENCES d_transfer_config(id) ON DELETE CASCADE
);

# --- !Downs
DROP TABLE IF EXISTS d_transfer_config_smtp_mail;
