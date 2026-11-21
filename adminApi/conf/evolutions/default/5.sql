# --- !Ups
ALTER TABLE d_transfer_config_smtp_mail
  ADD COLUMN from_address VARCHAR(255) NOT NULL DEFAULT '' AFTER smtp_user;

# --- !Downs
ALTER TABLE d_transfer_config_smtp_mail
  DROP COLUMN from_address;
