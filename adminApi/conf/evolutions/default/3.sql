# --- !Ups
ALTER TABLE d_transfer_config_mail_address
  DROP INDEX idx_transfer_config_mail_id,
  DROP COLUMN transfer_config_mail_id,
  ADD COLUMN transfer_config_id bigint NOT NULL;

CREATE INDEX idx_transfer_config_id
  ON d_transfer_config_mail_address (transfer_config_id);

# --- !Downs
ALTER TABLE d_transfer_config_mail_address
  DROP INDEX idx_transfer_config_id,
  DROP COLUMN transfer_config_id,
  ADD COLUMN transfer_config_mail_id bigint NOT NULL;

CREATE INDEX idx_transfer_config_mail_id
  ON d_transfer_config_mail_address (transfer_config_mail_id);
